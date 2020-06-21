package com.wakeonlan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class EditDeviceActivity extends BaseDeviceActivity {

    public static final String TAG = "WakeOnLan.EditDevice";

    private int mCurrentDeviceID;
    private String mCurrentDeviceName;
    private String mCurrentMAC;
    private int mCurrentPort;
    private String mCurrentIp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(SharedPrefsSettings.isThemeDark() ? R.style.AppThemeDark : R.style.AppThemeLight);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        mCurrentDeviceID = intent.getIntExtra(EXTRA_DEVICE_ID, 0);
        mCurrentDeviceName = intent.getStringExtra(EXTRA_DEVICE_NAME);
        mCurrentMAC = intent.getStringExtra(EXTRA_DEVICE_MAC);
        mCurrentPort = intent.getIntExtra(EXTRA_DEVICE_PORT, -1);
        mCurrentIp = intent.getStringExtra(EXTRA_DEVICE_ADDRESS);

        if (mCurrentIp == null) {
            mCurrentIp = "";
        }

        if (mCurrentDeviceID != 0) {
            Toast.makeText(getApplicationContext(),
                    "device ID=" + mCurrentDeviceID, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "device ID is 0", Toast.LENGTH_SHORT).show();
        }

        mTextInputDeviceName.getEditText().setText(mCurrentDeviceName);
        mTextInputMAC.getEditText().setText(mCurrentMAC);
        if (mCurrentPort != -1) {
            mTextInputPortNumber.getEditText().setText(String.valueOf(mCurrentPort));
        }
        if (!TextUtils.isEmpty(mCurrentIp)) {
            mTextInputIpAddress.getEditText().setText(mCurrentIp);
        }
        mConfirmButton.setText(getResources().getString(R.string.input_save_confirm_button));
    }

    @Override
    public void confirmInput(View v) {
        if (!validateAll()) return;

        String deviceName = null;
        String mac = null;
        String ip = null;
        int port = 0;

        deviceName = getDeviceName();
        mac = getMAC();
        port = getPort();
        ip = getIp();

        Log.d(TAG, EXTRA_DEVICE_NAME + " - " + deviceName + " " + mCurrentDeviceName);
        Log.d(TAG, EXTRA_DEVICE_MAC + " - " + mac + " " + mCurrentMAC);
        Log.d(TAG, EXTRA_DEVICE_PORT + " - " + port + " " + mCurrentPort);
        Log.d(TAG, EXTRA_DEVICE_ADDRESS + " - " + ip + " " + mCurrentIp);

        Intent returnIntent = new Intent();
        if (!deviceName.equals(mCurrentDeviceName)) {
            returnIntent.putExtra(EXTRA_DEVICE_NAME, deviceName);
        }
        if (!mac.equals(mCurrentMAC)) {
            returnIntent.putExtra(EXTRA_DEVICE_MAC, mac);
        }
        if (port != mCurrentPort) {
            if (port != -1) {
                returnIntent.putExtra(EXTRA_DEVICE_PORT, port);
            } else {
                returnIntent.putExtra(EXTRA_DEVICE_PORT, 0);
            }
        }
        if (!ip.equals(mCurrentIp)) {
            returnIntent.putExtra(EXTRA_DEVICE_ADDRESS, ip);
        }

        if (returnIntent.getExtras() != null) {
            Log.d(TAG, "Something changed" + returnIntent.getExtras().toString());
            returnIntent.putExtra(EXTRA_DEVICE_ID, mCurrentDeviceID);
            setResult(Activity.RESULT_OK, returnIntent);

        } else {
            Log.d(TAG, "Nothing changed");
        }
        finish();
    }
}
