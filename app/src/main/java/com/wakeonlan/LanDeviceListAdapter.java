package com.wakeonlan;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class LanDevicesListAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_EMPTY = 0;
    public static final int VIEW_TYPE_NORMAL = 1;

//    private Context mContext;
    private ArrayList<LanDevice> mLanDevicesList;
    private OnItemClickListener mListener;

    private LanDevicesListEmptyViewHolder mEmptyView;
    private String mEmptyViewText;


    public LanDevicesListAdapter(@NonNull Context context,
                                 ArrayList<LanDevice> devices,
                                 OnItemClickListener listener) {
//        mContext = context;
        mLanDevicesList = devices;
        mListener = listener;
    }

    public LanDevicesListAdapter(@NonNull Context context,
                                 OnItemClickListener listener) {
//        mContext = context;
        mListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void removeOnItemClickListener() {
        mListener = null;
    }

    public void addItem(LanDevice lanDevice) {
        if (mLanDevicesList == null) {
            mLanDevicesList = new ArrayList<>();
        }
        if (mLanDevicesList.size() == 0) {
            mLanDevicesList.add(lanDevice);
            int insertPosition = mLanDevicesList.size();
            notifyItemInserted(insertPosition);
        } else {
            int position = mLanDevicesList.indexOf(lanDevice);
            if (position != -1) {
                mLanDevicesList.set(position, lanDevice);
                notifyItemChanged(position);
            } else {
                mLanDevicesList.add(lanDevice);
                int insertPosition = mLanDevicesList.size();
                notifyItemInserted(insertPosition);
            }
        }
    }

    public void clearItems() {
        if (mLanDevicesList != null && mLanDevicesList.size() > 0) {
            mLanDevicesList.clear();
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
                    .inflate(R.layout.item_lan_device, parent, false);
            return new LanDevicesListViewHolder(view, mListener);
        } else {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_lan_device_empty, parent, false);
//            return new LanDevicesListViewHolder(view, mListener);
            LanDevicesListEmptyViewHolder lanDevicesListEmptyViewHolder = new LanDevicesListEmptyViewHolder(view);
//            lanDevicesListEmptyViewHolder.setViewText(mEmptyViewText);
            return lanDevicesListEmptyViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_NORMAL) {
            if (mLanDevicesList != null) {
                ((LanDevicesListViewHolder) holder).bind(mLanDevicesList.get(position));
            }
        } else {
            ((LanDevicesListEmptyViewHolder) holder).setViewText(mEmptyViewText);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mLanDevicesList != null && mLanDevicesList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mLanDevicesList != null && mLanDevicesList.size() > 0) {
            return mLanDevicesList.size();
        } else {
            return 1;
        }
    }

    public class LanDevicesListViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mHostname;
        private TextView mMac;
        private TextView mAddress;
        private TextView mVendor;

        public OnItemClickListener mListener;


        public LanDevicesListViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            mListener = listener;

            mHostname = (TextView) itemView.findViewById(R.id.item_lan_device_hostname);
            mMac = (TextView) itemView.findViewById(R.id.item_lan_device_mac);
            mAddress = (TextView) itemView.findViewById(R.id.item_lan_device_address);
            mVendor = (TextView) itemView.findViewById(R.id.item_lan_device_vendor);

            itemView.setOnClickListener(this);
        }

        public void bind(LanDevice lanDevice) {
            mMac.setText(lanDevice.getMac());
            mHostname.setText(lanDevice.getHostname());
            mAddress.setText(lanDevice.getAddress());
            if (lanDevice.getVendor() != null) {
                mVendor.setText(lanDevice.getVendor());
                mVendor.setVisibility(View.VISIBLE);
            } else {
                mVendor.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(mLanDevicesList.get(position));
                }
            }
        }
    }

    public class LanDevicesListEmptyViewHolder
            extends RecyclerView.ViewHolder {

        private TextView mEmptyTextView;


        public LanDevicesListEmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            mEmptyTextView = (TextView) itemView.findViewById(R.id.empty_device_list);
            mEmptyTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 48);
            mEmptyTextView.setVisibility(View.VISIBLE);
        }

        public void setViewText(String text) {
            mEmptyTextView.setText(text);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(LanDevice lanDevice);
    }
}
