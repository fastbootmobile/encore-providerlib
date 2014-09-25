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

#ifndef SRC_MAIN_JNI_NATIVESOCKET_SOCKETCLIENT_H_
#define SRC_MAIN_JNI_NATIVESOCKET_SOCKETCLIENT_H_

#include <string>
#include <thread>
#include <google/protobuf/message.h>
#include "SocketCallbacks.h"

/**
 * Client socket. This is the socket plug-ins should use to receive requests from the main app,
 * and receive/push audio data to it.
 */
class SocketClient {
 public:
    // ctor
    explicit SocketClient(const std::string& socket_name);

    // dtor
    ~SocketClient();

    // Initializes the socket
    bool initialize();

    // Set the callback
    void setCallback(SocketCallbacks* callback);

    // Write an AUDIO_DATA message
    void writeAudioData(const void* data, const uint32_t len);

    // Write an AUDIO_RESPONSE message
    void writeAudioResponse(const uint32_t written);

    // Write a FORMAT_INFO message
    void writeFormatInfo(const int channels, const int sample_rate);

    // Write a BUFFER_INFO message
    void writeBufferInfo(const int samples, const int stutter);

    // Write a REQUEST message
    void writeRequest(const ::omnimusic::Request_RequestType request);

 private:
    inline uint32_t convertBytesToUInt32(int8_t* data);
    inline void convertInt32ToBytes(int32_t value, uint8_t* buffer);

    bool writeProtoBufMessage(uint8_t opcode, const ::google::protobuf::Message& msg);

    // Writes the provided data to the socket
    bool writeToSocket(const uint8_t* data, uint32_t len);

    // Process events (update network, calls callbacks). Called auto by the thread
    void processEventsThread();
    int processEvents();

 private:
    std::string m_SocketName;
    int32_t m_Server;
    int8_t* m_pBuffer;
    SocketCallbacks* m_pCallback;
    std::thread m_EventThread;
};

#endif  // SRC_MAIN_JNI_NATIVESOCKET_SOCKETCLIENT_H_
