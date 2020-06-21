package com.wakeonlan;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefsSettings {

    private static SharedPreferences sPrefsSavedDevicesConfig;
    private static SharedPreferences.Editor sPrefSavedDevicesConfigEditor;

    private SharedPrefsSettings() {
    }

    public static void init(Context context) {
        if (sPrefsSavedDevicesConfig == null) {
            sPrefsSavedDevicesConfig = context.getSharedPreferences(
                    context.getPackageName() + ".theme",
                    Activity.MODE_PRIVATE);
            sPrefSavedDevicesConfigEditor = sPrefsSavedDevicesConfig.edit();
        }
    }

    public static boolean isThemeDark() {
        return sPrefsSavedDevicesConfig.getBoolean("darkTheme", false);
    }

    public static void setTheme(boolean darkTheme) {
        sPrefSavedDevicesConfigEditor.putBoolean("darkTheme", darkTheme);
        sPrefSavedDevicesConfigEditor.commit();
    }
}
