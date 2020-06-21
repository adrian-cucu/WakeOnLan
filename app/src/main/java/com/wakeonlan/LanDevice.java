package com.wakeonlan;

import androidx.annotation.Nullable;

public class LanDevice {

    private String mMac;
    private String mAddress;
    private String mHostname;
    private String mVendor;

    public LanDevice(String hostname, String mac, String address, String vendor) {
        mHostname = hostname;
        mMac = mac;
        mAddress = address;
        mVendor = vendor;
    }

    public String getVendor() {
        return mVendor;
    }

    public String getMac() {
        return mMac;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getHostname() {
        return mHostname;
    }

    public void setHostname(String hostname) {
        mHostname = hostname;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof LanDevice)) {
            return false;
        }

        LanDevice other = (LanDevice) obj;
        return other.getMac().equals(mMac);
    }

    @Override
    public String toString() {
        return "LanDevice{" +
                "mac='" + mMac + '\'' +
                ", address='" + mAddress + '\'' +
                ", hostname='" + mHostname + '\'' +
                '}';
    }
}