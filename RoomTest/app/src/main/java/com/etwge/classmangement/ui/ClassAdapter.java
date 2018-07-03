package com.etwge.classmangement.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etwge.classmangement.R;
import com.etwge.classmangement.db.Classes;

import java.util.ArrayList;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> implements ItemTouchHelperAdapter  {

	private List<Classes>       mData;
	private OnItemClickListener mOnItemClickListener;

	public interface OnItemClickListener{
		void onItemClick(int position);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(holder.getAdapterPosition());
				}
			}
		});
		final Classes classes = mData.get(position);
		holder.className.setText(classes.getClassName());
		holder.classMonitor.setText(classes.getClassMonitor());
	}

	@Override
	public int getItemCount() {
		return mData != null ? mData.size() : 0;
	}

	public void setData(List<Classes> data) {
		mData = data;
		notifyDataSetChanged();
	}

	public List<Classes> getData() {
		return mData;
	}

	@Override
	public void onItemDismiss(int position) {
		mData.remove(position);
		notifyItemRemoved(position);
	}

	public void append(Classes classes) {
		if (mData == null) {
			mData = new ArrayList<>();
		}
		mData.add(classes);
		notifyDataSetChanged();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		TextView className;
		TextView classMonitor;

		public ViewHolder(View itemView) {
			super(itemView);
			className = itemView.findViewById(R.id.tv_class_name);
			classMonitor = itemView.findViewById(R.id.tv_class_monitor);
		}
	}
}
