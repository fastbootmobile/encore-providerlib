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

#include "SocketHost.h"
#include "SocketCommon.h"
#include <errno.h>
#include <netdb.h>
#include <unistd.h>
#include <netinet/tcp.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <string>
#include <thread>
#include "Log.h"
#include "proto/Plugin.pb.h"

#define SOCKET_BUFFER_SIZE 100000
#define LOG_TAG "NativeSocket-SocketHost"

// -------------------------------------------------------------------------------------
SocketHost::SocketHost(const std::string& socket_name) : SocketCommon(socket_name),
        m_Server(-1), m_Client(-1) {
    m_pBuffer = new int8_t[SOCKET_BUFFER_SIZE];
}
// -------------------------------------------------------------------------------------
SocketHost::~SocketHost() {
    ALOGD("Releasing socket %s (fd=%d)", m_SocketName.c_str(), m_Server);

    // Java LocalSocket on Android are broken. Thank you Google for not fixing a critical bug
    // 5 years after it is reported to you. In order to be able to not hit a deadlock in read()
    // on the java side, we send a "shutdown" message.
    uint8_t* socket_buffer = reinterpret_cast<uint8_t*>(malloc(5));
    convertInt32ToBytes(1, socket_buffer);  // + 1 for opcode byte, see Java
    socket_buffer[4] = MESSAGE_SHUTDOWN;
    writeToSocket(socket_buffer, 5);

    if (m_Server >= 0) {
        shutdown(m_Server, SHUT_RDWR);
        m_Server = -1;
    }

    if (m_EventThread.joinable()) {
        m_EventThread.detach();
    }

    delete[] m_pBuffer;
}
// -------------------------------------------------------------------------------------
bool SocketHost::initialize() {
    sockaddr_un addr;
    socklen_t len;

    // Setup socket address
    bzero(&addr, sizeof(addr));
    addr.sun_family = AF_LOCAL;

    // Copy socket name
    addr.sun_path[0] = '\0';
    snprintf(&addr.sun_path[1], sizeof(addr.sun_path), "%s", m_SocketName.c_str());
    len = offsetof(sockaddr_un, sun_path) + 1 + strlen(&addr.sun_path[1]);

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

    if (bind(m_Server, reinterpret_cast<sockaddr*>(&addr), len) < 0) {
        ALOGE("Cannot bind to socket: %s", strerror(errno));
        close(m_Server);
        return false;
    }

    if (listen(m_Server, 2) < 0) {
        ALOGE("Cannot start listening on socket: %s", strerror(errno));
        close(m_Server);
        return false;
    }

    ALOGD("Socket '%s' ready", m_SocketName.c_str());

    // Start poll thread
    m_EventThread = std::thread(&SocketHost::processEventsThread, this);
    return true;
}
// -------------------------------------------------------------------------------------
bool SocketHost::writeToSocket(const uint8_t* data, uint32_t len) {
    if (m_Client < 0) {
        if (m_Server > 0) {
            // This message is expected if no client is bound when we shut down
            ALOGE("Trying to write to socket '%s', but no client connected", m_SocketName.c_str());
        }
        return false;
    }

    int32_t len_left = len;
    int32_t len_written = 0;

    // Loop until all is sent
    while (len_left > 0) {
        len_written = send(m_Client, &(data[len_written]), len_left, 0);

        if (len_written < 0) {
            // An error occured.
            if (errno == EINTR) {
                // The socket call was interrupted, we can try again
                return false;
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
void SocketHost::processEventsThread() {
    while (m_Server >= 0) {
        if (m_Client < 0) {
            // Wait for a client
            m_Client = accept(m_Server, NULL, NULL);
            if (m_Client < 0) {
                if (errno != EINVAL) {
                    ALOGE("Error in accept(): %s (m_Server=%d)", strerror(errno), m_Server);
                }
                return;
            }
        }

        if (processEvents() < 0) {
            ALOGW("processEvents returned -1");

            // Make sure client is disconnected
            close(m_Client);
            m_Client = -1;
        }
    }
}
// -------------------------------------------------------------------------------------
int SocketHost::processEvents() {
    int32_t len_read = 0;
    int32_t total_len_read = 0;

    // Before processing the protobuf structure itself, we first read the message size (4 bytes)
    // and the opcode (one byte), so 5 bytes total
    const int32_t header_size = 5;

    while (total_len_read < header_size) {
        len_read = recv(m_Client, &m_pBuffer[total_len_read], header_size - total_len_read,
                0);

        if (len_read < 0) {
            if (errno == EINTR) {
                // The socket call was interrupted, we can try again
                return -1;
            } else {
                // A more dangerous error occurred, bail out
                ALOGE("Error while reading from socket: %s (%d)!", strerror(errno), errno);
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
    int32_t message_size = convertBytesToInt32(reinterpret_cast<uint8_t*>(&m_pBuffer[0])) - 1;
    MessageType message_type = static_cast<MessageType>(m_pBuffer[4]);

    const int32_t final_size = header_size + message_size;
    if (final_size > SOCKET_BUFFER_SIZE) {
        ALOGE("FATAL: Final size=%d, message_size=%d, SOCKET_BUFFER_SIZE=%d opcode=%d", final_size,
                message_size, SOCKET_BUFFER_SIZE, message_type);
        ALOGE("FATAL: Message size is larger than socket buffer size!");
    }

    // ALOGD("Message type=%d size=%d", message_type, message_size);

    // Now that we have the message size, we can keep on reading the following data
    while (total_len_read < final_size) {
        len_read = recv(m_Client, &m_pBuffer[total_len_read], final_size - total_len_read, 0);

        if (len_read < 0) {
            if (errno == EINTR) {
                // The socket call was interrupted
                return -1;
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

    return processMessage(m_pBuffer, message_size, message_type);
}
// -------------------------------------------------------------------------------------
