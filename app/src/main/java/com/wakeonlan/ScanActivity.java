package com.wakeonlan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.net.util.SubnetUtils;

import static com.wakeonlan.BaseDeviceActivity.EXTRA_DEVICE_ADDRESS;
import static com.wakeonlan.BaseDeviceActivity.EXTRA_DEVICE_MAC;
import static com.wakeonlan.BaseDeviceActivity.EXTRA_DEVICE_NAME;


public class ScanActivity
        extends AppCompatActivity
        implements OnNewDeviceDiscoveredListener {

    public static final String TAG = "WakeOnLan.ScanActivity";

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LanDevicesListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mAddDeviceFloatingButton;
    private AsyncTask<Void, Void, Void> mScanTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(SharedPrefsSettings.isThemeDark() ? R.style.AppThemeDark : R.style.AppThemeLight);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.activity_search_devices_title));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.scan_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(ScanActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new LanDevicesListAdapter(
                ScanActivity.this,
                new LanDevicesListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(LanDevice lanDevice) {
                    openAddDeviceActivity(lanDevice);
                }
            });
        mAdapter.setEmptyViewText(
                getResources().getString(R.string.device_list_is_empty));

        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mScanTask = new AsyncTask<Void, Void, Void>() {

                    long mStartTimestamp;
                    long mStopTimestamp;


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if (!Utils.isWifiConnected(getApplicationContext())) {
                            mAdapter.setEmptyViewText(
                                    getResources().getString(R.string.error_wifi_not_connected));
                            mAdapter.clearItems();

                            mSwipeRefreshLayout.setRefreshing(false);
                            cancel(true);
                            return;
                        }
                        mSwipeRefreshLayout.setRefreshing(true);
                        mStartTimestamp = System.currentTimeMillis();
                        Log.d(TAG, "start time: " + mStartTimestamp);
                    }

                    @Override
                    protected void onCancelled() {
                        super.onCancelled();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        Log.d(TAG, "running...");

                        Log.d(TAG, "mask: " +
                                Utils.getWifiSubnetMask(getApplicationContext()));
                        Log.d(TAG, "broadcast: " +
                                Utils.getWifiBroadcastAddress(getApplicationContext()));
                        Log.d(TAG, "subnet: " +
                                Utils.getWifiSubnetAddress(getApplicationContext()));

//                SubnetUtils utils = new SubnetUtils("192.168.1.0/24");
                        SubnetUtils utils = new SubnetUtils(
                                Utils.getWifiSubnetAddress(getApplicationContext()),
                                Utils.getWifiSubnetMask(getApplicationContext())
                        );



                        String[] allIps = utils.getInfo().getAllAddresses();
//                        Log.e(TAG, utils.getInfo().toString());
//                        Log.e(TAG, utils.getInfo().getLowAddress());
//                        Log.e(TAG, utils.getInfo().getHighAddress());

                        final int MAX_THREADS = allIps.length;

                        int numOfCores = Runtime.getRuntime().availableProcessors();

                        final int MAX_WAIT_TIME = 200;
                        final int MAX_PROCESS_TIME = 50;

                        /**
                         * Brian Goetz in his famous book "Java Concurrency in Practice"
                         * recommends the following formula:
                         * Number of threads = Number of Available Cores * (1 + Wait time / Service time)
                         */
//                        int idealNumOfThreads = numOfCores * (1 + MAX_WAIT_TIME / MAX_PROCESS_TIME);

                        int idealNumOfThreads = 40;

                        Log.d(TAG, "numbers if cores: " + numOfCores + " threads: " + idealNumOfThreads);


//                        ThreadPoolExecutor executor = new ThreadPoolExecutor(
//                                idealNumOfThreads, idealNumOfThreads, 10, TimeUnit.SECONDS,
//                                new LinkedBlockingQueue<Runnable>(MAX_THREADS + 1)
//                        );
//
                        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                                idealNumOfThreads, idealNumOfThreads, 10, TimeUnit.SECONDS,
                                new LinkedBlockingQueue<Runnable>());

                        ExecutorService networkExecutor = Executors.newSingleThreadExecutor();

                        Collection<Future<?>> futures = new LinkedList<Future<?>>();

//                        Future<?> fut = null;
//                        try {
////                            fut = executor.submit(new Worker("128.224.174.110", ScanActivity.this));
////                            fut = executor.submit(new Worker("128.224.174.3", ScanActivity.this));
////                            fut = executor.submit(new Worker("128.224.174.21", ScanActivity.this));
//                            fut = executor.submit(new Worker("128.224.174.110", ScanActivity.this));
//                        } catch (UnknownHostException e) {
//                            e.printStackTrace();
//                        }
//                        futures.add(fut);

                        for (int i = 0; i < MAX_THREADS; ++i) {
//                                executor.execute(new Worker(allIps[i], ScanActivity.this));
//                                CheckHostTask task = new CheckHostTask(ScanActivity.this);
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, addr);
//                                } else {
//                                    task.execute(addr);
                            Future<?> future = null;
                            try {
                                future = executor.submit(new Worker(allIps[i], ScanActivity.this, networkExecutor));
                                futures.add(future);
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                        }

//                        for (int i = 0; i < 3; ++i) {
//                            Future<?> future = executor.submit(new WorkerTest(i));
//                            futures.add(future);
//                        }
//
//                        Log.d(TAG, "Num of futures = " + futures.size());

                        ///
