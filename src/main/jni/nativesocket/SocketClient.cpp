/*
 * Copyright (C) 2014 Fastboot Mobile, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "SocketClient.h"
#include "SocketCommon.h"
#include <errno.h>
#include <netdb.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <sys/un.h>
#include <string>
#include <thread>
#include "Log.h"
#include "proto/Plugin.pb.h"

#define SOCKET_BUFFER_SIZE 100000
#define LOG_TAG "NativeSocket-SocketClient"

// -------------------------------------------------------------------------------------
SocketClient::SocketClient(const std::string& socket_name) : m_SocketName(socket_name),
        m_Server(-1), m_pCallback(nullptr) {
    GOOGLE_PROTOBUF_VERIFY_VERSION;
    m_pBuffer = new int8_t[SOCKET_BUFFER_SIZE];
}
// -------------------------------------------------------------------------------------
SocketClient::~SocketClient() {
    if (m_Server >= 0) {
        close(m_Server);
        m_Server = -1;
    }

    if (m_EventThread.joinable()) {
        m_EventThread.join();
    }

    delete[] m_pBuffer;
    google::protobuf::ShutdownProtobufLibrary();
}
// -------------------------------------------------------------------------------------
bool SocketClient::initialize() {
    sockaddr_un addr;
    socklen_t len;

    // Setup socket address
    bzero(&addr, sizeof(addr));
    addr.sun_family = AF_LOCAL;

    // Copy socket name
    addr.sun_path[0] = '\0';
    snprintf(&addr.sun_path[1], sizeof(addr.sun_path), "%s", m_SocketName.c_str());
    len = offsetof(sockaddr_un, sun_path) + 1 + strlen(&addr.sun_path[1]);

    ALOGD("Opening SocketClient socket...");

    // Ensure we stop previous events
    if (m_EventThread.joinable()) {
        m_Server = -1;
        m_EventThread.join();
    }

    // Run the new socket
    m_Server = socket(PF_LOCAL, SOCK_STREAM, 0);

    if (m_Server < 0) {
        ALOGE("Cannot open socket %s: %s", m_SocketName.c_str(), strerror(errno));
        return false;
    }

    ALOGD("Connecting to socket...");
    if (connect(m_Server, reinterpret_cast<sockaddr*>(&addr), len) < 0) {
        ALOGE("Cannot connect to socket: %s", strerror(errno));
        close(m_Server);
        return false;
    }

    ALOGD("Socket connected");

    // Start poll thread
    m_EventThread = std::thread(&SocketClient::processEventsThread, this);
    return true;
}
// -------------------------------------------------------------------------------------
bool SocketClient::writeToSocket(const uint8_t* data, uint32_t len) {
    uint32_t len_left = len;
    uint32_t len_written = 0;

    // Loop until all is sent
    while (len_left > 0) {
        len_written = send(m_Server, &(data[len_written]), len_left, 0);

        if (len_written < 0) {
            // An error occured.
            if (errno == EINTR) {
                // The socket call was interrupted, we can try again
                continue;
            } else {
                // A more dangerous error occurred, bail out
                ALOGE("Error during send(): %s (%d)", strerror(errno), errno);
                perror("write");
                return false;
            }
        } else if (len_written == 0) {
            // Socket is closed
            ALOGE("Socket is closed");
            return false;
        }

        len_left -= len_written;
    }

    return true;
}
// -------------------------------------------------------------------------------------
void SocketClient::processEventsThread() {
    while (m_Server >= 0) {
        if (processEvents() < 0) {
            break;
        }
        usleep(1000);
    }
}
// -------------------------------------------------------------------------------------
int SocketClient::processEvents() {
    int32_t len_read = 0;
    uint32_t total_len_read = 0;

    // Before processing the protobuf structure itself, we first read the message size (4 bytes)
    // and the opcode (one byte), so 5 bytes total
    const uint32_t header_size = 5;

    while (total_len_read < header_size) {
        len_read = recv(m_Server, &m_pBuffer[total_len_read], header_size - total_len_read,
                MSG_WAITALL);

        if (len_read < 0) {
            if (errno == EINTR) {
                // The socket call was interrupted, we can try again
                continue;
            } else {
                // A more dangerous error occurred, bail out
                ALOGE("Error while reading from socket!");
                return -1;
            }
        } else if (len_read == 0) {
            // Socket is closed
            return -1;
        }
        total_len_read += len_read;
    }

    // Message size is protobuf message + 1 byte for the opcode in the header. We substract it
    // so that we only read what's remaining.
    uint32_t message_size = convertBytesToUInt32(&m_pBuffer[0]) - 1;
    MessageType message_type = static_cast<MessageType>(m_pBuffer[4]);

    const uint32_t final_size = header_size + message_size;

    if (final_size > SOCKET_BUFFER_SIZE) {
        ALOGE("FATAL: Message size is larger than socket buffer size!");
    }

    // Now that we have the message size, we can keep on reading the following data
    while (total_len_read < final_size) {
        len_read = recv(m_Server, &m_pBuffer[total_len_read], final_size - total_len_read,
                MSG_WAITALL);

        if (len_read < 0) {
            if (errno == EINTR) {
                // The socket call was interrupted, we can try again
                continue;
            } else {
                // A more dangerous error occurred, bail out
                ALOGE("Error while reading from socket!");
                return -1;
            }
        } else if (len_read == 0) {
            // Socket is closed
            return -1;
        }
        total_len_read += len_read;
    }

    // Convert to protobuf message and broadcast to the callback
    std::string container(reinterpret_cast<const char*>(&m_pBuffer[5]), message_size);

    switch (message_type) {
        case MESSAGE_AUDIO_DATA:
            if (m_pCallback) {
                omnimusic::AudioData message;
                message.ParseFromString(container);

                std::string s_container = message.samples();
                const uint8_t* samples = reinterpret_cast<const uint8_t*>(s_container.c_str());
                const uint32_t num_samples = s_container.size();
                m_pCallback->onAudioData(samples, num_samples);
            }
            break;

        case MESSAGE_AUDIO_RESPONSE:
            if (m_pCallback) {
                omnimusic::AudioResponse message;
                message.ParseFromString(container);
                m_pCallback->onAudioResponse(message.written());
            }
            break;

        case MESSAGE_BUFFER_INFO:
            if (m_pCallback) {
                omnimusic::BufferInfo message;
                message.ParseFromString(container);
                m_pCallback->onBufferInfo(message.samples(), message.stutter());
            }
            break;

        case MESSAGE_FORMAT_INFO:
            if (m_pCallback) {
                omnimusic::FormatInfo message;
                message.ParseFromString(container);
                m_pCallback->onFormatInfo(message.sampling_rate(), message.channels());
            }
            break;

        case MESSAGE_REQUEST:
            if (m_pCallback) {
                omnimusic::Request message;
                message.ParseFromString(container);
                m_pCallback->onRequest(message.request());
            }
            break;

        default:
            ALOGE("Unhandled opcode %d", message_type);
            break;
    }

    return 0;
}
// -------------------------------------------------------------------------------------
void SocketClient::setCallback(SocketCallbacks* callback) {
    m_pCallback = callback;
}
// -------------------------------------------------------------------------------------
bool SocketClient::writeProtoBufMessage(uint8_t opcode, const ::google::protobuf::Message& msg) {
    const int size = msg.ByteSize();
    const int size_with_header = size + 5;
    uint8_t* socket_buffer = reinterpret_cast<uint8_t*>(malloc(size_with_header));

    // header
    convertInt32ToBytes(size + 1, socket_buffer);  // + 1 for opcode byte, see Java
    socket_buffer[4] = opcode;

    // message
    msg.SerializeToArray(&(socket_buffer[5]), size);

    bool result = writeToSocket(socket_buffer, size_with_header);

    free(socket_buffer);

    return result;
}
// -------------------------------------------------------------------------------------
void SocketClient::writeAudioData(const void* data, const uint32_t len) {
    omnimusic::AudioData msg;
    msg.set_samples(data, len);

    writeProtoBufMessage(MESSAGE_AUDIO_DATA, msg);
}
// -------------------------------------------------------------------------------------
void SocketClient::writeFormatInfo(const int channels, const int sample_rate) {
    omnimusic::FormatInfo msg;
    msg.set_channels(channels);
    msg.set_sampling_rate(sample_rate);

    writeProtoBufMessage(MESSAGE_FORMAT_INFO, msg);
}
// -------------------------------------------------------------------------------------
void SocketClient::writeBufferInfo(const int samples, const int stutter) {
    omnimusic::BufferInfo msg;
    msg.set_samples(samples);
    msg.set_stutter(stutter);

    writeProtoBufMessage(MESSAGE_BUFFER_INFO, msg);
}
// -------------------------------------------------------------------------------------
void SocketClient::writeRequest(const ::omnimusic::Request_RequestType request) {
    omnimusic::Request msg;
    msg.set_request(request);

    writeProtoBufMessage(MESSAGE_REQUEST, msg);
}
// -------------------------------------------------------------------------------------
void SocketClient::writeAudioResponse(const uint32_t written) {
    omnimusic::AudioResponse msg;
    msg.set_written(written);

    writeProtoBufMessage(MESSAGE_AUDIO_RESPONSE, msg);
}
// -------------------------------------------------------------------------------------
uint32_t SocketClient::convertBytesToUInt32(int8_t* data) {
    return ((data[0] << 24) | (data[1] << 16) | (data[2] << 8) | (data[3]));
}
// -------------------------------------------------------------------------------------
void SocketClient::convertInt32ToBytes(int32_t value, uint8_t* buffer) {
    for (int i = 0; i < 4; ++i) {
        buffer[3 - i] = (value >> (i * 8));
    }
}
// -------------------------------------------------------------------------------------
