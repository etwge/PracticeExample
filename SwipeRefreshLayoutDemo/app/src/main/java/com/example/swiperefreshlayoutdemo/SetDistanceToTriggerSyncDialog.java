package com.example.swiperefreshlayoutdemo;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

public class SetDistanceToTriggerSyncDialog extends Dialog {


	private final SetDistanceToTriggerSyncBean mSetSize;

	private TextView mOk;

	private OnOKListener mOnOKListener;

	public interface OnOKListener {

		void onOKClick();
	}

	public SetDistanceToTriggerSyncDialog(@NonNull Context context, SetDistanceToTriggerSyncBean setSizeBean) {
		super(context, R.style.NoTitleDialog);
		mSetSize = setSizeBean;
		init();
	}


	private void init() {
		View view = View.inflate(getContext(), R.layout.dialog_set_diatance_trigger_sync, null);
		final EditText diatance = view.findViewById(R.id.edit_distance);
		diatance.setText(String.valueOf(mSetSize.distance));

		mOk = view.findViewById(R.id.button_dialog_ok);
		mOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mSetSize.distance = Integer.parseInt(diatance.getText().toString());
				} catch (Exception ex) {

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

	static public class SetDistanceToTriggerSyncBean {

		int distance;
	}
}
