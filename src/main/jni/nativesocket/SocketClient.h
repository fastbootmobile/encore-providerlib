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
#ifndef SRC_MAIN_JNI_NATIVESOCKET_SOCKETCLIENT_H_
#define SRC_MAIN_JNI_NATIVESOCKET_SOCKETCLIENT_H_

#include <string>

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

    // Writes the provided data to the socket
    bool write(uint8_t* data, uint32_t len);

    // Process events (update network, calls callbacks)
    void processEvents();

 private:
    inline uint32_t convertBytesToUInt32(uint8_t* data);

 private:
    std::string m_SocketName;
    int32_t m_Server;
    uint8_t* m_pBuffer;
};

#endif  // SRC_MAIN_JNI_NATIVESOCKET_SOCKETCLIENT_H_
