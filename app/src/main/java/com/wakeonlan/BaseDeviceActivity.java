package com.wakeonlan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseDeviceActivity extends AppCompatActivity {

    public static final String TAG = "WakeOnLan.BaseDevice";

    public static final String EXTRA_DEVICE_ID = "deviceID";
    public static final String EXTRA_DEVICE_NAME = "deviceName";
    public static final String EXTRA_DEVICE_MAC = "mac";
    public static final String EXTRA_DEVICE_PORT = "port";
    public static final String EXTRA_DEVICE_ADDRESS = "ip";

    public static final int DEFAULT_PORT_NUMBER = 9;

    protected TextInputLayout mTextInputDeviceName;
    protected TextInputLayout mTextInputMAC;
    protected TextInputLayout mTextInputPortNumber;
    protected TextInputLayout mTextInputIpAddress;
    protected TextInputLayout mTextInputSecureOnPassword;
    protected AppCompatButton mConfirmButton;
    protected AppCompatButton mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_device);

        Log.d(TAG, "onCreate");

        mTextInputDeviceName = (TextInputLayout) findViewById(R.id.text_input_device_name);
        mTextInputMAC = (TextInputLayout) findViewById(R.id.text_input_mac);
        mTextInputPortNumber = (TextInputLayout) findViewById(R.id.text_input_port);
        mTextInputIpAddress = (TextInputLayout) findViewById(R.id.text_input_ip);
//        mTextInputSecureOnPassword = (TextInputLayout)
//                findViewById(R.id.text_input_secure_on_password);

        registerMacHintCallback(mTextInputMAC.getEditText());
        registerAfterMacTextChangedCallback(mTextInputMAC.getEditText());
//        registerMacHintCallback(mTextInputSecureOnPassword.getEditText());
//        registerAfterMacTextChangedCallback(mTextInputSecureOnPassword.getEditText());
//        registerPortHintCallback(mTextInputPortNumber.getEditText());

        mTextInputPortNumber.getEditText().setHint(getResources().getString(R.string.input_hint_port));
        mTextInputIpAddress.getEditText().setHint(getResources().getString(R.string.input_hint_ip));

