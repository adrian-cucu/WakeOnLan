//
// Created by eu_ad on 12/2/2019.
//

//
// Created by eu_ad on 12/2/2019.
//

#include <jni.h>
#include <android/log.h>

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>            // struct addrinfo
#include <sys/types.h>        // needed for socket(), uint8_t, uint16_t
#include <sys/socket.h>       // needed for socket()
#include <netinet/in.h>       // IPPROTO_RAW, INET_ADDRSTRLEN
#include <netinet/ip.h>       // IP_MAXPACKET (which is 65535)
#include <arpa/inet.h>        // inet_pton() and inet_ntop()
#include <sys/ioctl.h>        // macro ioctl is defined
#include <net/if.h>           // struct ifreq
#include <linux/if_ether.h>   // ETH_P_ARP = 0x0806
#include <linux/if_packet.h>  // struct sockaddr_ll (see man 7 packet)
#include <net/ethernet.h>
#include <getopt.h>
#include <ifaddrs.h>
#include <sys/cdefs.h>

#include <errno.h>            // errno, perror()

#define xstr(s) str(s)
#define str(s) #s
#define TAG "WakeOnLan"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,    TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,     TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,     TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,    TAG, __VA_ARGS__)

#define PERROR(__err_fmt, ...)                              \
do {                                                        \
    char err_buf[1024];                                     \
    int ret = -1;                                           \
    if (errno != 0)                                         \
        ret = snprintf(err_buf, sizeof(err_buf), "%s",      \
            strerror(errno));                               \
    if (ret <= 0) {                                         \
        LOGE(__err_fmt "\r\n",                              \
                ## __VA_ARGS__);                            \
    } else {                                                \
         LOGI(__err_fmt "(%s)\r\n",                         \
            err_buf, ## __VA_ARGS__);                       \
    }                                                       \
} while(0)

/* Total octets in header.   */
#ifndef ETH_HLEN
#define ETH_HLEN    14
#endif

/* Octets in one ethernet addres(MAC) */
#ifndef ETH_ALEN
#define ETH_ALEN    6
#endif

// Wake On Lan packet header
struct wolhdr {
    // sets all bytes to 0xff
    uint8_t sync_stream[6];
    // 16 repetitions of target computer mac address
    uint8_t _16mac[16 * ETH_ALEN];
} __attribute__((packed));

#define UDP_WOL_PORT 9
#define MAX_UDP_PAYLOAD_SIZE    65535

#define BUF_SIZE    2048

namespace com_wakeonlan_WakeOnLan {

    static int exec(const char *cmd) {
        FILE *file = NULL;
        char cmdbuf[255];
        char buf[BUF_SIZE + 1];
        int exit_status;

//        snprintf(cmdbuf, sizeof(cmdbuf), "ping -q -c 2 -W 2 %s", host);
//        snprintf(cmdbuf, sizeof(cmdbuf), "ping -c 2 -W 2 -s 0 %s", host);
//        snprintf(cmdbuf, sizeof(cmdbuf), "ping -c 2 -W 2 %s", host);

//        LOGD("Our command is: '%s'", cmdbuf);

//        file = popen("ping -c 2 -i 5 10.28.33.120", "r");

        file = popen(cmd, "r");
        if (file == NULL) {
            PERROR("popen(): ");
            return -1;
        }
        while (fgets(buf, BUF_SIZE - 1, file)) {
            LOGD("%s", buf);
        }

        exit_status = WEXITSTATUS(pclose(file));
        LOGD("'%s' exit status: %d", cmd, exit_status);
        return exit_status;
    }

    static int ping_1(const char *host) {
        FILE *file = NULL;
        char cmdbuf[255];
        char buf[BUF_SIZE + 1];
        int exit_status;

//        snprintf(cmdbuf, sizeof(cmdbuf), "ping -q -c 2 -W 2 %s", host);
        snprintf(cmdbuf, sizeof(cmdbuf), "ping -c 3 -W 2 -s 0 %s", host);
//        snprintf(cmdbuf, sizeof(cmdbuf), "ping -c 2 -W 2 %s", host);

//        LOGD("Our command is: '%s'", cmdbuf);

//        file = popen("ping -c 2 -i 5 10.28.33.120", "r");

        file = popen(cmdbuf, "r");
        if (file == NULL) {
            PERROR("popen(): ");
            return -1;
        }
        while (fgets(buf, BUF_SIZE - 1, file)) {
            LOGD("%s", buf);
        }

        exit_status = WEXITSTATUS(pclose(file));
        LOGD("'%s' exit status: %d", cmdbuf, exit_status);
        return exit_status;
    }

    static void test() {

        char buf[BUF_SIZE];
        char ch;
        size_t n;
        FILE *fp;

        fp = fopen("/proc/net/arp", "r"); // read mode
        if (fp == NULL) {
            PERROR("Error while opening the file.\n");
            return;
        }


//        while ((n = fread(buf, BUF_SIZE - 1, 1, fp))) {
//        }

        while (fgets(buf, BUF_SIZE - 1, fp)) {
            LOGD("%s", buf);
        }

        fclose(fp);
    }

    static char *GETHOSTBYADDR(const char *addr)
    {
        int ret;
        struct sockaddr_in saddr;

        memset(&saddr, 0, sizeof(struct sockaddr_in));
        saddr.sin_family = AF_INET;
        saddr.sin_addr.s_addr = inet_addr(addr); // set the local IP address
        //saddr.sin_port = htons(80); // set the port number

        static char hostname[255];

        ret = getnameinfo((const struct sockaddr *)&saddr, sizeof(struct sockaddr_in),
                          hostname, sizeof(hostname), NULL, 0,
                          NI_NAMEREQD | NI_NOFQDN);

        printf("ret = %d\n", ret);
//    if (ret != 0) {
        printf("%d", EAI_AGAIN);
        printf("%d", EAI_NONAME);
        printf("%d", EAI_BADFLAGS);
        printf("%d", EAI_FAIL);
        printf("%d", EAI_FAMILY);
        //  }

        return hostname;
    }


    static jint wol_udp(JNIEnv *env, jclass cls,
            jbyteArray macAddr, jstring ipAddr, jint port) {

        int sock_fd = -1;

        sock_fd  = socket(AF_PACKET, SOCK_DGRAM, 0);
        if (sock_fd == -1) {
            PERROR("failed to create socket: ");
            return -1;
        }

        LOGD("socket created: %d", sock_fd);

        return 0;

        int sock = -1;
        int i, n;
        struct sockaddr_in addr;
        char payload[MAX_UDP_PAYLOAD_SIZE];
        unsigned int payload_length;
        struct wolhdr *wol_hdr = NULL;

        sock = socket(AF_INET, SOCK_DGRAM, 0);
        if (sock < 0) {
            PERROR("socket()");
            return -1;
        }

        // Set socket options
        // Enable broadcast
        int broadcastEnable = 1;
        int ret = setsockopt(sock, SOL_SOCKET, SO_BROADCAST, &broadcastEnable,
                             sizeof(broadcastEnable));
        if (ret == -1) {
            perror("setsockopt: ");
            close(sock);
        }

        addr.sin_family = AF_INET;
        addr.sin_port = htons(port);

        jboolean is_copy;
        const char *ip_addr_str = env->GetStringUTFChars(ipAddr, &is_copy);
        inet_aton(ip_addr_str, &addr.sin_addr);
        if (is_copy == JNI_TRUE) {
            env->ReleaseStringUTFChars(ipAddr, ip_addr_str);
        }

        // WoL magic packet data
        wol_hdr = (struct wolhdr *) &payload[0];
        wol_hdr->sync_stream[0] = 0xff;
        wol_hdr->sync_stream[1] = 0xff;
        wol_hdr->sync_stream[2] = 0xff;
        wol_hdr->sync_stream[3] = 0xff;
        wol_hdr->sync_stream[4] = 0xff;
        wol_hdr->sync_stream[5] = 0xff;

        jbyte *target_mac = (jbyte *) env->GetByteArrayElements(macAddr, NULL);
        if (!target_mac) {
            return -1;
        }
        for (i = 0; i < 16; ++i)
            memcpy(&wol_hdr->_16mac[i * ETH_ALEN], &target_mac[0], ETH_ALEN);
        // WoL magic packet data is now ready

        env->ReleaseByteArrayElements(macAddr, target_mac, 0);

        payload_length = sizeof(struct wolhdr);

        for (i = 0; i < 3; ++i) {
            n = sendto(sock, payload, payload_length, 0,
                       (const struct sockaddr *) &addr, sizeof(addr));
            if (n <= 0) {
                PERROR("sendto");
                close(sock);
            }
        }
        close(sock);

        return 0;
    }

    static  jint ping(JNIEnv *env, jclass cls, jstring host) {

//        LOGD("%s", GETHOSTBYADDR("192.168.1.1"));
//        test();

        jboolean is_copy;
        const char *hostname = env->GetStringUTFChars(host, &is_copy);

        FILE *file = NULL;
        char cmdbuf[255];
        char buf[1024];
        int exit_status;

//        snprintf(cmdbuf, sizeof(cmdbuf), "ping -q -c 2 -W 2 %s", host);
        snprintf(cmdbuf, sizeof(cmdbuf), "ping -c 1 %s", hostname);
//        snprintf(cmdbuf, sizeof(cmdbuf), "ping 192.168.1.255");
//        snprintf(cmdbuf, sizeof(cmdbuf), "ping -c 2 -W 2 %s", host);
//        LOGD("Our command is: '%s'", cmdbuf);
//        file = popen("ping -c 2 -i 5 10.28.33.120", "r");

        file = popen(cmdbuf, "r");
        if (file == NULL) {
            PERROR("popen(): ");
            return -1;
        }
//        while (fgets(buf, BUF_SIZE - 1, file)) {
//            LOGD("%s", buf);
//        }

        exit_status = WEXITSTATUS(pclose(file));
        LOGD("'%s' exit status: %d", cmdbuf, exit_status);

        if (is_copy == JNI_TRUE) {
            env->ReleaseStringUTFChars(host, hostname);
        }
        return exit_status;;
    }


    static JNINativeMethod method_table[] = {
            {"wol_udp", "([BLjava/lang/String;I)I", (void *) wol_udp},
            {"ping",    "(Ljava/lang/String;)I", (void *) ping}
    };
}


using namespace com_wakeonlan_WakeOnLan;

extern "C" jint JNI_OnLoad(JavaVM *vm, void *reserver) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    jclass cls = env->FindClass("com/wakeonlan/WakeOnLan");
    if (!cls) {
        return -1;
    }

    env->RegisterNatives(cls, method_table,
                         sizeof(method_table) / sizeof(method_table[0]));
    env->DeleteLocalRef(cls);

    return JNI_VERSION_1_6;
}
