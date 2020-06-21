package com.wakeonlan;

public interface OnNewDeviceDiscoveredListener {

    void onNewDevice(String hostname, String addr, String mac, String vendor);
}