//        if (Utils.getWifiBroadcastAddress(getApplicationContext()) != null) {
//            mTextInputIpAddress.getEditText().setText(
//                    Utils.getWifiBroadcastAddress(getApplicationContext()));
//        }

        mConfirmButton = (AppCompatButton) findViewById(R.id.confirm_button);

        mBackButton = (AppCompatButton) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            if (intent.getStringExtra(EXTRA_DEVICE_NAME) != null) {
                mTextInputDeviceName.getEditText().setText(intent.getStringExtra(EXTRA_DEVICE_NAME));
            }
            if (intent.getStringExtra(EXTRA_DEVICE_MAC) != null) {
                mTextInputMAC.getEditText().setText(intent.getStringExtra(EXTRA_DEVICE_MAC));
            }
            if (intent.getStringExtra(EXTRA_DEVICE_PORT) != null) {
                mTextInputPortNumber.getEditText().setText(
                        String.valueOf(intent.getStringExtra(EXTRA_DEVICE_PORT)));
            }
            if (intent.getStringExtra(EXTRA_DEVICE_ADDRESS) != null) {
                mTextInputIpAddress.getEditText().setText(intent.getStringExtra(EXTRA_DEVICE_ADDRESS));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    protected boolean validateDeviceName() {
        String deviceName = getDeviceName();
        if (TextUtils.isEmpty(deviceName)) {
            mTextInputDeviceName.setError(getResources().getString(R.string.error_empty_input_device_name));
            return false;
        }
        mTextInputDeviceName.setError(null);
        mTextInputDeviceName.setErrorEnabled(false);
        return true;
    }

    protected boolean validateMAC() {
        String mac = getMAC();
        if (TextUtils.isEmpty(mac)) {
            mTextInputMAC.setError(
                    getResources().getString(R.string.error_empty_input_mac));
            return false;
        }
//        Pattern p = Pattern.compile("^([a-fA-F0-9][:-]){5}[a-fA-F0-9][:-]$");
        Pattern p = Pattern.compile(
                "^([0-9A-Fa-f]{2}:){5}(([0-9A-Fa-f]{2}:){14})?([0-9A-Fa-f]{2})$");
        Matcher m = p.matcher(mac);
        if (!m.find()) {
            mTextInputMAC.setError(
                    getResources().getString(R.string.error_invalid_input_mac));
            return false;
        }

        mTextInputMAC.setError(null);
        mTextInputMAC.setErrorEnabled(false);
        return true;
    }

    protected boolean validatePortNumber() {
        try {
            String portInput = getTextInput(mTextInputPortNumber);
            if (!TextUtils.isEmpty(portInput)) {
                int port = Integer.parseInt(portInput);
            }
        } catch (NumberFormatException e) {
            Log.e(MainActivity.TAG, e.getMessage());
            mTextInputPortNumber.setError(
                    getResources().getString(R.string.error_invalid_input_port));
            return false;
        }
        mTextInputPortNumber.setError(null);
        mTextInputPortNumber.setErrorEnabled(false);
        return true;
    }

    protected boolean validateIP() {
        String ip = getIp();
        if (!TextUtils.isEmpty(ip)) {
            if (!Patterns.IP_ADDRESS.matcher(ip).matches()) {
                mTextInputIpAddress.setError(
                        getResources().getString(R.string.error_invalid_input_ip));
                return false;
            }
        }
        mTextInputIpAddress.setError(null);
        mTextInputIpAddress.setErrorEnabled(false);
        return true;
    }


    protected String getDeviceName() {
        return getTextInput(mTextInputDeviceName);
    }

    protected String getMAC() {
        return getTextInput(mTextInputMAC);
    }

    protected int getPort() {
        try {
            String portInput = getTextInput(mTextInputPortNumber);
            if (!TextUtils.isEmpty(portInput)) {
                int port = Integer.parseInt(portInput);
                return port;
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        return -1;
    }

    protected String getIp() {
        return getTextInput(mTextInputIpAddress);
    }

    protected String getTextInput(TextInputLayout textInputLayout) {
        String input = textInputLayout
                .getEditText()
                .getText()
                .toString();
        if (!TextUtils.isEmpty(input)) {
            return input.trim();
        }
        return input;
    }

    public abstract void confirmInput(View v);

    protected boolean validateAll() {

        boolean valid = true;

        valid &= validateDeviceName();
        valid &= validateMAC();
        valid &= validatePortNumber();
        valid &= validateIP();
        return valid;
    }

    private void registerMacHintCallback(final EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (TextUtils.isEmpty(editText.getText().toString())) {
                        editText.setHint("00:00:00:00:00:00");
                    }
                } else {
                    editText.setHint("");
                }
            }
        });
//        Toast.makeText(getApplicationContext(), editText.getHint(), Toast.LENGTH_LONG).show();
////        editText.setTextColor(getResources().getColor(R.color.textColorHint1));
//        editText.setHintTextColor(getResources().getColor(R.color.textColorHint2    ));
//        editText.setLinkTextColor(getResources().getColor(R.color.textColorHint2    ));
    }

    private void registerPortHintCallback(final EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (TextUtils.isEmpty(editText.getText().toString())) {
                        mTextInputPortNumber.setHint(getResources().getString(R.string.input_hint_port));
                        editText.setHint("");
                    }
                } else {
                    if (TextUtils.isEmpty(editText.getText().toString())) {
                        mTextInputPortNumber.setHint("");
                        editText.setHint(getResources().getString(R.string.input_port_text));
                    }
                }
            }
        });
