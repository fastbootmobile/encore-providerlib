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

// -------------------------------------------------------------------------------------
SocketCommon::SocketCommon(const std::string& socket_name) : m_SocketName(socket_name),
        m_pCallback(nullptr), m_bWaitingAudioResponse(false), m_iWrittenSamples(0) {
    GOOGLE_PROTOBUF_VERIFY_VERSION;
}
// -------------------------------------------------------------------------------------
SocketCommon::~SocketCommon() {
    google::protobuf::ShutdownProtobufLibrary();
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

                if (m_bWaitingAudioResponse) {
                    m_iWrittenSamples = message.written();
                    m_bWaitingAudioResponse = false;
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
        m_iWrittenSamples = -1;
        m_bWaitingAudioResponse = true;
    }

    writeProtoBufMessage(MESSAGE_AUDIO_DATA, msg);

    long approximate_time_us = 0;

    if (wait_for_response) {
        // Wait max. about 3 seconds for a response
        while (m_iWrittenSamples < 0 && approximate_time_us < 1000 * 1000 * 3) {
            // Wait 0.1ms
            usleep(100);
            approximate_time_us += 100;
        }
        return m_iWrittenSamples;
    } else {
        return len;
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