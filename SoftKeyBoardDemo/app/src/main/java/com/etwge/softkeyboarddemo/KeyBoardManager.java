package com.etwge.softkeyboarddemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

public class KeyBoardManager {

	private static final String KEY_KEYBOARD_HEIGHT = "keyboard_height";
	private boolean mKeyboardVisible;

	public void addOnSoftKeyBoardVisibleListener(Activity activity, final IKeyBoardVisibleListener listener) {
		final View decorView = activity.getWindow().getDecorView();
		decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Rect rect = new Rect();
				decorView.getWindowVisibleDisplayFrame(rect);
				//计算出可见屏幕的高度
				int displayHeight = rect.bottom - rect.top;
				//获得屏幕整体的高度
				int height = decorView.getHeight();

				//获得键盘高度
				int keyboardHeight = height - displayHeight - rect.top;
				boolean visible = (double) displayHeight / height < 0.8;
				if (visible) {
					saveKeyBoardHeight(decorView.getContext(), keyboardHeight);
				}
				if (visible != mKeyboardVisible) {
					listener.onSoftKeyBoardVisible(visible, keyboardHeight);
				}
				mKeyboardVisible = visible;
			}
		});
	}

	private void saveKeyBoardHeight(Context context, int keyboardHeight) {
		SharedPreferences sp = context.getSharedPreferences("keyboard", Context.MODE_PRIVATE);
		sp.edit().putInt(KEY_KEYBOARD_HEIGHT, keyboardHeight).apply();
	}

	public int getSaveKeyBoardHeight(Context context) {
		SharedPreferences sp = context.getSharedPreferences("keyboard", Context.MODE_PRIVATE);
		return sp.getInt(KEY_KEYBOARD_HEIGHT, 0);
	}

	public boolean isKeyboardVisible() {
		return mKeyboardVisible;
	}

	public interface IKeyBoardVisibleListener {

		void onSoftKeyBoardVisible(boolean visible, int windowBottom);
	}


}
