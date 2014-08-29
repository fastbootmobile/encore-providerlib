# Copyright (C) 2009 The Android Open Source Project
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
#
#

LOCAL_PATH := $(call my-dir)

IGNORED_WARNINGS := -Wno-sign-compare -Wno-unused-parameter -Wno-sign-promo

CC_LITE_SRC_FILES := \
    $(LOCAL_PATH)/src/google/protobuf/stubs/common.cc                              \
    $(LOCAL_PATH)/src/google/protobuf/stubs/once.cc                                \
    $(LOCAL_PATH)/src/google/protobuf/extension_set.cc                             \
    $(LOCAL_PATH)/src/google/protobuf/generated_message_util.cc                    \
    $(LOCAL_PATH)/src/google/protobuf/message_lite.cc                              \
    $(LOCAL_PATH)/src/google/protobuf/repeated_field.cc                            \
    $(LOCAL_PATH)/src/google/protobuf/wire_format_lite.cc                          \
    $(LOCAL_PATH)/src/google/protobuf/io/coded_stream.cc                           \
    $(LOCAL_PATH)/src/google/protobuf/io/zero_copy_stream.cc                       \
    $(LOCAL_PATH)/src/google/protobuf/io/zero_copy_stream_impl_lite.cc

# C++ lite library
# =======================================================
include $(CLEAR_VARS)

LOCAL_MODULE := libprotobuf

LOCAL_CPP_EXTENSION := .cc

LOCAL_SRC_FILES := $(CC_LITE_SRC_FILES)

ifeq ($(TARGET_ARCH),x86)
    LOCAL_SRC_FILES += $(LOCAL_PATH)/src/google/protobuf/stubs/atomicops_internals_x86_gcc.cc
endif

LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/android \
    $(LOCAL_PATH)/src

LOCAL_CFLAGS := -DGOOGLE_PROTOBUF_NO_RTTI $(IGNORED_WARNINGS)
# LOCAL_NDK_STL_VARIANT := stlport_static

include $(BUILD_SHARED_LIBRARY)
