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

// Keep in tune with Java
enum MessageType {
    MESSAGE_AUDIO_DATA      = 1,
    MESSAGE_AUDIO_RESPONSE  = 2,
    MESSAGE_BUFFER_INFO     = 3,
    MESSAGE_FORMAT_INFO     = 4,
    MESSAGE_REQUEST         = 5
};

#endif  // SRC_MAIN_JNI_NATIVESOCKET_SOCKETCOMMON_H_
