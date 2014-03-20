LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := fft-jni
LOCAL_SRC_FILES := fft.cpp
LOCAL_LDLIBS := -Lbuild/platforms/android-19/arch-arm/usr/lib -llog 

include $(BUILD_SHARED_LIBRARY)
