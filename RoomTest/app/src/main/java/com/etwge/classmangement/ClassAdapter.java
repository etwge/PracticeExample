package com.etwge.classmangement;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

	private List<Classes> mData;

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
