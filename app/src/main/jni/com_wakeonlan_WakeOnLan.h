/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_wakeonlan_WakeOnLan */

#ifndef _Included_com_wakeonlan_WakeOnLan
#define _Included_com_wakeonlan_WakeOnLan
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_wakeonlan_WakeOnLan
 * Method:    wol_udp
 * Signature: ([BLjava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_wakeonlan_WakeOnLan_wol_1udp
  (JNIEnv *, jclass, jbyteArray, jstring, jint);

/*
 * Class:     com_wakeonlan_WakeOnLan
 * Method:    ping
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_wakeonlan_WakeOnLan_ping
  (JNIEnv *, jclass, jstring);

#ifdef __cplusplus
}
#endif
#endif