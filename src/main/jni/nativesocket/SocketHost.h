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

#ifndef SRC_MAIN_JNI_NATIVESOCKET_SOCKETHOST_H_
#define SRC_MAIN_JNI_NATIVESOCKET_SOCKETHOST_H_

#include <string>
#include <thread>
#include "SocketCallbacks.h"
#include "SocketCommon.h"

/**
 * Host socket. This socket is only used by the main app, but it is implemented in providerlib
 * to keep most of the read/write implementation common.
 */
class SocketHost : public SocketCommon {
 public:
    // ctor
    explicit SocketHost(const std::string& socket_name);

    // dtor
    virtual ~SocketHost();

    // Initializes the socket
    bool initialize();

 private:
    // Implements virtual parent method
    bool writeToSocket(const uint8_t* data, uint32_t len);

    // Process events (update network, calls callbacks). Called auto by the thread
    void processEventsThread();
    int processEvents();

 private:
    int32_t m_Server;
    int32_t m_Client;
    int8_t* m_pBuffer;
    std::thread m_EventThread;
};

#endif  // SRC_MAIN_JNI_NATIVESOCKET_SOCKETCLIENT_H_
