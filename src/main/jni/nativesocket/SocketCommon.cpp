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

#include "SocketCommon.h"
#include "Log.h"
#include "proto/Plugin.pb.h"
#include <unistd.h>
#include <sys/un.h>

#define LOG_TAG "SocketCommon"

static int gProtobufUsageCount = 0;

// -------------------------------------------------------------------------------------
SocketCommon::SocketCommon(const std::string& socket_name) : m_SocketName(socket_name),
        m_pCallback(nullptr), m_iWrittenSamples(0) {
    GOOGLE_PROTOBUF_VERIFY_VERSION;
    gProtobufUsageCount++;
}
// -------------------------------------------------------------------------------------
SocketCommon::~SocketCommon() {
    gProtobufUsageCount--;
    if (gProtobufUsageCount == 0) {
        google::protobuf::ShutdownProtobufLibrary();
    }
}
// -------------------------------------------------------------------------------------
void SocketCommon::setCallback(SocketCallbacks* callback) {
    m_pCallback = callback;
}
// -------------------------------------------------------------------------------------
std::string SocketCommon::getSocketName() const {
    return m_SocketName;
}
// -------------------------------------------------------------------------------------
int SocketCommon::processMessage(const int8_t* data, const int message_size,
        const MessageType message_type) {
    // ALOGD("Message length=%d", message_size);

    if (message_size <= 0) {
        ALOGW("Message size invalid! (%d)", message_size);
        return -1;
    }

    // Convert to protobuf message and broadcast to the callback
    std::string container(reinterpret_cast<const char*>(&data[5]), message_size);

    // ALOGD("Incoming message length=%d opcode=%d", message_size, message_type);

    switch (message_type) {
        case MESSAGE_AUDIO_DATA:
            if (m_pCallback) {
                omnimusic::AudioData message;
                message.ParseFromString(container);

                std::string s_container = message.samples();
                const uint8_t* samples = reinterpret_cast<const uint8_t*>(s_container.c_str());
                const uint32_t num_samples = s_container.size();
                m_pCallback->onAudioData(this, samples, num_samples);
            }
            break;

        case MESSAGE_AUDIO_RESPONSE: {
                omnimusic::AudioResponse message;
                message.ParseFromString(container);

                if (m_pCallback) {
                    m_pCallback->onAudioResponse(this, message.written());
                }

                {
                    // Notify of the response internally for writeAudioData, if needed
                    std::unique_lock<std::mutex> lock(m_WrittenMutex);
                    m_iWrittenSamples = message.written();
                    m_WrittenCondition.notify_all();
                }
            }
            break;

        case MESSAGE_BUFFER_INFO:
            if (m_pCallback) {
                omnimusic::BufferInfo message;
                message.ParseFromString(container);
                m_pCallback->onBufferInfo(this, message.samples(), message.stutter());
            }
            break;

        case MESSAGE_FORMAT_INFO:
            if (m_pCallback) {
                omnimusic::FormatInfo message;
                message.ParseFromString(container);
                m_pCallback->onFormatInfo(this, message.sampling_rate(), message.channels());
            }
            break;

        case MESSAGE_REQUEST:
            if (m_pCallback) {
                omnimusic::Request message;
                message.ParseFromString(container);
                m_pCallback->onRequest(this, message.request());
            }
            break;

        default:
            ALOGE("Unhandled opcode %d", message_type);
            return -1;
    }

    return 0;
}
// -------------------------------------------------------------------------------------
bool SocketCommon::writeProtoBufMessage(uint8_t opcode, const ::google::protobuf::Message& msg) {
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
int32_t SocketCommon::writeAudioData(const void* data, const uint32_t len, bool wait_for_response) {
    omnimusic::AudioData msg;
    msg.set_samples(data, len);

    if (wait_for_response) {
        // Wait max. about 500 milliseconds for a response. Note that we lock the mutex first
        // so that we don't miss the notify event in case we get the response so fast the lock
        // doesn't even have time to get in place.
        {
            std::unique_lock<std::mutex> lock(m_WrittenMutex);

            // Send the message
            if (!writeProtoBufMessage(MESSAGE_AUDIO_DATA, msg)) {
                return -1;
            }

            // Wait 100ms max for the reply
            auto now = std::chrono::system_clock::now();
            if (m_WrittenCondition.wait_until(lock, now + std::chrono::milliseconds(500))
                    == std::cv_status::timeout) {
                ALOGW("Timed out waiting for sink written reply");
                m_iWrittenSamples = 0;
            }
        }

        return m_iWrittenSamples;
    } else {
        // No wait - send and return the amount written to the socket
        if (!writeProtoBufMessage(MESSAGE_AUDIO_DATA, msg)) {
            return -1;
        } else {
            return len;
        }
    }
}
// -------------------------------------------------------------------------------------
void SocketCommon::writeFormatInfo(const int channels, const int sample_rate) {
    omnimusic::FormatInfo msg;
    msg.set_channels(channels);
    msg.set_sampling_rate(sample_rate);

    writeProtoBufMessage(MESSAGE_FORMAT_INFO, msg);
}
// -------------------------------------------------------------------------------------
void SocketCommon::writeBufferInfo(const int samples, const int stutter) {
    omnimusic::BufferInfo msg;
    msg.set_samples(samples);
    msg.set_stutter(stutter);

    writeProtoBufMessage(MESSAGE_BUFFER_INFO, msg);
}
// -------------------------------------------------------------------------------------
void SocketCommon::writeRequest(const ::omnimusic::Request_RequestType request) {
    omnimusic::Request msg;
    msg.set_request(request);

    writeProtoBufMessage(MESSAGE_REQUEST, msg);
}
// -------------------------------------------------------------------------------------
void SocketCommon::writeAudioResponse(const uint32_t written) {
    omnimusic::AudioResponse msg;
    msg.set_written(written);

    writeProtoBufMessage(MESSAGE_AUDIO_RESPONSE, msg);
}
// -------------------------------------------------------------------------------------
