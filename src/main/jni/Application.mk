# Recommended NDK version: android-r10
NDK_TOOLCHAIN_VERSION := 4.9
APP_ABI := armeabi-v7a
APP_CPPFLAGS += -std=c++11
APP_STL := c++_shared

# debug
APP_OPTIM=debug
NDK_DEBUG=1
