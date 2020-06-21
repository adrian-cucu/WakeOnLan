package com.wakeonlan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;


public class AddDeviceActivity extends BaseDeviceActivity {

    public static final String TAG = "WakeOnLan.AddDevice";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(SharedPrefsSettings.isThemeDark() ? R.style.AppThemeDark : R.style.AppThemeLight);
        super.onCreate(savedInstanceState);
        mConfirmButton.setText(getResources().getString(R.string.input_add_confirm_button));
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmInput(v);
            }
        });
    }

    @Override
    public void confirmInput(View v) {
        if (validateAll()) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_DEVICE_NAME, getDeviceName());
            resultIntent.putExtra(EXTRA_DEVICE_MAC, getMAC());
            int port = getPort();
            if (port != -1) {
                resultIntent.putExtra(EXTRA_DEVICE_PORT, port);
            }
            String ip = getIp();
            if (!TextUtils.isEmpty(ip)) {
                resultIntent.putExtra(EXTRA_DEVICE_ADDRESS, ip);
            }
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
