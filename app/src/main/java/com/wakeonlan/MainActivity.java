package com.wakeonlan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.wakeonlan.net.Network;

import org.apache.commons.net.util.SubnetUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "WakeOnLan";
    public static final int UPDATE_DEVICE_REQUEST = 1;
    public static final int ADD_DEVICE_REQUEST = 2;

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    ActionBarDrawerToggle mToggle;

    private RecyclerView mRecyclerView;
    private DeviceListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mAddDeviceFloatingButton;

    private AsyncTask<MainActivity, Void, Void> mLoadSharedPrefsTask;


    /*
     * Note: using WifiManager is not safe for obtaining MAC address:
     * To provide users with greater data protection, starting in this release,
     * Android removes programmatic access to the device’s local hardware identifier for apps
     * using the Wi-Fi and Bluetooth APIs.
     * The WifiInfo.getMacAddress() and the BluetoothAdapter.getAddress() methods now return
     * a constant value of 02:00:00:00:00:00.
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Network net = new Network(getApplicationContext());
//
//                    for (int prefix = 1; prefix < 32; ++prefix) {
//                        SubnetUtils subnet = new SubnetUtils(?"192.168.43.1/" + prefix);
//
//                        String netmask1 = Utils.makeInetAddress(Utils.getNetmaskFromPrefixLength(prefix)).getHostAddress();
//                        String netmask2 = subnet.getInfo().getNetmask();
//
//                        if (!netmask1.equals(netmask2)) {
//
//                            Log.e(TAG, "prefix " + prefix + " " + netmask1 + " " + netmask2);
//                        }
//                    }
//
                    Log.d(TAG, net.toString());

                } catch (UnknownHostException e) {
                    Log.e(TAG, e.getMessage());
                } catch (SocketException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }).start();


//        Log.d(TAG, "is a valid IP address \"null\": " + Patterns.IP_ADDRESS.matcher(null));
        Log.d(TAG, "is a valid IP address \"192.168.0.0\": " + Patterns.IP_ADDRESS.matcher("192.168.0.0").matches());
        Log.d(TAG, "is a valid IP address \"255.255.255.255\": " + Patterns.IP_ADDRESS.matcher("255.255.255.255").matches());





        SharedPrefsDeviceConfiguration.init(MainActivity.this.getApplicationContext());
        // TODO move this elsewhere
        SharedPrefsSettings.init(MainActivity.this.getApplicationContext());
            try {
            MacVendors.init(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(MainActivity.TAG, e.getMessage());
        }

        setTheme(SharedPrefsSettings.isThemeDark() ? R.style.AppThemeDark : R.style.AppThemeLight);
//        enableStrictMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
//        mToolbar.getContext().setTheme(mThemeDark ?
////                R.style.ActionBarStyleDark : R.style.ActionBarStyleLight);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_star);
        }

        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_devices);

        mToggle = new ActionBarDrawerToggle(this, mDrawer,
                mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mDrawer.addDrawerListener(mToggle);
//        mToggle.setDrawerIndicatorEnabled(false);
//        mToggle.setHomeAsUpIndicator(R.drawable.ic_star);
        mToggle.syncState();

        mAddDeviceFloatingButton = findViewById(R.id.add_new_device_floating_button);
        mAddDeviceFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openAddDeviceActivity();
                openScanDevicesActivity();
            }
        });

        initRecyclerView();
        buildRecyclerView();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    private void enableStrictMode() {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .penaltyDialog()
                        .build()
        );

        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .penaltyDeath()
                        .build()
        );
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(true);
    }

    private void buildRecyclerView() {
//        ArrayList<DeviceConfiguration> devicesConfigList = new ArrayList<>();
//        devicesConfigList.add(new DeviceConfiguration(new Device(1, "11:22:33:44:55:66", "huawei-p20"), "192.168.1.1", (short) 9));
//        devicesConfigList.add(new DeviceConfiguration(new Device(2, "11:22:33:AA:BB:CC", "samsung-j6"), "192.168.1.2", (short) 9));
//        devicesConfigList.add(new DeviceConfiguration(new Device(3, "11:22:33:AA:BB:CC", "samsung galaxy s8"), "192.168.1.2", (short) 9));
//        devicesConfigList.add(new DeviceConfiguration(new Device(4, "11:22:33:AA:BB:CC", "iPhoneX"), "192.168.1.2", (short) 9));
//        devicesConfigList.add(new DeviceConfiguration(new Device(5, "11:22:33:AA:BB:CC", "iPhone7 gold"), "192.168.1.2", (short) 9));
//        mAdapter = new DeviceListAdapter(MainActivity.this, devicesConfigList);

        mAdapter = new DeviceListAdapter(MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyViewText(getResources().getString(R.string.device_list_is_empty));
    }

    public void openScanDevicesActivity() {
        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
        startActivityForResult(intent, MainActivity.ADD_DEVICE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_DEVICE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                mAdapter.onItemEdited(data);
            }
        } else if (requestCode == ADD_DEVICE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                mAdapter.onItemAdded(data);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_devices:
                Log.d(TAG, "selected menu devices");
                break;
            case R.id.nav_share:
                Log.d(TAG, "selected menu share");
                break;
            case R.id.nav_settings:
                Log.d(TAG, "selected menu settings");
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_container, new SettingsFragment()).commit();

                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.nav_rate_app:
                Log.d(TAG, "selected menu rate app");
                goToMyApp(true);
                break;
        }
        return false;
    }

    private void goToMyApp(boolean googlePlay)  {
        //true if Google Play, false if Amazone Store
        try {
//            final String appPackageName = "co.uk.mrwebb.wakeonlan";
//        final String appPackageName = "com.facebook.orca";
            final String appPackageName = getPackageName();
            Uri uri = null;
            if (googlePlay) {
//                uri = Uri.parse(String.format("market://details?id=%s", appPackageName));
                uri = Uri.parse(String.format("http://play.google.com/store/apps/details?id=%s",
                        appPackageName));
            } else {
                uri = Uri.parse(String.format("amzn://apps/android?p=%s", appPackageName));
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            int flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                    | Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP;
            if (Build.VERSION.SDK_INT >= 21) {
                flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
            } else {
                //noinspection deprecation
                flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
            }

            intent.addFlags(flags);
            startActivity(intent);

        } catch (ActivityNotFoundException e1) {
            Log.d(MainActivity.TAG, e1.getMessage());

            Toast.makeText(MainActivity.this.getApplicationContext(),
                    getResources().getString(R.string.error_rate_app_not_found),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
