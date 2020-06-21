package com.wakeonlan;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ProgressBar;

import org.apache.commons.net.util.SubnetUtils;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.WIFI_SERVICE;

public class Utils {

    public static final String TAG = "Utils.WakeOnLan";


    public static String getWifiBroadcastAddress(Context context) {

        WifiManager wifiMgr;
        DhcpInfo dhcpInfo;
        int broadcast;

        wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiMgr == null || !wifiMgr.isWifiEnabled()) {
            return null;
        }

        dhcpInfo = wifiMgr.getDhcpInfo();
        if (dhcpInfo == null) {
            return null;
        }

        if (dhcpInfo.netmask == 0) {
            try {
                Log.d(MainActivity.TAG, "netmask = 0");
                InetAddress inetAddress = Utils.makeInetAddress(dhcpInfo.ipAddress);
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);

                for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                    short netPrefix = address.getNetworkPrefixLength();

                    if (0 < netPrefix && netPrefix <= 32 && address.getBroadcast() != null) {
//                        getNetmaskFromPrefixLength(netPrefix);
                        broadcast = dhcpInfo.gateway | ~getNetmaskFromPrefixLength(netPrefix);
                        return Formatter.formatIpAddress(broadcast);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {

            try {
                Log.d(MainActivity.TAG, "netmask != 0: " +
                        Utils.makeInetAddress(dhcpInfo.netmask).getHostAddress());

                Log.d(MainActivity.TAG, "address: " +
                        Utils.makeInetAddress(dhcpInfo.ipAddress).getHostAddress());

                Log.d(MainActivity.TAG, "address: " +
                        Utils.makeInetAddress(dhcpInfo.ipAddress | (~dhcpInfo.netmask)).getHostAddress());

//                Log.d(MainActivity.TAG, "gateway: " +
//                        Utils.makeInetAddress(dhcpInfo.gateway).getHostAddress());
//
//                Log.d(MainActivity.TAG, "server: " +
//                        Utils.makeInetAddress(dhcpInfo.serverAddress).getHostAddress());
//                Log.d(MainActivity.TAG, "hdchInfo: " + dhcpInfo.toString());

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (NoRouteToHostException e) {
                e.printStackTrace();
            }
        }

        broadcast = dhcpInfo.ipAddress | (~dhcpInfo.netmask);
        return Formatter.formatIpAddress(broadcast);
    }

    public static String getWifiSubnetAddress(Context context) {
        WifiManager wifiMgr;
        DhcpInfo dhcpInfo;
        int broadcast;

        wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiMgr == null || !wifiMgr.isWifiEnabled()) {
            return null;
        }

        dhcpInfo = wifiMgr.getDhcpInfo();
        if (dhcpInfo == null) {
            return null;
        }

        if (dhcpInfo.netmask == 0) {
            try {
                InetAddress inetAddress = Utils.makeInetAddress(dhcpInfo.ipAddress);
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);

                for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                    short netPrefix = address.getNetworkPrefixLength();

                    if (0 < netPrefix && netPrefix <= 32 && address.getBroadcast() != null) {
//                        getNetmaskFromPrefixLength(netPrefix);
                        broadcast = dhcpInfo.gateway & getNetmaskFromPrefixLength(netPrefix);
                        return Formatter.formatIpAddress(broadcast);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        broadcast = dhcpInfo.ipAddress & dhcpInfo.netmask;
        return Formatter.formatIpAddress(broadcast);
    }

    public static String getWifiSubnetMask(Context context) {
        WifiManager wifiMgr;
        DhcpInfo dhcpInfo;
        int broadcast;

        wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiMgr == null || !wifiMgr.isWifiEnabled()) {
            return null;
        }

        dhcpInfo = wifiMgr.getDhcpInfo();
        if (dhcpInfo == null) {
            return null;
        }

        if (dhcpInfo.netmask == 0) {
            try {
                InetAddress inetAddress = Utils.makeInetAddress(dhcpInfo.ipAddress);
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);

                for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                    short netPrefix = address.getNetworkPrefixLength();

                    if (0 < netPrefix && netPrefix <= 32 && address.getBroadcast() != null) {
                        return Formatter.formatIpAddress(getNetmaskFromPrefixLength(netPrefix));
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        return Formatter.formatIpAddress(dhcpInfo.netmask);
    }

    public static String getWifiGatewayAddress(Context context) {
        WifiManager wifiMgr;
        DhcpInfo dhcpInfo;

        wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiMgr == null || !wifiMgr.isWifiEnabled()) {
            return null;
        }

        dhcpInfo = wifiMgr.getDhcpInfo();
        if (dhcpInfo == null) {
            return null;
        }

        return Formatter.formatIpAddress(dhcpInfo.gateway);
    }

    public static String getWifiAddress(Context context) {
        WifiManager wifiMgr;
        DhcpInfo dhcpInfo;

        wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiMgr == null || !wifiMgr.isWifiEnabled()) {
            return null;
        }

        dhcpInfo = wifiMgr.getDhcpInfo();
        if (dhcpInfo == null) {
            return null;
        }

        return Formatter.formatIpAddress(dhcpInfo.ipAddress);
    }

    public static boolean haveNetworkConnection(Context context) {

        ConnectivityManager cm = (ConnectivityManager)
                context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Network network = cm.getActiveNetwork();
            if (network == null) {
                return false;
            }

            NetworkCapabilities networkCap = cm.getNetworkCapabilities(network);
            if (networkCap == null) {
                return false;
            }
            if (networkCap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true;
            }
            if (networkCap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true;
            }
            return false;
        } else {
            if (cm.getActiveNetwork() == null) {
                return false;
            }
            return cm.getActiveNetworkInfo().isConnected();
        }
    }

    public static boolean haveWifiConnection(Context context) {

        ConnectivityManager cm = (ConnectivityManager)
                context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Network network = cm.getActiveNetwork();
            if (network == null) {
                return false;
            }

            NetworkCapabilities networkCap = cm.getNetworkCapabilities(network);
            if (networkCap == null) {
                return false;
            }
            if (networkCap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true;
            }
            return false;
        } else {
            if (cm.getActiveNetwork() == null) {
                return false;
            }
            return cm.getActiveNetworkInfo().isConnected();
        }
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return info != null && info.isConnected() && info.isAvailable();
    }

