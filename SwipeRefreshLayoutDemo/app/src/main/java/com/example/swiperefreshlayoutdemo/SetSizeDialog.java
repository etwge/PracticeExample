package com.example.swiperefreshlayoutdemo;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * 提示对话框
 */
public class SetSizeDialog extends Dialog {


	private final SetSizeBean mSetSize;
	private       TextView    mOk;

	private OnOKListener mOnOKListener;

	public interface OnOKListener {

		void onOKClick();
	}

	public SetSizeDialog(@NonNull Context context, SetSizeBean setSizeBean) {
		super(context, R.style.NoTitleDialog);
		mSetSize = setSizeBean;
		init();
	}


	private void init() {
		View view = View.inflate(getContext(), R.layout.dialog_setsize, null);

		final RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
		radioGroup.check(mSetSize.size == SwipeRefreshLayout.DEFAULT ? R.id.radioButton_default : R.id.radioButton_large);

		mOk = view.findViewById(R.id.button_dialog_ok);
		mOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSetSize.size = radioGroup.getCheckedRadioButtonId() == R.id.radioButton_default ? SwipeRefreshLayout.DEFAULT :
								SwipeRefreshLayout.LARGE;
				if (mOnOKListener != null) {
					mOnOKListener.onOKClick();
				}
				dismiss();
			}
		});
		view.findViewById(R.id.button_dialog_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		setContentView(view);
		Window dialogWindow = getWindow();
		if (dialogWindow != null) {
			dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
	}

	public void setOnOKListener(OnOKListener onOKListener) {
		mOnOKListener = onOKListener;
	}

	static public class SetSizeBean {

		public SetSizeBean(int size) {
			this.size = size;
		}

		int size;
	}
}
