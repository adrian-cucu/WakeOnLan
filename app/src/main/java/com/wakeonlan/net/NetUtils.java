package com.wakeonlan.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;


public final class NetUtils {

    public static int getNetmaskFromPrefixLength(int networkPrefixLength) {
        int netmask = -(1 << (32 - networkPrefixLength));

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            netmask = Integer.reverseBytes(netmask);
        }
        return netmask;
    }

    public static int makeInetIntAddress(byte[] byteAddress)
            throws UnknownHostException {
        int address = 0;

        if (byteAddress == null || byteAddress.length != IPv4Address.ADDR_LEN) {
            throw new UnknownHostException("Invalid address");
        }

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            address = (int) (byteAddress[0] & 0xFF)
                    | (int) (byteAddress[1] & 0xFF) << 8
                    | (int) (byteAddress[2] & 0xFF) << 16
                    | (int) (byteAddress[3] & 0xFF) << 24;
        } else {
            address = (int) (byteAddress[0] & 0xFF) << 24
                    | (int) (byteAddress[1] & 0xFF) << 16
                    | (int) (byteAddress[2] & 0xFF) << 8
                    | (int) (byteAddress[3] & 0xFF);
        }

        return address;
    }

    public static InetAddress makeInetAddress(int address)
            throws UnknownHostException {
        InetAddress inetAddress;
        byte[] byteAddr = null;

        if (address == 0) {
            throw new UnknownHostException("Invalid address: " + address);
        }

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            byteAddr = new byte[IPv4Address.ADDR_LEN];
            byteAddr[0] = (byte) (address & 0xFF);
            byteAddr[1] = (byte) (0xFF & address >> 8);
            byteAddr[2] = (byte) (0xFF & address >> 16);
            byteAddr[3] = (byte) (0xFF & address >> 24);
        } else {
            byteAddr = new byte[IPv4Address.ADDR_LEN];
            byteAddr[0] = (byte) (0xFF & address >> 24);
            byteAddr[1] = (byte) (0xFF & address >> 16);
            byteAddr[2] = (byte) (0xFF & address >> 8);
            byteAddr[3] = (byte) (address & 0xFF);
        }

        inetAddress = InetAddress.getByAddress(byteAddr);
        return inetAddress;
    }

    public static List<NetworkInterface> getNetworkInterfaces() {
        List<NetworkInterface> interfacesList;
        Enumeration<NetworkInterface> interfaces = null;

        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
//            Log.d(TAG, Log.getStackTraceString(e));
        }

        if (interfaces == null)
            return Collections.emptyList();

        interfacesList = new ArrayList<>();

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (isNetworkIfaceConnected(networkInterface))
                interfacesList.add(networkInterface);
        }
        return interfacesList;
    }

    public static boolean isNetworkIfaceConnected(NetworkInterface networkInterface) {
        try {
            if (networkInterface != null)
                return networkInterface.isUp() &&
                        !networkInterface.isLoopback() &&
                        !networkInterface.getInterfaceAddresses().isEmpty();
        } catch (SocketException e) {
            return false;
        }
        return false;
    }

    public static int hostToNetworkByteOrder(int address) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            return Integer.reverseBytes(address);
        }
        return address;
    }

    public static int networkToHostByteOrder(int address) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            return Integer.reverseBytes(address);
        }
        return address;
    }
}
