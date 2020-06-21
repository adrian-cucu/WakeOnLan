package com.wakeonlan;

public class WakeOnLan {

    static {
        System.loadLibrary("com_wakeonlan_WakeOnLan");
    }

    public native static int wol_udp(byte[] mac, String ip, int port);

    public native static int ping(String host);
}
