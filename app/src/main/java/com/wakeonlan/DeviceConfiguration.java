package com.wakeonlan;

import androidx.annotation.Nullable;

public class DeviceConfiguration {

    public enum DeviceStatus {
        NOT_AWAKE, AWAKE, UNKNOWN
    }

    private Device mDevice;
    private DeviceStatus mDeviceStatus;
    private String mAddress;
    private int mPort;


    public DeviceConfiguration(Device device) {
        mDevice = device;
        mDeviceStatus = DeviceStatus.UNKNOWN;
        mAddress = null;
        mPort = 0;
    }

    public DeviceConfiguration(Device device, int port) {
        mDevice = device;
        mDeviceStatus = DeviceStatus.UNKNOWN;
        mAddress = null;
        mPort = port;
    }

    public DeviceConfiguration(Device device, String address) {
        mDevice = device;
        mDeviceStatus = DeviceStatus.UNKNOWN;
        mAddress = address;
        mPort = 0;
    }

    public DeviceConfiguration(Device device, String address, int port) {
        mDevice = device;
        mDeviceStatus = DeviceStatus.UNKNOWN;
        mAddress = address;
        mPort = port;
    }

    public Device getDevice() {
        return mDevice;
    }

    public String getAddress() {
        return mAddress;
    }

    public int getPort() {
        return mPort;
    }

    public DeviceStatus getDeviceStatus() {
        return mDeviceStatus;
    }

    @Override
    public String toString() {
        return "DeviceConfiguration{" +
                "device_configuration_item=" + mDevice +
                ", deviceStatus=" + mDeviceStatus +
                ", address='" + mAddress + '\'' +
                ", port=" + mPort +
                '}';
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DeviceConfiguration)) {
            return false;
        }
        DeviceConfiguration other = (DeviceConfiguration) obj;
        return other.getDevice().getID() == mDevice.getID();
    }
}
