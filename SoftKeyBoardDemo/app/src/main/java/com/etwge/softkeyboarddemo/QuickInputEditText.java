package com.etwge.softkeyboarddemo;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class QuickInputEditText extends AppCompatEditText {

	private static final int DEFAULT_QUICK_BAR_HEIGHT    = 40;
	private static final int DEFAULT_QUICK_LAYOUT_HEIGHT = 260;

	private Context           mContext;
	private View              mQuickInputView;
	private FrameLayout       mContentView;
	private QuickInputAdapter mAdapter;

	private int mQuickBarHeight;
	private int mQuickLayoutHeight;

	private KeyBoardManager mKeyBoardManager;

	final KeyBoardManager.IKeyBoardVisibleListener listener = new KeyBoardManager.IKeyBoardVisibleListener() {
		@Override
		public void onSoftKeyBoardVisible(boolean visible, int windowBottom) {
			final ViewGroup.LayoutParams layoutParams = mQuickInputView.getLayoutParams();
			final ImageButton changeBoard = mQuickInputView.findViewById(R.id.button_change_board);
			if (!visible) {
				if (mQuickInputView.getParent() != null && changeBoard.getTag() == null) {
					removeQuickInputView();
				} else if (mQuickInputView.getParent() != null && changeBoard.getTag().equals(R.drawable.ic_keyboard)) {
					layoutParams.height = mQuickLayoutHeight;
					final View child = mContentView.getChildAt(0);
					final FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) child.getLayoutParams();
					layoutParams2.bottomMargin = mQuickLayoutHeight;
					mQuickInputView.setLayoutParams(layoutParams);
					child.setLayoutParams(layoutParams2);
				}
			}else {
				final int saveKeyBoardHeight = mKeyBoardManager.getSaveKeyBoardHeight(mContext);
				mQuickLayoutHeight = saveKeyBoardHeight != 0 ? saveKeyBoardHeight + mQuickBarHeight :
									 (int) (mContext.getResources().getDisplayMetrics().density * DEFAULT_QUICK_LAYOUT_HEIGHT);
			}
			changeBoard.setTag(null);
		}
	};

	public QuickInputEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mKeyBoardManager = new KeyBoardManager();
		mKeyBoardManager.addOnSoftKeyBoardVisibleListener((Activity) mContext, listener);

		mQuickBarHeight = (int) (mContext.getResources().getDisplayMetrics().density * DEFAULT_QUICK_BAR_HEIGHT);
		final int saveKeyBoardHeight = mKeyBoardManager.getSaveKeyBoardHeight(mContext);
		mQuickLayoutHeight = saveKeyBoardHeight != 0 ? saveKeyBoardHeight + mQuickBarHeight :
							 (int) (context.getResources().getDisplayMetrics().density * DEFAULT_QUICK_LAYOUT_HEIGHT);
		initView();

		setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					removeQuickInputView();
					KeyboardUtils.closeKeyBoard(QuickInputEditText.this, mContext);
				}
			}
		});

	}

	private void initView() {
		mQuickInputView = View.inflate(getContext(), R.layout.view_quick_input, null);
		RecyclerView quickInputList = mQuickInputView.findViewById(R.id.recycler_quick_input);
		quickInputList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
		final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
		dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_layout_quick_input_divider));
		quickInputList.addItemDecoration(dividerItemDecoration);
		quickInputList.setAdapter(mAdapter = new QuickInputAdapter());
		mAdapter.setOnItemSelectListener(new QuickInputAdapter.OnItemSelectListener() {
			@Override
			public void onItemSelect(String item) {
				append(item);
			}
		});
		mContentView = ((Activity) mContext).findViewById(android.R.id.content);
		LayoutTransition layoutTransition = new LayoutTransition();
		mContentView.setLayoutTransition(layoutTransition);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_UP && mQuickInputView != null && mQuickInputView.getParent() == null) {
			requestFocus();
			requestFocusFromTouch();
			addQuickInputView();
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean handle = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mQuickInputView!= null && mQuickInputView.getParent() != null) {
				removeQuickInputView();
				handle = true;
			}
		}
		return handle || super.onKeyDown(keyCode, event);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if (!hasWindowFocus) {
			KeyboardUtils.closeKeyBoard(QuickInputEditText.this, mContext);
		}
	}

	private void addQuickInputView() {
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mQuickLayoutHeight);
		params.gravity = Gravity.BOTTOM;
		mContentView.addView(mQuickInputView, params);
		final ImageButton changeBoard = mQuickInputView.findViewById(R.id.button_change_board);
		changeBoard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mKeyBoardManager.isKeyboardVisible()) {
					//打开软键盘
					KeyboardUtils.openKeyBoard(QuickInputEditText.this, mContext);
					changeBoard.setImageResource(R.drawable.ic_quick_input);
					changeBoard.setTag(R.drawable.ic_quick_input);
					setQuickInputViewHeight(mQuickBarHeight);
					setChildMarginBottom(mQuickBarHeight);
				} else {
					//关闭软键盘
					KeyboardUtils.closeKeyBoard(QuickInputEditText.this, mContext);
					changeBoard.setImageResource(R.drawable.ic_keyboard);
					changeBoard.setTag(R.drawable.ic_keyboard);
				}

			}
		});

		TextView complete = mQuickInputView.findViewById(R.id.button_complete);
		complete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeQuickInputView();
				KeyboardUtils.closeKeyBoard(QuickInputEditText.this, mContext);
			}
		});

		setChildMarginBottom(mQuickLayoutHeight);
	}

	private void setChildMarginBottom(int marginBottom) {
		final View child = mContentView.getChildAt(0);
		final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) child.getLayoutParams();
		layoutParams.bottomMargin = marginBottom;
		child.setLayoutParams(layoutParams);
	}

	private void setQuickInputViewHeight(int height) {
		final ViewGroup.LayoutParams layoutParams = mQuickInputView.getLayoutParams();
		layoutParams.height = height;
		mQuickInputView.setLayoutParams(layoutParams);
	}

	private void removeQuickInputView() {
		if (mQuickInputView != null) {
			mContentView.removeView(mQuickInputView);
		}
		final View child = mContentView.getChildAt(0);
		final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) child.getLayoutParams();
		layoutParams.bottomMargin = 0;
		child.setLayoutParams(layoutParams);
	}

	public void seQuickInputList(List<String> shortcutList) {
		mAdapter.setDataList(shortcutList);
	}
}