//        Toast.makeText(getApplicationContext(), editText.getHint(), Toast.LENGTH_LONG).show();
////        editText.setTextColor(getResources().getColor(R.color.textColorHint1));
//        editText.setHintTextColor(getResources().getColor(R.color.textColorHint2    ));
//        editText.setLinkTextColor(getResources().getColor(R.color.textColorHint2    ));
    }

    /**
     * Registers TextWatcher for MAC EditText field. Automatically adds colons,
     * switches the MAC to upper case and handles the cursor position.
     */
    private void registerAfterMacTextChangedCallback(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            String mPreviousMac = null;

            /* (non-Javadoc)
             * Does nothing.
             * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
             */
            @Override
            public void afterTextChanged(Editable arg0) {
            }

            /* (non-Javadoc)
             * Does nothing.
             * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
             */
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            /* (non-Javadoc)
             * Formats the MAC address and handles the cursor position.
             * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
             */
            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredMac = editText.getText().toString().toUpperCase();
                String cleanMac = clearNonMacCharacters(enteredMac);
                String formattedMac = formatMacAddress(cleanMac);

                int selectionStart = editText.getSelectionStart();
                formattedMac = handleColonDeletion(enteredMac, formattedMac, selectionStart);
                int lengthDiff = formattedMac.length() - enteredMac.length();

                setMacEdit(cleanMac, formattedMac, selectionStart, lengthDiff);
            }

            /**
             * Strips all characters from a string except A-F and 0-9.
             * @param mac       User input string.
             * @return String containing MAC-allowed characters.
             */
            private String clearNonMacCharacters(String mac) {
                return mac.toString().replaceAll("[^A-Fa-f0-9]", "");
            }

            /**
             * Adds a colon character to an unformatted MAC address after
             * every second character (strips full MAC trailing colon)
             * @param cleanMac      Unformatted MAC address.
             * @return Properly formatted MAC address.
             */
            private String formatMacAddress(String cleanMac) {
                int grouppedCharacters = 0;
                String formattedMac = "";

                for (int i = 0; i < cleanMac.length(); ++i) {
                    formattedMac += cleanMac.charAt(i);
                    ++grouppedCharacters;

                    if (grouppedCharacters == 2) {
                        formattedMac += ":";
                        grouppedCharacters = 0;
                    }
                }

                // Removes trailing colon for complete MAC address
                if (cleanMac.length() == 12)
                    formattedMac = formattedMac.substring(0, formattedMac.length() - 1);

                return formattedMac;
            }

            /**
             * Upon users colon deletion, deletes MAC character preceding deleted colon as well.
             * @param enteredMac            User input MAC.
             * @param formattedMac          Formatted MAC address.
             * @param selectionStart        MAC EditText field cursor position.
             * @return Formatted MAC address.
             */
            private String handleColonDeletion(String enteredMac, String formattedMac, int selectionStart) {
                if (mPreviousMac != null && mPreviousMac.length() > 1) {
                    int previousColonCount = colonCount(mPreviousMac);
                    int currentColonCount = colonCount(enteredMac);

                    if (currentColonCount < previousColonCount) {
                        formattedMac = formattedMac.substring(0, selectionStart - 1) + formattedMac.substring(selectionStart);
                        String cleanMac = clearNonMacCharacters(formattedMac);
                        formattedMac = formatMacAddress(cleanMac);
                    }
                }
                return formattedMac;
            }

            /**
             * Gets MAC address current colon count.
             * @param formattedMac      Formatted MAC address.
             * @return Current number of colons in MAC address.
             */
            private int colonCount(String formattedMac) {
                return formattedMac.replaceAll("[^:]", "").length();
            }

            /**
             * Removes TextChange listener, sets MAC EditText field value,
             * sets new cursor position and re-initiates the listener.
             * @param cleanMac          Clean MAC address.
             * @param formattedMac      Formatted MAC address.
             * @param selectionStart    MAC EditText field cursor position.
             * @param lengthDiff        Formatted/Entered MAC number of characters difference.
             */
            private void setMacEdit(String cleanMac, String formattedMac, int selectionStart, int lengthDiff) {
                editText.removeTextChangedListener(this);
                if (cleanMac.length() <= 12) {
                    editText.setText(formattedMac);
                    editText.setSelection(selectionStart + lengthDiff);
                    mPreviousMac = formattedMac;
                } else {
                    editText.setText(mPreviousMac);
                    editText.setSelection(mPreviousMac.length());
                }
                editText.addTextChangedListener(this);
            }
        });
    }
}

