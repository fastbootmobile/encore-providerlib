/*
 * Copyright (C) 2014 Fastboot Mobile, LLC.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses>.
 */

#include "SocketClient.h"
#include <errno.h>
#include <netdb.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <sys/un.h>
#include <string>
#include "Log.h"
#include "proto/Plugin.pb.h"

#define SOCKET_BUFFER_SIZE 1024
#define LOG_TAG "NativeSocket-SocketClient"

// -------------------------------------------------------------------------------------
SocketClient::SocketClient(const std::string& socket_name) : m_SocketName(socket_name),
        m_Server(-1) {
    GOOGLE_PROTOBUF_VERIFY_VERSION;
    m_pBuffer = new uint8_t[SOCKET_BUFFER_SIZE];
}
// -------------------------------------------------------------------------------------
SocketClient::~SocketClient() {
    if (m_Server >= 0) {
        close(m_Server);
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

    return true;
}
// -------------------------------------------------------------------------------------
bool SocketClient::write(uint8_t* data, uint32_t len) {
    uint32_t len_left = len;
    uint32_t len_written;
    uint8_t* data_ptr = data;

    // Loop until all is sent
    while (len_left > 0) {
        len_written = send(m_Server, data, len_left, 0);

        if (len_written < 0) {
            // An error occured.
            if (errno == EINTR) {
                // The socket call was interrupted, we can try again
                continue;
            } else {
                // A more dangerous error occurred, bail out
                perror("write");
                return false;
            }
        } else if (len_written == 0) {
            // Socket is closed
            return false;
        }

        len_written -= len_written;
        data_ptr += len_written;
    }

    return true;
}
// -------------------------------------------------------------------------------------
void SocketClient::processEvents() {
    int32_t len_read = 0;
    int32_t total_len_read = 0;

    // Before processing the protobuf structure itself, we first read the message type (one byte)
    // and the message length (four bytes), so five bytes.
    const int32_t header_size = 5;

    while (total_len_read < header_size) {
        len_read = recv(m_Server, m_pBuffer, header_size, MSG_WAITALL);

        if (len_read < 0) {
            if (errno == EINTR) {
                // The socket call was interrupted, we can try again
                continue;
            } else {
                // A more dangerous error occurred, bail out
                ALOGE("Error while reading from socket!");
                return;
            }
        } else if (len_read == 0) {
            // Socket is closed
            return;
        }
        total_len_read += len_read;
    }

    uint8_t message_type = m_pBuffer[0];
    uint32_t message_size = convertBytesToUInt32(&m_pBuffer[1]);

    ALOGD("message type: %d, message size: %d", message_type, message_size);
}
// -------------------------------------------------------------------------------------
uint32_t SocketClient::convertBytesToUInt32(uint8_t* data) {
    return ( (data[0] << 24) | (data[1] << 16) | (data[2] << 8) | (data[3]));
}
// -------------------------------------------------------------------------------------
