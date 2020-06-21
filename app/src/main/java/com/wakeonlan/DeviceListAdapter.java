package com.wakeonlan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.wakeonlan.BaseDeviceActivity.EXTRA_DEVICE_ID;
import static com.wakeonlan.BaseDeviceActivity.EXTRA_DEVICE_NAME;
import static com.wakeonlan.BaseDeviceActivity.EXTRA_DEVICE_MAC;
import static com.wakeonlan.BaseDeviceActivity.EXTRA_DEVICE_PORT;
import static com.wakeonlan.BaseDeviceActivity.EXTRA_DEVICE_ADDRESS;


public class DeviceListAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements AdapterItemActionListener {

    public static final String TAG = "WakeOnLan.DeviceAdapter";

    public static final int VIEW_TYPE_EMPTY = 0;
    public static final int VIEW_TYPE_NORMAL = 1;

    private Context mContext;
    private ArrayList<DeviceConfiguration> mDevicesConfigList;
    private String mEmptyViewText;


    public DeviceListAdapter(Context context) {
        mContext = context;
        mDevicesConfigList = SharedPrefsDeviceConfiguration.loadDevicesList();
    }

    public DeviceListAdapter(Context context, ArrayList<DeviceConfiguration> devices) {
        mContext = context;
        mDevicesConfigList = devices;
    }

    public void addItem(DeviceConfiguration deviceConfiguration) {
        if (mDevicesConfigList == null) {
            mDevicesConfigList = new ArrayList<>();
        }
        if (mDevicesConfigList.size() == 0) {
            mDevicesConfigList.add(deviceConfiguration);
            int insertPosition = mDevicesConfigList.size();
            notifyItemInserted(insertPosition);
        } else {
            int position = mDevicesConfigList.indexOf(deviceConfiguration);
            if (position != -1) {
                mDevicesConfigList.set(position, deviceConfiguration);
                notifyItemChanged(position);
            } else {
                mDevicesConfigList.add(deviceConfiguration);
                int insertPosition = mDevicesConfigList.size();
                notifyItemInserted(insertPosition);
            }
        }
    }

    public void clearItems() {
        if (mDevicesConfigList != null && mDevicesConfigList.size() > 0) {
            mDevicesConfigList.clear();
            notifyDataSetChanged();
        } else {
            notifyItemChanged(0);
        }
    }

