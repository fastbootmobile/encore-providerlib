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
#ifndef SRC_MAIN_JNI_NATIVESOCKET_SOCKETCALLBACKS_H_
#define SRC_MAIN_JNI_NATIVESOCKET_SOCKETCALLBACKS_H_

#include <proto/Plugin.pb.h>

/**
 * SocketCallbacks interface
 * Must be implemented by the client plugin
 */
class SocketCallbacks {
 public:
    /**
     * Called when the other end requests a particular information
     */
    virtual void onRequest(const omnimusic::Request_RequestType type) = 0;

    /**
     * When a BufferInfo message has arrived
     * @param sample The number of samples currently in the buffer
     * @param stutter The number of stutters/dropouts since the start of the session
     */
    virtual void onBufferInfo(int32_t samples, int32_t stutter) = 0;

    /**
     * When a FormatInfo message arrived
     * @param sample_rate The sample rate, in Hz
     * @param channels The number of channels
     */
    virtual void onFormatInfo(int32_t sample_rate, int32_t channels) = 0;

    /**
     * When audio data arrives. Data must be deleted in the callback once done using it.
     * @param data A pointer to the audio data
     * @param len The length of the data
     */
    virtual void onAudioData(uint8_t* data, uint32_t len) = 0;
};

#endif  // SRC_MAIN_JNI_NATIVESOCKET_SOCKETCALLBACKS_H_
