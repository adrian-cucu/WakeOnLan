package com.wakeonlan;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


public class DeviceListItemAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_DEVICE_CONFIGURATION = 0;
    public static final int VIEW_TYPE_DELETE_DEVICE_ICON = 1;
    public static final int VIEW_TYPE_EDIT_DEVICE_ICON = 2;

    private Context mContext;
    private DeviceConfiguration mDeviceConfig;
    private OnItemAction mItemActionListener;


    public DeviceListItemAdapter(Context context) {
        mContext = context;
    }

    public DeviceListItemAdapter(Context context, OnItemAction itemActionListener) {
        mContext = context;
        mItemActionListener = itemActionListener;
    }

    public DeviceListItemAdapter(Context context,
                                 DeviceConfiguration deviceConfig,
                                 OnItemAction itemActionListener) {
        mContext = context;
        mDeviceConfig = deviceConfig;
        mItemActionListener = itemActionListener;
    }

    public void setDeviceConfig(DeviceConfiguration deviceConfig) {
        mDeviceConfig = deviceConfig;
        notifyItemChanged(VIEW_TYPE_DEVICE_CONFIGURATION);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_DEVICE_CONFIGURATION;
        } else if (position == 1) {
            return VIEW_TYPE_DELETE_DEVICE_ICON;
        } else {
            return VIEW_TYPE_EDIT_DEVICE_ICON;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DEVICE_CONFIGURATION) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.device_configuration_item,
                            parent, false);
            return new DeviceConfigurationViewHolder(view, mItemActionListener);

        } else if (viewType == VIEW_TYPE_DELETE_DEVICE_ICON) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.delete_device_item,
                            parent, false);
            return new DeleteDeviceViewHolder(view, mItemActionListener);

        } else {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.edit_device_item,
                            parent, false);
            return new EditDeviceViewHolder(view, mItemActionListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_DEVICE_CONFIGURATION) {

            Log.d(MainActivity.TAG, "DeviceListItemAdapter  onBindViewHolder position ");
            DeviceConfigurationViewHolder devConfigViewHolder =
                    (DeviceConfigurationViewHolder) holder;

            devConfigViewHolder.deviceNameTextView.setText(
                    mDeviceConfig.getDevice().getHostName());
            devConfigViewHolder.macAddressTextView.setText(
                    mDeviceConfig.getDevice().getMacAddress());
            if (!TextUtils.isEmpty(mDeviceConfig.getAddress())) {
                devConfigViewHolder.mAddressImageView.setVisibility(View.VISIBLE);
                devConfigViewHolder.ipAddressTextView.setText(mDeviceConfig.getAddress());
            } else {
                devConfigViewHolder.mAddressImageView.setVisibility(View.GONE);
                devConfigViewHolder.ipAddressTextView.setText("");
            }

            switch (mDeviceConfig.getDeviceStatus()) {
                case UNKNOWN:
                    //                loading.setVisibility(View.VISIBLE);
                    devConfigViewHolder.mAwakeStatusImageView.setVisibility(View.GONE);
                    break;
                case AWAKE:
                    //                loading.setVisibility(View.GONE);
                    devConfigViewHolder.mAwakeStatusImageView.setImageResource(R.drawable.circle_green);
                    devConfigViewHolder.mAwakeStatusImageView.setVisibility(View.VISIBLE);
                    break;
                case NOT_AWAKE:
                    //                loading.setVisibility(View.GONE);
                    devConfigViewHolder.mAwakeStatusImageView.setImageResource(R.drawable.circle_red);
                    devConfigViewHolder.mAwakeStatusImageView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }


    public static class DeviceConfigurationViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView deviceNameTextView;
        public TextView macAddressTextView;
        public ImageView mAddressImageView;
        public TextView ipAddressTextView;
        public ImageView mAwakeStatusImageView;
        public ProgressBar mLodingProgressBar;

        private OnItemAction mItemActionListener;


        public DeviceConfigurationViewHolder(@NonNull View itemView,
                                             OnItemAction itemActionListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            mItemActionListener = itemActionListener;
            deviceNameTextView = (TextView) itemView.findViewById(R.id.device_name);
            macAddressTextView = (TextView) itemView.findViewById(R.id.mac_address);
            mAddressImageView = (ImageView) itemView.findViewById(R.id.img_static_address);
            ipAddressTextView = (TextView) itemView.findViewById(R.id.ip_address);
            mAwakeStatusImageView = (ImageView) itemView.findViewById(R.id.awake_status);
            mLodingProgressBar = (ProgressBar) itemView.findViewById(R.id.loading);
            mLodingProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            if (mItemActionListener != null) {
                new AsyncTask<Void,Void,Void>(){

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        mLodingProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected Void doInBackground(final Void... params){
                        // Do your loading here. Don't touch any views from here, and then return null
                        mItemActionListener.onClick();
                        return null;
                    }


                    @Override
                    protected void onPostExecute(final Void result){
                        // Update your views here
                        mLodingProgressBar.setVisibility(View.GONE);
                    }
                }.execute();
            }
        }
    }

    public static class DeleteDeviceViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageView mDeleteDevice;
        private OnItemAction mItemActionListener;

        public DeleteDeviceViewHolder(@NonNull final View itemView,
                                      OnItemAction itemActionListener) {
            super(itemView);
            mItemActionListener = itemActionListener;
            mDeleteDevice = (ImageView) itemView.findViewById(R.id.button_img_delete);
            mDeleteDevice.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemActionListener != null) {
//                DialogBox mDialogBox  = new DialogBox(itemView.getContext(),
//                        itemView.getContext().getResources()
//                                .getString(R.string.dialog_title_confirm_delete_device));
//                mDialogBox.setOnClickPositiveButtonListener(null);
//                mDialogBox.show();
//

                LayoutInflater layoutInflater = (LayoutInflater)
                        itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View inflatedLayoutView = layoutInflater.inflate(R.layout.dialog_box,null);
//                inflatedLayoutView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.)


                AlertDialog.Builder builder = new AlertDialog.Builder(
                        itemView.getContext(), R.style.Dialog);
                builder.setTitle("Animation Dialog");
                builder.setMessage("Message from dialog is this");
//                builder.setNegativeButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.PopupWindowAnimation;
                dialog.setCancelable(true);
                dialog.show();

//                mItemActionListener.onDelete();
            }
        }
    }

    public static class EditDeviceViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageView mEditDevice;
        private OnItemAction mItemActionListener;


        public EditDeviceViewHolder(@NonNull View itemView,
                                    OnItemAction itemActionListener) {
            super(itemView);
            mItemActionListener = itemActionListener;
            mEditDevice = (ImageView) itemView.findViewById(R.id.button_img_edit);
            mEditDevice.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemActionListener != null) {
                mItemActionListener.onEdit();
            }
        }
    }
}