    public void setEmptyViewText(String text) {
        mEmptyViewText = text;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_NORMAL) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.device_list_wrapper, parent, false);
            return new DeviceListViewHolder(view, this);
        } else {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_device_empty, parent, false);
            return new DeviceListEmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_NORMAL) {
            if (mDevicesConfigList != null) {
                Log.d(MainActivity.TAG, "onBindViewHolder position " + position + " changed " + mDevicesConfigList.get(position));
                ((DeviceListViewHolder) holder).bind(mDevicesConfigList.get(position));
            }
        } else {
            ((DeviceListEmptyViewHolder) holder).setViewText(mEmptyViewText);
            Log.d(MainActivity.TAG, "onBindViewHolder empty view");
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

                int delta_x = 0;
                int delta_y = 0;
                int curr_pos = 0;
                boolean auto_scrolling = false;

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (delta_x > 30) {
                            recyclerView.smoothScrollToPosition(2);
                        } else {
                            recyclerView.smoothScrollToPosition(0);
                        }

                        if (auto_scrolling) {
                            auto_scrolling = false;
                            return;
                        }

                        if (Math.abs(delta_x) < 200) {
                            recyclerView.smoothScrollToPosition(curr_pos);
                            auto_scrolling = true;
                            return;
                        }
                        if (delta_x < -50) {
                            recyclerView.smoothScrollToPosition(0);
                            auto_scrolling = true;
                        } else if (delta_x > 50) {
                            recyclerView.smoothScrollToPosition(2);
                            auto_scrolling = true;
                        } else {
                            recyclerView.smoothScrollToPosition(1);
                            auto_scrolling = true;
                        }
                    } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    delta_x += dx;
                    delta_y += dy;
                }
            };
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mDevicesConfigList != null && mDevicesConfigList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mDevicesConfigList != null && mDevicesConfigList.size() > 0) {
            return mDevicesConfigList.size();
        } else {
            return 1;
        }
    }

    @Override
    public void onClick(int position) {
        Log.d(MainActivity.TAG, "DeviceListAdapter onClick: " +
                mDevicesConfigList.get(position));

        DeviceConfiguration deviceConfig = mDevicesConfigList.get(position);

        try {
            long start = System.currentTimeMillis();

            int ret = WakeOnLan.wol_udp(
                    deviceConfig.getDevice().getRawMacAddress(),
                    deviceConfig.getAddress(),
                    deviceConfig.getPort());

            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;

//            Log.d(TAG, "took: " + timeElapsed + " millis");

//            throw  new Exception("fmm");

            Thread.sleep(5000);


        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
            new AlertDialog.Builder(mContext)
                    .setTitle("Error")
                    .setMessage(e.getMessage())

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("no", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("1", null)
                    .setIcon(android.R.drawable.ic_dialog_alert);
//                    .show();
        }
    }

    @Override
    public void onDelete(int position) {
        DeviceConfiguration deviceConfig = mDevicesConfigList.get(position);
        Log.d(MainActivity.TAG, "DeviceListAdapter onDelete: " + deviceConfig);
        if (SharedPrefsDeviceConfiguration.remove(deviceConfig)) {
            mDevicesConfigList.remove(deviceConfig);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void onEdit(int position) {
        Log.d(MainActivity.TAG, "DeviceListAdapter onEdit: " +
                mDevicesConfigList.get(position));

        DeviceConfiguration deviceConfig = mDevicesConfigList.get(position);

        Intent intent = new Intent(mContext, EditDeviceActivity.class);
        intent.putExtra(EXTRA_DEVICE_ID, deviceConfig.getDevice().getID());
        intent.putExtra(EXTRA_DEVICE_NAME, deviceConfig.getDevice().getHostName());
        intent.putExtra(EXTRA_DEVICE_MAC, deviceConfig.getDevice().getMacAddress());
        intent.putExtra(EXTRA_DEVICE_PORT, deviceConfig.getPort());
        intent.putExtra(EXTRA_DEVICE_ADDRESS, deviceConfig.getAddress());

        Activity origin = (Activity)mContext;
        origin.startActivityForResult(intent, MainActivity.UPDATE_DEVICE_REQUEST);
    }

    public void onItemAdded(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.d(MainActivity.TAG, key + " = \"" + bundle.get(key) + "\"");
            }

            String deviceName = data.getStringExtra(EXTRA_DEVICE_NAME);
            String mac = data.getStringExtra(EXTRA_DEVICE_MAC);
            int port = data.getIntExtra(EXTRA_DEVICE_PORT, -1);
            String address = data.getStringExtra(EXTRA_DEVICE_ADDRESS);

            Log.d(TAG, EXTRA_DEVICE_NAME + " : " + deviceName);
            Log.d(TAG, EXTRA_DEVICE_MAC + " : " + mac);
            Log.d(TAG, EXTRA_DEVICE_PORT + " : " + port);
            Log.d(TAG, EXTRA_DEVICE_ADDRESS + " : " + address);

            DeviceConfiguration deviceConfig =
                    SharedPrefsDeviceConfiguration.saveDeviceConfig(
                            deviceName, mac, address, port);
            if (deviceConfig != null) {
                addItem(deviceConfig);
            }
        }
    }

    public void onItemEdited(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.d(TAG, key + " = \"" + bundle.get(key) + "\"");
            }

            int deviceID = bundle.getInt(EXTRA_DEVICE_ID);
            if (deviceID != -1) {
                int position = getPositionForDeviceId(deviceID);
                if (position != RecyclerView.NO_POSITION) {
                    DeviceConfiguration deviceConfig = mDevicesConfigList.get(position);

                    String deviceName = data.getStringExtra(EXTRA_DEVICE_NAME);
                    String mac = data.getStringExtra(EXTRA_DEVICE_MAC);
                    int port = data.getIntExtra(EXTRA_DEVICE_PORT, -1);
                    String ip = data.getStringExtra(EXTRA_DEVICE_ADDRESS);

                    Device device = new Device(deviceID,
                            mac != null ?
                                    mac : deviceConfig.getDevice().getMacAddress(),
                            deviceName != null ?
                                    deviceName : deviceConfig.getDevice().getHostName());

                    String updatedIP = (ip != null) ? ip : deviceConfig.getAddress();
                    int updatedPort = (port != -1) ?  (port != 0 ? port : -1): deviceConfig.getPort();
                    DeviceConfiguration updatedDevice =
                            new DeviceConfiguration(device, updatedIP, updatedPort);

                    Log.d(MainActivity.TAG, "....\n" +
                            deviceConfig + "  change to: \n" + updatedDevice);

                    if (SharedPrefsDeviceConfiguration.update(deviceID, updatedDevice)) {
                        Log.d(MainActivity.TAG, "successfull update");
                        mDevicesConfigList.set(position, updatedDevice);
                        notifyItemChanged(position);
                    }
                }
            }
        }
    }

    private int getPositionForDeviceId(int deviceId) {
        for (DeviceConfiguration deviceConfig : mDevicesConfigList) {
            if (deviceConfig.getDevice().getID() == deviceId) {
                return mDevicesConfigList.indexOf(deviceConfig);
            }
        }
        return RecyclerView.NO_POSITION;
    }

    public static class DeviceListViewHolder
            extends RecyclerView.ViewHolder
            implements OnItemAction {

        private RecyclerView mRecyclerView;
        private LinearLayoutManager mLayoutManager;
        private DeviceListItemAdapter mDeviceListItemAdapter;
        private AdapterItemActionListener mAdapterItemActionListener;


        public DeviceListViewHolder(@NonNull View itemView,
                                    AdapterItemActionListener adapterItemActionListener) {
            super(itemView);

            mAdapterItemActionListener = adapterItemActionListener;
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view_wrapper);
            mLayoutManager = new LinearLayoutManager(
                    itemView.getContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false);
            mLayoutManager.setInitialPrefetchItemCount(4);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mDeviceListItemAdapter = new DeviceListItemAdapter(itemView.getContext(), this);
            mRecyclerView.setAdapter(mDeviceListItemAdapter);
        }

        public void bind(DeviceConfiguration deviceConfiguration) {
            Log.d(MainActivity.TAG, "DeviceListViewHolder.bind");
            mDeviceListItemAdapter.setDeviceConfig(deviceConfiguration);
            mRecyclerView.scrollToPosition(0);
        }

        @Override
        public void onClick() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Log.d(MainActivity.TAG, "onClick device config pos:"
                        + position + " -> send wol packet");
                mAdapterItemActionListener.onClick(position);
            }
        }

        @Override
        public void onDelete() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Log.d(MainActivity.TAG, "onDelete device config pos:"
                        + position + " -> delete");
                mAdapterItemActionListener.onDelete(position);
            }
        }

        @Override
        public void onEdit() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Log.d(MainActivity.TAG, "onEdit device config pos:"
                        + position + " -> edit");
                mAdapterItemActionListener.onEdit(position);
            }
        }
    }

    public static class DeviceListEmptyViewHolder
            extends RecyclerView.ViewHolder {

        private TextView mEmptyList;


        public DeviceListEmptyViewHolder(@NonNull View itemView) {
            super(itemView);

            mEmptyList = (TextView) itemView.findViewById(R.id.empty_device_list);
            mEmptyList.setTextSize(TypedValue.COMPLEX_UNIT_PX, 48);
            mEmptyList.setVisibility(View.VISIBLE);
        }

        public void setViewText(String text) {
            mEmptyList.setText(text);
            mEmptyList.setVisibility(View.VISIBLE);
        }
    }
}
