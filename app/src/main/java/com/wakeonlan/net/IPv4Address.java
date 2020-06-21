package com.wakeonlan.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class IPv4Address {

    public static final int ADDR_LEN = 4;

    private int mAddr;
    private byte[] mRawByteAddr;
    private InetAddress mInetAddr;
    private String mAddressString;

    public IPv4Address(int address) throws UnknownHostException {
        mAddr = address;
        mInetAddr = NetUtils.makeInetAddress(address);
        mRawByteAddr = Arrays.copyOf(mInetAddr.getAddress(), ADDR_LEN);
        mAddressString = mInetAddr.getHostAddress();
    }

    public IPv4Address(byte[] byteAddr) throws UnknownHostException {
        mAddr = NetUtils.makeInetIntAddress(byteAddr);
        mInetAddr = InetAddress.getByAddress(byteAddr);
        mRawByteAddr = Arrays.copyOf(mInetAddr.getAddress(), ADDR_LEN);
        mAddressString = mInetAddr.getHostAddress();
    }

    public byte[] getRawAddress() {
        return Arrays.copyOf(mRawByteAddr, ADDR_LEN);
    }

    public InetAddress getInetAddress() {
        return mInetAddr;
    }

    public int getInAddr() {
        return mAddr;
    }

    @Override
    public String toString() {
        return mAddressString;
    }
}
