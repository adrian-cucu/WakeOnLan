LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_SRC_FILES := com_wakeonlan_WakeOnLan.cpp
LOCAL_MODULE := com_wakeonlan_WakeOnLan
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)
