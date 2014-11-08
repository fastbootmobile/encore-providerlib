# Copyright (C) 2014 Fastboot Mobile, LLC.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# Module name and files
LOCAL_MODULE := libnativesocket
LOCAL_SRC_FILES := \
    $(LOCAL_PATH)/SocketClient.cpp \
    $(LOCAL_PATH)/SocketHost.cpp \
    $(LOCAL_PATH)/SocketCallbacks.cpp \
    $(LOCAL_PATH)/SocketCommon.cpp \
	$(LOCAL_PATH)/proto/Plugin.pb.cc

LOCAL_CPP_EXTENSION := .cc .cpp
LOCAL_C_INCLUDES := $(LOCAL_PATH)/../protobuf/src

LOCAL_CFLAGS := -DGOOGLE_PROTOBUF_NO_RTTI -Wall -Werror -Wno-sign-compare

# Optimization CFLAGS
LOCAL_CFLAGS += -ffast-math -O3 -funroll-loops

# Workaround for bug 61571 https://gcc.gnu.org/bugzilla/show_bug.cgi?id=61571
LOCAL_CFLAGS += -fno-strict-aliasing

LOCAL_SHARED_LIBRARIES := libprotobuf

include $(BUILD_STATIC_LIBRARY)