//                        executor.shutdown();
//                        try {
////                            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//                            executor.awaitTermination(500, TimeUnit.MILLISECONDS);
//                        } catch (InterruptedException e) {
//                            Log.e(TAG, e.getMessage());
//                        }
                        ///

                        Log.d(TAG, "Done wait");


                        for (Future<?> future : futures) {
                            try {
                                if (isCancelled()) break;
                                future.get(10, TimeUnit.SECONDS);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                                Log.e(TAG, e.getMessage());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                Log.e(TAG, e.getMessage());
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                                Log.e(TAG, e.getMessage());
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        mSwipeRefreshLayout.setRefreshing(false);
                        mStopTimestamp = System.currentTimeMillis();
                        long dt = mStopTimestamp - mStartTimestamp;
                        Log.d(TAG, String.format("%02f seconds", (dt / 1000.00f)));
//                        Toast.makeText(ScanActivity.this.getApplicationContext(),
//                                String.format("%.2f seconds -> %d hosts found",
//                                        (dt / 1000.00f), mAdapter.getItemCount()), Toast.LENGTH_LONG).show();

                        Toast toast = new Toast(ScanActivity.this.getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);

                        LayoutInflater inflater = getLayoutInflater();

                        View toastLayout = inflater.inflate(R.layout.toast,
                                (ViewGroup) findViewById(R.id.custom_toast_container));

                        toast.setView(toastLayout);
                        toast.show();

                    }
                };
                mScanTask.execute();
            }
        });

        mAddDeviceFloatingButton = (FloatingActionButton)
                findViewById(R.id.add_new_device_floating_button);
        mAddDeviceFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddDeviceActivity(null);
            }
        });
    }

    private void openAddDeviceActivity(LanDevice device) {
        if (device != null) {
            Intent intent = new Intent(ScanActivity.this, AddDeviceActivity.class);
            intent.putExtra(EXTRA_DEVICE_NAME, device.getHostname());
            intent.putExtra(EXTRA_DEVICE_MAC, device.getMac());
            intent.putExtra(EXTRA_DEVICE_ADDRESS, device.getAddress());
            startActivityForResult(intent, MainActivity.ADD_DEVICE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==  MainActivity.ADD_DEVICE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    @Override
    public void onNewDevice(String hostname, String addr, String mac, String vendor) {
        LanDevice newDevice = new LanDevice(hostname, mac, addr, vendor);
        mAdapter.addItem(newDevice);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mScanTask != null && mScanTask.getStatus() == AsyncTask.Status.RUNNING) {
            mScanTask.cancel(true);
        }
    }


    public static class WorkerTest implements Callable<Integer> {

        private int mValue;

        public WorkerTest(int value) {
            mValue = value;
        }

        @Override
        public Integer call() throws Exception {
            Thread.sleep(5000);
            return mValue;
        }
    }

    public static class Worker implements Runnable {

        private Handler mHandler;
        private ExecutorService mNetworkExecutor;

        private InetAddress mInetAddress;
        private OnNewDeviceDiscoveredListener mListener;

        private String mHostname;
        private String mMacAddress;
        private String mAddress;
        private String mVendor;

        public Worker(String address, OnNewDeviceDiscoveredListener listener,
                      ExecutorService networkExecutor)
                throws UnknownHostException {
            mHandler = new Handler(Looper.getMainLooper());
            mNetworkExecutor = networkExecutor;
            mListener = listener;
            String[] o = address.split("\\.");
            mInetAddress = InetAddress.getByAddress(
                    new byte[]{
                            (byte) Integer.parseInt(o[0]),
                            (byte) Integer.parseInt(o[1]),
                            (byte) Integer.parseInt(o[2]),
                            (byte) Integer.parseInt(o[3])});
        }

        @Override
        public void run() {
            TrafficStats.setThreadStatsTag((int) Thread.currentThread().getId());
            try {
                if (!mInetAddress.isReachable(1000)) {
                    Log.d(TAG, "Done with(NOT reachable)" + mInetAddress.getHostAddress());
                    return;
                } else {
                    Log.d(TAG, "Done with(reachable)" + mInetAddress.getHostAddress());
                }
            } catch (IOException e) {
                Log.d(TAG, "isReachable(" + e.getMessage());
                return;
            }

//            Log.d(TAG, mInetAddress.toString());
//            long t0 = System.currentTimeMillis();
            try {
                mMacAddress = Utils.findMac(mInetAddress.getHostAddress());
            } catch (Exception e) {
                Log.e(TAG, "run() -> " + e.getMessage());
            }
//            mMacAddress = Utils.findMac(mInetAddress.getHostAddress());
//            mMacAddress = Utils.getArpCache().get(mInetAddress.getHostAddress());
//            long t1 = System.currentTimeMillis();
//            Log.d(TAG, "took: " + (t1 - t0));

            if (mMacAddress != null) {
                mHostname = mInetAddress.getHostName();
                if (mHostname != null) {
                    mAddress = mInetAddress.getHostAddress();
                    mVendor = MacVendors.lookup(MacVendors.getOui(mMacAddress));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onNewDevice(mHostname, mAddress, mMacAddress, mVendor);
                        }
                    });
                }
            }
        }
    }
}