    public static boolean isConnectivityAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        return info != null && info.isConnected();
    }
//
//    public static void getNetworkInterface(Context context) throws UnknownHostException {
//
//        WifiManager wifiManager = null;
//        DhcpInfo dhcpInfo = null;
//        NetworkInterface netInterface = null;
//
//        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        if (wifiManager != null) {
//            dhcpInfo = wifiManager.getDhcpInfo();
//
//            if (dhcpInfo != null) {
//
//                InetAddress localAddress = Utils.makeInetAddress(dhcpInfo.ipAddress);
//                Log.e(MainActivity.TAG, localAddress.getHostAddress());
//
//                try {
//                    netInterface = NetworkInterface.getByInetAddress(localAddress);
//                    if (netInterface == null)
//                        throw new IllegalStateException("Error retrieving network interface.");
//
//                    Log.d(MainActivity.TAG, netInterface.getDisplayName() + localAddress.getHostAddress());
//
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


    public static int getNetmaskFromPrefixLength(int networkPrefixLength) {
        int netmask = -(1 << (32 - networkPrefixLength));

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            netmask = Integer.reverseBytes(netmask);
        }
        return  netmask;
    }

    public static InetAddress makeInetAddress(int address)
            throws UnknownHostException, NoRouteToHostException {
        InetAddress inetAddress;
        byte[] byteAddr = new byte[4];

        if (address == 0)
            throw new NoRouteToHostException("Not connected to any network.");

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            byteAddr[0] = (byte) (address & 0xFF);
            byteAddr[1] = (byte) (0xFF & address >> 8);
            byteAddr[2] = (byte) (0xFF & address >> 16);
            byteAddr[3] = (byte) (0xFF & address >> 24);
        } else {
            byteAddr[0] = (byte) (0xFF & address >> 24);
            byteAddr[1] = (byte) (0xFF & address >> 16);
            byteAddr[2] = (byte) (0xFF & address >> 8);
            byteAddr[3] = (byte) (address & 0xFF);
        }

        inetAddress = InetAddress.getByAddress(byteAddr);
        return inetAddress;
    }

    public static void listNetworkInterfaces() {
        try {
            Enumeration<NetworkInterface> interfaces =
                    NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                // drop inactive

                if (!isIfaceConnected(networkInterface))
                    continue;

                Enumeration<InetAddress> addresses =
                        networkInterface.getInetAddresses();

                for (InterfaceAddress ifaceAddress :
                        networkInterface.getInterfaceAddresses()) {

                    InetAddress addr = ifaceAddress.getAddress();
                    InetAddress bcast = ifaceAddress.getBroadcast();

                    if (addr instanceof Inet4Address) {
                        Inet4Address ipv4Address = (Inet4Address) addr;
                        ipv4Address.getAddress();

                    } else if (addr instanceof Inet6Address) {
                        Inet6Address ipv6Address = (Inet6Address) addr;
                        ipv6Address.getAddress();

                    }

                    Log.d(TAG, String.format(
                            "NetInterface: name [%s], %s ipv6 [%s] %s %s broadcast: %s, prefix: %d",
                            networkInterface.getDisplayName(),
                            parseMacAddress(networkInterface.getHardwareAddress()),
                            addr.getHostAddress(),
                            addr.getHostName(),
                            addr.getCanonicalHostName(),
                            bcast != null ? bcast.getHostName() : "no broadcast",
                            ifaceAddress.getNetworkPrefixLength()));
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static final String ARP_CACHE_FILE = "/proc/net/arp";
    private static File mArpCacheFile = null;
    private static long mArpCacheFileLastModified = 0;

    private static HashMap<String, String> mArpCache = null;


    public static synchronized String findMac2(String address) {
        if (mArpCacheFile == null) {
            mArpCacheFile = new File(ARP_CACHE_FILE);
        }

        long modifiedTs = mArpCacheFile.lastModified();
        if (modifiedTs == mArpCacheFileLastModified) {
            return mArpCache.get(address);
        }
        mArpCacheFileLastModified = modifiedTs;
        mArpCache = getArpCache();
        return mArpCache.get(address);
    }

    public static synchronized String findMac3(String address) {
        File arpCacheFile = new File(ARP_CACHE_FILE);

        long modifiedTs = arpCacheFile.lastModified();
        if (modifiedTs == mArpCacheFileLastModified) {
            return mArpCache.get(address);
        }
        mArpCacheFileLastModified = modifiedTs;
        mArpCache = getArpCache();
        return mArpCache.get(address);
    }

    public static String findMac(String address) {
        BufferedReader bufferedReader = null;
        String line = null;

        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));

            while ((line = bufferedReader.readLine()) != null) {
//                Log.d(TAG, line);

                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    String mac = splitted[3];

                    if (!mac.equals("00:00:00:00:00:00") && ip.equals(address)) {
                        return mac;
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static String getClientMacByIP(String ip) {
        String res = "nulll";
        if (ip == null)
            return res;

        String flushCmd = "sh ip -s -s neigh flush all";
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(flushCmd, null, new File("/proc/net"));
        } catch (Exception e) {
            Log.e(MainActivity.TAG, e.getMessage());
        }

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
//                Log.d(MainActivity.TAG, line);
                String[] sp = line.split(" +");
                if (sp.length >= 4 && ip.equals(sp[0])) {
                    //Assistance.Log(sp[0]+sp[2]+sp[3],ALERT_STATES.ALERT_STATE_LOG);
                    String mac = sp[3];
                    if (mac.matches("..:..:..:..:..:..") && sp[2].equals("0x2")) {
                        res = mac;
                        break;
                    }
                }
            }

            br.close();
        } catch (Exception e) {
            Log.e(MainActivity.TAG, e.getMessage());
        }

        return res;
    }

    public static HashMap<String, String> getArpCache() {
        BufferedReader bufferedReader = null;
        HashMap<String, String> arpEntry = new HashMap();

        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));

            String line;
            // skip first line
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
//                Log.d(TAG, line);

                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    String mac = splitted[3];

                    if (!mac.equals("00:00:00:00:00:00")) {
//                        arpEntry.put(mac, ip);
                        arpEntry.put(ip, mac);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        for (String mac : arpEntry.keySet()) {
//            String ip = arpEntry.get(mac);
//            Log.d(MainActivity.TAG, mac + "  - " + ip);
//        }
        return arpEntry;
    }

    public static String parseMacAddress(byte[] byteMac) {
        if (byteMac == null || byteMac.length != 6) {
            return null;
        }

        return String.format("%02x:%02x:%02x:%02x:%02x:%02x",
                byteMac[0], byteMac[1], byteMac[2],
                byteMac[3], byteMac[4], byteMac[5]);
    }

    public static String getMacOui(String mac) {
        if (mac == null) {
            return null;
        }

        String[] macHex = mac.split("\\:");
        if (macHex.length != 6) {
            return null;
        }

        return macHex[0].toUpperCase() +
            macHex[1].toUpperCase() +
            macHex[2].toUpperCase();
    }

    public static boolean isIfaceConnected(NetworkInterface networkInterface) {
        try {
            return networkInterface.isUp() && !networkInterface.isLoopback() &&
                    !networkInterface.getInterfaceAddresses().isEmpty();
        } catch (SocketException e) {
            return false;
        }
    }
}
