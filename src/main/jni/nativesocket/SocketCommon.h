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
