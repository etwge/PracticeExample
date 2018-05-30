package com.etwge.softkeyboarddemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;

public class KeyBoardManager {

	private static final String KEY_KEYBOARD_HEIGHT = "keyboard_height";
	private boolean mKeyboardVisible;

	public void addOnSoftKeyBoardVisibleListener(final Activity activity, final IKeyBoardVisibleListener listener) {
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
				int keyboardHeight = height - displayHeight - getStatusBarHeight(activity) - getSoftButtonsBarHeight(activity);
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

	private int getStatusBarHeight(Activity activity) {
		Resources resources = activity.getResources();
		int resourceId = resources.getIdentifier("status_bar_height","dimen", "android");
		//获取NavigationBar的高度
		return resources.getDimensionPixelSize(resourceId);
	}

	//导航栏高度
	private  int getSoftButtonsBarHeight(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		//这个方法获取可能不是真实屏幕的高度
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int usableHeight = metrics.heightPixels;
		//获取当前屏幕的真实高度
		activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
		int realHeight = metrics.heightPixels;
		if (realHeight > usableHeight) {
			return realHeight - usableHeight;
		} else {
			return 0;
		}
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
