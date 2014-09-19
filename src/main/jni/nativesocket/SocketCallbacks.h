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
