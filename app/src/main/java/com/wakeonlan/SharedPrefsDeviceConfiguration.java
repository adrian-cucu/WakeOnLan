package com.wakeonlan;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;


public class SharedPrefsDeviceConfiguration {

    private static SharedPreferences sPrefsSavedDevicesConfig;
    private static SharedPreferences.Editor sPrefSavedDevicesConfigEditor;

    private SharedPrefsDeviceConfiguration() {
    }

    public static void init(Context context) {
        if (sPrefsSavedDevicesConfig == null) {
            sPrefsSavedDevicesConfig = context.getSharedPreferences(
                    context.getPackageName() + ".devices",
                    Activity.MODE_PRIVATE);
            sPrefSavedDevicesConfigEditor = sPrefsSavedDevicesConfig.edit();
        }
    }

    public static DeviceConfiguration saveDeviceConfig(
            String deviceName, String mac, String ip, int port) {
        if (sPrefSavedDevicesConfigEditor == null) {
            return null;
        }
        // Finds a new ID
        for (int id = 1; id < Integer.MAX_VALUE; ++id) {
            String keyID = Integer.toString(id);
            if (sPrefsSavedDevicesConfig.getString(keyID, null) == null) {
                Device device = new Device(id, mac, deviceName);
                DeviceConfiguration deviceConfig =
                        new DeviceConfiguration(device, ip, port);
                Gson gson = new Gson();
                String json = gson.toJson(deviceConfig);
                sPrefSavedDevicesConfigEditor.putString(keyID, json);
                if (sPrefSavedDevicesConfigEditor.commit()) {
                    return deviceConfig;
                }
            }
        }
        return null;
    }

    public static boolean remove(DeviceConfiguration deviceConfig) {
        if (sPrefSavedDevicesConfigEditor != null) {
            sPrefSavedDevicesConfigEditor.remove(String.valueOf(deviceConfig.getDevice().getID()));
            sPrefSavedDevicesConfigEditor.apply();
            return true;
        }
        return false;
    }

    public static boolean removeByID(int deviceConfigID) {
        if (sPrefSavedDevicesConfigEditor != null) {
            sPrefSavedDevicesConfigEditor.remove(String.valueOf(deviceConfigID));
            sPrefSavedDevicesConfigEditor.apply();
            return true;
        }
        return false;
    }

    public static boolean update(int devID, DeviceConfiguration updatedDevice) {
        if (sPrefSavedDevicesConfigEditor != null) {
            Gson gson = new Gson();
            String json = gson.toJson(updatedDevice);
            sPrefSavedDevicesConfigEditor.putString(
                    String.valueOf(updatedDevice.getDevice().getID()), json);
            return sPrefSavedDevicesConfigEditor.commit();
        }
        return false;
    }

    public static boolean update(DeviceConfiguration deviceConfig) {
        if (sPrefSavedDevicesConfigEditor != null) {
            String key = String.valueOf(deviceConfig.getDevice().getID());
            Gson gson = new Gson();
            String json = gson.toJson(deviceConfig);
            sPrefSavedDevicesConfigEditor.putString(key, json);
            return sPrefSavedDevicesConfigEditor.commit();
        }
        return false;
    }

    public static DeviceConfiguration getByKey(String key) {
        if (sPrefsSavedDevicesConfig != null) {
            String json = sPrefsSavedDevicesConfig.getString(key, null);
            if (json != null) {
                Gson gson = new Gson();
                DeviceConfiguration deviceConfig = gson.fromJson(json, DeviceConfiguration.class);
                return deviceConfig;
            }
        }
        return null;
    }

    public static ArrayList<DeviceConfiguration> loadDevicesList() {
        if (sPrefSavedDevicesConfigEditor != null) {
            Map<String, ?> allEntries = sPrefsSavedDevicesConfig.getAll();
            if (allEntries != null) {
                ArrayList<DeviceConfiguration> devices = new ArrayList<>();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    Gson gson = new Gson();
                    String json = (String) entry.getValue();

                    DeviceConfiguration deviceConfig =
                            gson.fromJson(json, DeviceConfiguration.class);
                    devices.add(deviceConfig);
                }
                return devices;
            }
        }
        return null;
    }
}