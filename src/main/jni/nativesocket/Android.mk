LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# Module name and files
LOCAL_MODULE := libnativesocket
LOCAL_SRC_FILES := \
    $(LOCAL_PATH)/SocketClient.cpp \
    $(LOCAL_PATH)/SocketCallbacks.cpp \
	$(LOCAL_PATH)/proto/Plugin.pb.cc

LOCAL_CPP_EXTENSION := .cc .cpp
LOCAL_C_INCLUDES := $(LOCAL_PATH)/../protobuf/src

LOCAL_CFLAGS := -DGOOGLE_PROTOBUF_NO_RTTI -Wall -Werror

# Optimization CFLAGS
LOCAL_CFLAGS += -ffast-math -O3 -funroll-loops

# Workaround for bug 61571 https://gcc.gnu.org/bugzilla/show_bug.cgi?id=61571
LOCAL_CFLAGS += -fno-strict-aliasing

LOCAL_SHARED_LIBRARIES := libprotobuf

include $(BUILD_STATIC_LIBRARY)
