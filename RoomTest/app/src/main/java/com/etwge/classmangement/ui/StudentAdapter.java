package com.etwge.classmangement.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etwge.classmangement.R;
import com.etwge.classmangement.db.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> implements ItemTouchHelperAdapter  {

	private List<Student> mData;
	private OnItemClickListener mOnItemClickListener;

	public interface OnItemClickListener{
		void onItemClick(int postion);
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
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
		final Student Student = mData.get(position);
		holder.studentName.setText(Student.getStudentName());
		holder.studentGender.setText(Student.getStudentGender());
	}

	@Override
	public int getItemCount() {
		return mData != null ? mData.size() : 0;
	}

	public void setData(List<Student> data) {
		mData = data;
		notifyDataSetChanged();
	}

	public List<Student> getData() {
		return mData;
	}

	@Override
	public void onItemDismiss(int position) {
		mData.remove(position);
		notifyItemRemoved(position);
	}

	public void append(Student Student) {
		if (mData == null) {
			mData = new ArrayList<>();
		}
		mData.add(Student);
		notifyDataSetChanged();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		TextView studentName;
		TextView studentGender;

		public ViewHolder(View itemView) {
			super(itemView);
			studentName = itemView.findViewById(R.id.tv_student_name);
			studentGender = itemView.findViewById(R.id.tv_student_gender);
		}
	}
}
