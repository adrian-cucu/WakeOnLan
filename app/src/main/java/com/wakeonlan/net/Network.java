package com.wakeonlan.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;


import org.apache.commons.net.util.SubnetUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;


public class Network {

    public static final String TAG = "WakeOnLan.Network";

    private ConnectivityManager mConnectivityManager;
    private WifiManager mWifiManager;
    private DhcpInfo mDhcpInfo;
    private WifiInfo mWifiInfo;

    private NetworkInterface mNetworkInterface;
    private IPv4Address mLocal;
    private IPv4Address mNetmask;
    private IPv4Address mBroadcast;
    private IPv4Address mNetwork;
    private IPv4Address mGateway;
    private int mNetworkPrefix;


    public Network(Context context)
            throws SocketException, UnknownHostException {
        mWifiManager = (WifiManager)
                context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mDhcpInfo = mWifiManager.getDhcpInfo();
        mWifiInfo = mWifiManager.getConnectionInfo();
        initNetwork();
    }

    private void initNetwork() throws SocketException, UnknownHostException {
        mLocal = new IPv4Address(mDhcpInfo.ipAddress);
        try {
            mGateway = new IPv4Address(mDhcpInfo.gateway);
        } catch (UnknownHostException e) {
            mGateway = null;
        }

        mNetworkInterface = NetworkInterface.getByInetAddress(mLocal.getInetAddress());

        if (mNetworkInterface == null)
            throw new UnknownHostException("network not found");

        for (InterfaceAddress ifaceAddress : mNetworkInterface.getInterfaceAddresses()) {
            if (ifaceAddress.getAddress() instanceof Inet4Address) {
                if (ifaceAddress.getAddress().equals(mLocal.getInetAddress())) {
                    InetAddress bcast = ifaceAddress.getBroadcast();
                    mNetworkPrefix = ifaceAddress.getNetworkPrefixLength();
                    mNetmask = new IPv4Address(NetUtils.getNetmaskFromPrefixLength(mNetworkPrefix));
                    mBroadcast = new IPv4Address(bcast.getAddress());
                    mNetwork = new IPv4Address(mLocal.getInAddr() & mNetmask.getInAddr());
                }
            }
        }
    }

    public boolean isInternalAddress(InetAddress address) {
        if (address != null) {
            byte[] networkAddr = mNetwork.getRawAddress();
            byte[] mask = mNetmask.getRawAddress();
            byte[] bytesAddr = address.getAddress();

            for (int i = 0; i < networkAddr.length; ++i)
                if ((networkAddr[i] & mask[i]) != (bytesAddr[i] & mask[i]))
                    return false;

            return true;
        }
        return false;
    }

    public boolean isInternalAddress(IPv4Address address) {
        if (address != null) {
            return (address.getInAddr() & mNetmask.getInAddr()) == mNetwork.getInAddr();
        }
        return false;
    }

    public String getNetworkInterfaceName() {
        return mNetworkInterface.getDisplayName();
    }

    public DhcpInfo getDhcpInfo() {
        return mDhcpInfo;
    }

    public WifiInfo getWifiInfo() {
        return mWifiInfo;
    }

    public InetAddress getNetworkLocalAddress() {
        return mLocal.getInetAddress();
    }

    public InetAddress getNetworkMask() {
        return mNetmask.getInetAddress();
    }

    public InetAddress getBroadcastAddr() {
        return mBroadcast.getInetAddress();
    }

    public String getNetworkMasked() {
        return mNetwork.toString();
    }

    public void make() {

        long t0, t1;

        t0 = System.currentTimeMillis();
        int networkAddress = mNetwork.getInAddr();
        int bcastAddress = mBroadcast.getInAddr();

        int firstAddress = NetUtils.hostToNetworkByteOrder(networkAddress) + 1;
        int lastAddress = NetUtils.hostToNetworkByteOrder(bcastAddress) - 1;
        try {
            Log.d(TAG, "first address: " + NetUtils.makeInetAddress(
                    NetUtils.networkToHostByteOrder(firstAddress)));
            Log.d(TAG, "last address: " + NetUtils.makeInetAddress(
                    NetUtils.networkToHostByteOrder(lastAddress)));

            for (int address = firstAddress; address <= lastAddress; ++address) {
                if (Math.abs((address - firstAddress) - (lastAddress - address)) <= 2 ) {
                    IPv4Address addr = new IPv4Address(NetUtils.networkToHostByteOrder(address));
                    Log.d(TAG, "middle: " + addr.toString() + " is internal " + isInternalAddress(addr));
                }
            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        t1 = System.currentTimeMillis();
        Log.d(TAG, "took: " + (t1 - t0));

        t0 = System.currentTimeMillis();
        SubnetUtils s = new SubnetUtils(mNetwork + "/" + mNetworkPrefix);
        Log.d(TAG, s.getInfo().getLowAddress() + " to " + s.getInfo().getHighAddress());
        String[] aa = s.getInfo().getAllAddresses();
        t1 = System.currentTimeMillis();
        Log.d(TAG, "took: " + (t1 - t0));
    }

//    public SubnetUtils.SubnetInfo getSubnet() {
//        SubnetUtils sub = new SubnetUtils(mLocalAddr.getHostAddress(), mNetmaskAddr.getHostAddress());
//        return sub.getInfo();
//    }


    public int getNetworPrefixLength() {
        return mNetworkPrefix;
    }

    @Override
    public String toString() {
        return "Network{" +
                "networkInterface:" + mNetworkInterface.getDisplayName() +
                ", network:" + mNetwork + "/" + mNetworkPrefix +
                ", inet addr:" + mLocal +
                ", bcast:" + mBroadcast +
                ", mask:" + mNetmask +
                ", gateway:" + mGateway +
                '}';
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null)
                return info.isConnected() && info.isAvailable();
        }
        return false;
    }

    public static boolean isConnectivityAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null)
                return info.isConnected();
        }
        return false;
    }


}
