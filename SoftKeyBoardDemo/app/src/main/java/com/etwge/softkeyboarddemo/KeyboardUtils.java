package com.etwge.softkeyboarddemo;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class KeyboardUtils {


	/**
	 * 打卡软键盘
	 *
	 * @param editText 输入框
	 * @param context  上下文
	 */
	public static void openKeyBoard(EditText editText, Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
	}

	/**
	 * 关闭软键盘
	 *
	 * @param editText 输入框
	 * @param context  上下文
	 */
	public static void closeKeyBoard(EditText editText, Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		}
	}

}
