package com.wakeonlan;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MacVendors {

    private static final String VENDORS_DB_FILE = "mac_vendors.json";

    private static HashMap<String, String> sOuiVendors = null;


    private MacVendors() { }

    public static void init(Context context) throws IOException {
        if (sOuiVendors == null) {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(MacVendors.VENDORS_DB_FILE);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Gson gson = new Gson();
            sOuiVendors = gson.fromJson(bufferedReader, HashMap.class);
        }
    }

    public static void show() {
        if (sOuiVendors != null) {
            for (Map.Entry<String, String> entry : sOuiVendors.entrySet()) {
                Log.d(MainActivity.TAG, entry.getKey() + "->" + entry.getValue());
            }
        }
    }

    public static String lookup(String oui) {
        if (sOuiVendors != null) {
            return sOuiVendors.get(oui.toUpperCase());
        }
        return null;
    }

    public static String lookup(byte[] mac) {
        if (sOuiVendors != null) {
            if (mac != null && mac.length == 6) {
                String oui = getOui(mac);
                if (oui != null)
                    return sOuiVendors.get(oui);
            }
        }
        return null;
    }

    public static String getOui(String mac) {
        if (mac != null) {
            String[] oct = mac.split(":|-");
            if (oct.length == 6) {
                String oui = oct[0] + "" + oct[1] + "" + oct[2];
                return oui.toUpperCase();
            }
        }
        return null;
    }

    public static String getOui(byte[] mac) {
        if (mac != null && mac.length == 6) {
            String oui = String.format("%02x%02x%02x",
                    mac[0], mac[1], mac[2]);
            if (oui != null) {
                return oui.toUpperCase();
            }
        }
        return null;
    }
}
