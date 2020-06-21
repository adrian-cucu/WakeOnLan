package com.wakeonlan;

import androidx.annotation.Nullable;
import java.math.BigInteger;
import java.util.Formatter;


public class Device {

    private int mID;
    private String mHostName;
    private String mMacAddress;

    public Device(int id, String macAddress) {
        mID = id;
        mHostName = null;
        mMacAddress = macAddress;
    }

    public Device(int id, String macAddress, String hostName) {
        mID = id;
        mHostName = hostName;
        mMacAddress = macAddress;
    }

    public String getHostName() {
        return mHostName;
    }

    public void setHostName(String hostName) {
        mHostName = hostName;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public int getID() {
        return mID;
    }

    public byte[] getRawMacAddress() {
        String[] bytes = mMacAddress.split(":");
        byte[] parsed = new byte[bytes.length];

        for (int x = 0; x < bytes.length; x++) {
            BigInteger temp = new BigInteger(bytes[x], 16);
            byte[] raw = temp.toByteArray();
            parsed[x] = raw[raw.length - 1];
        }
        return parsed;
    }

    @Override
    public String toString() {
        return "Device{" +
                "ID=" + mID +
                ", hostName='" + mHostName + '\'' +
                ", mac=" + mMacAddress +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Device)) {
            return false;
        }

        Device other = (Device) obj;
        return mMacAddress.equals(other.mMacAddress);
    }
}
