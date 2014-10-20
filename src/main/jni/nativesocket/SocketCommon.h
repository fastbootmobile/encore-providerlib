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

#ifndef SRC_MAIN_JNI_NATIVESOCKET_SOCKETCOMMON_H_
#define SRC_MAIN_JNI_NATIVESOCKET_SOCKETCOMMON_H_

#include "SocketCallbacks.h"
#include <google/protobuf/message.h>

// Keep in tune with Java
enum MessageType {
    MESSAGE_AUDIO_DATA      = 1,
    MESSAGE_AUDIO_RESPONSE  = 2,
    MESSAGE_BUFFER_INFO     = 3,
    MESSAGE_FORMAT_INFO     = 4,
    MESSAGE_REQUEST         = 5
};

class SocketCommon {
 public:
    // ctor
    SocketCommon(const std::string& socket_name);

    // dtor
    virtual ~SocketCommon();

    // Initializes the socket
    virtual bool initialize() = 0;

    // Returns the socket name
    std::string getSocketName() const;

    // Set the callback
    void setCallback(SocketCallbacks* callback);

    // Write an AUDIO_DATA message. If wait_for_response is true, the method call will wait
    // for the other end to reply the number of samples written, and that value will be returned.
    // If wait_for_response is false, writeAudioData will return 'len' once the write call is done.
    int32_t writeAudioData(const void* data, const uint32_t len, bool wait_for_response = false);

    // Write an AUDIO_RESPONSE message
    void writeAudioResponse(const uint32_t written);

    // Write a FORMAT_INFO message
    void writeFormatInfo(const int channels, const int sample_rate);

    // Write a BUFFER_INFO message
    void writeBufferInfo(const int samples, const int stutter);

    // Write a REQUEST message
    void writeRequest(const ::omnimusic::Request_RequestType request);

    inline uint32_t convertBytesToUInt32(uint8_t* data) {
        return data[3] | ( (char) data[2] << 8 ) | ( (char) data[1] << 16 ) | ( (char) data[0] << 24 );
    }

    inline void convertInt32ToBytes(uint32_t n, uint8_t* buffer) {
        buffer[0] = (n >> 24) & 0xFF;
        buffer[1] = (n >> 16) & 0xFF;
        buffer[2] = (n >> 8) & 0xFF;
        buffer[3] = n & 0xFF;
    }

 protected:
    bool writeProtoBufMessage(uint8_t opcode, const ::google::protobuf::Message& msg);

    // Writes the provided data to the socket
    virtual bool writeToSocket(const uint8_t* data, uint32_t len) = 0;

    int processMessage(const int8_t* data, const size_t message_size,
            const MessageType message_type);


 protected:
    std::string m_SocketName;
    SocketCallbacks* m_pCallback;
    std::atomic<bool> m_bWaitingAudioResponse;
    std::atomic<int> m_iWrittenSamples;
};

#endif  // SRC_MAIN_JNI_NATIVESOCKET_SOCKETCOMMON_H_
