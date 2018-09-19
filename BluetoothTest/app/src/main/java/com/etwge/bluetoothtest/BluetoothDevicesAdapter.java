package com.etwge.bluetoothtest;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDevicesAdapter extends RecyclerView.Adapter<BluetoothDevicesAdapter.ItemHolder> {

	private List<BluetoothDevice> mDataList;
	private Context               mContext;
	private Callback              mCallback;


	public BluetoothDevicesAdapter(Context context) {
		mContext = context;
	}

	public void appendData(BluetoothDevice device) {
		if (mDataList == null) {
			mDataList = new ArrayList<>();
		} else {
			for (BluetoothDevice bluetoothDevice : mDataList) {
				if (TextUtils.equals(bluetoothDevice.getAddress(), device.getAddress())) {
					return;
				}
			}
		}
		mDataList.add(device);
		notifyDataSetChanged();
	}

	public interface Callback {

		void onItemClick(BluetoothDevice bean);
	}

	public void setDataList(List<BluetoothDevice> dataList) {
		mDataList = dataList;
		notifyDataSetChanged();
	}

	public void setCallback(Callback callback) {
		mCallback = callback;
	}

	@NonNull
	@Override
	public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_devices, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull final ItemHolder holder, int position) {
		BluetoothDevice item = mDataList.get(position);
		holder.mTextDevice.setText(TextUtils.isEmpty(item.getName()) ? item.getAddress() : item.getName());
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCallback != null) {
					mCallback.onItemClick(mDataList.get(holder.getAdapterPosition()));
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	static class ItemHolder extends RecyclerView.ViewHolder {

		TextView  mTextDevice;
		ImageView mImageDeviceIcon;

		ItemHolder(View itemView) {
			super(itemView);
			mTextDevice = itemView.findViewById(R.id.text_item_bluetooth_device);
			mImageDeviceIcon = itemView.findViewById(R.id.image_item_bluetooth_device_icon);
		}
	}

}