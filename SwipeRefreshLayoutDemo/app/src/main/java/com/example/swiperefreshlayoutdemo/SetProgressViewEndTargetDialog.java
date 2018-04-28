package com.example.swiperefreshlayoutdemo;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SetProgressViewEndTargetDialog extends Dialog {


	private final SetProgressViewEndTargetBean mSetSize;
	private       TextView                     mOk;

	private OnOKListener mOnOKListener;

	public interface OnOKListener {

		void onOKClick();
	}

	public SetProgressViewEndTargetDialog(@NonNull Context context, SetProgressViewEndTargetBean setSizeBean) {
		super(context, R.style.NoTitleDialog);
		mSetSize = setSizeBean;
		init();
	}


	private void init() {
		View view = View.inflate(getContext(), R.layout.dialog_set_progress_end_target_view, null);

		final RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
		radioGroup.check(mSetSize.scale? R.id.radioButton_true : R.id.radioButton_false);

		final EditText end = view.findViewById(R.id.edit_end);
		end.setText(String.valueOf(mSetSize.end));


		mOk = view.findViewById(R.id.button_dialog_ok);
		mOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSetSize.scale = radioGroup.getCheckedRadioButtonId() == R.id.radioButton_true;
				try {
					mSetSize.end = Integer.parseInt(end.getText().toString());
				} catch (Exception ex){

				}
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

	static public class SetProgressViewEndTargetBean {

		boolean scale;
		int end;
	}
}
