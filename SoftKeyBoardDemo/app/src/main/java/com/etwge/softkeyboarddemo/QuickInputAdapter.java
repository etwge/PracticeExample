package com.etwge.softkeyboarddemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

public class QuickInputAdapter extends RecyclerView.Adapter<QuickInputAdapter.ItemHolder> {

	public interface OnItemSelectListener {

		void onItemSelect(String item);
	}

	private List<String>         mDataList;
	private OnItemSelectListener mItemSelectListener;

	public void setOnItemSelectListener(OnItemSelectListener listener) {
		mItemSelectListener = listener;
	}

	public void setDataList(List<String> dataList) {
		mDataList = dataList;
		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		ItemHolder itemHolder = new ItemHolder(
			LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shortcut_content, parent, false));
		itemHolder.mTextItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mItemSelectListener != null) {
					mItemSelectListener.onItemSelect((String) v.getTag());
				}
			}
		});
		return itemHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
		holder.mTextItem.setText(mDataList.get(position));
		holder.mTextItem.setTag(mDataList.get(position));
	}

	@Override
	public int getItemCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	static class ItemHolder extends RecyclerView.ViewHolder {

		TextView mTextItem;

		ItemHolder(View itemView) {
			super(itemView);
			mTextItem = (TextView) itemView.findViewById(R.id.text_item_shortcut_content);
		}
	}

}
