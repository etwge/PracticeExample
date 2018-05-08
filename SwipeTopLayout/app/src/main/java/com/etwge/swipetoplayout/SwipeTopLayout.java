package com.etwge.swipetoplayout;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import static android.support.v4.widget.ViewDragHelper.INVALID_POINTER;


public class SwipeTopLayout extends ViewGroup {

	private static final float DRAG_RATE = 1f;

	private static final int   SWIPE_VIEW_ON_TOP               = 1;
	private static final int   SWIPE_VIEW_ON_BOTTOM            = 2;
	private static final int   ANIMATE_TO_START_DURATION       = 200;
	private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
	private final DecelerateInterpolator mDecelerateInterpolator;

	private View    mTopView;
	private View    mSwipeView;
	private float   mInitialMotionY;
	private int     mTouchSlop;
	private boolean mIsBeingDragged;
	private int     mActivePointerId;
	private float   mInitialDownY;
	private int     mCurrentSwipeOffsetTop;
	private int     mOriginalOffsetTop;
	private int     mTargetTop;

	private int mSwipeViewPosition = SWIPE_VIEW_ON_BOTTOM;
	private int mFrom;

	public SwipeTopLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		ensureView();
		int height = MeasureSpec.getSize(heightMeasureSpec);
		measureChild(mTopView, widthMeasureSpec, heightMeasureSpec);
		mSwipeView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		setMeasuredDimension(widthMeasureSpec, height);
	}

	private void ensureView() {
		if (getChildCount() != 2) {
			return;
		}

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (i == 0) {
				mTopView = child;
			} else {
				mSwipeView = child;
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int topViewLeft = getPaddingLeft();
		int topViewTop = getPaddingTop();
		int topViewRight = topViewLeft + mTopView.getMeasuredWidth();
		int topViewBottom = topViewTop + mTopView.getMeasuredHeight();
		mTopView.layout(topViewLeft, topViewTop, topViewRight, topViewBottom);

		int swipeViewLeft = getPaddingLeft();
		int swipeViewRight = swipeViewLeft + mSwipeView.getMeasuredWidth();
		int swipeViewBottom = topViewBottom + mSwipeView.getMeasuredHeight();
		mSwipeView.layout(swipeViewLeft, topViewBottom, swipeViewRight, swipeViewBottom);

		mOriginalOffsetTop = mCurrentSwipeOffsetTop = mSwipeView.getTop();
	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		ensureView();

		final int action = ev.getActionMasked();
		int pointerIndex;

		//		if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
		//			mReturningToStart = false;
		//		}
		//
		//		if (!isEnabled() || mReturningToStart || canChildScrollUp()
		//			|| mRefreshing || mNestedScrollInProgress) {
		//			// Fail fast if we're not in a state where a swipe is possible
		//			return false;
		//		}

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				//				setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView.getTop());
				mOriginalOffsetTop = mSwipeView.getTop();
				mActivePointerId = ev.getPointerId(0);
				mIsBeingDragged = false;

				pointerIndex = ev.findPointerIndex(mActivePointerId);
				if (pointerIndex < 0) {
					return false;
				}
				mInitialDownY = ev.getY(pointerIndex);
				break;

			case MotionEvent.ACTION_MOVE:
				if (mActivePointerId == INVALID_POINTER) {
					//					Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
					return false;
				}

				pointerIndex = ev.findPointerIndex(mActivePointerId);
				if (pointerIndex < 0) {
					return false;
				}
				final float y = ev.getY(pointerIndex);
				startDragging(y);
				break;

			case MotionEvent.ACTION_POINTER_UP:
				//				onSecondaryPointerUp(ev);
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				mIsBeingDragged = false;
				mActivePointerId = INVALID_POINTER;
				break;
		}

		return mIsBeingDragged;
	}

	private void startDragging(float y) {
		final float yDiff = y - mInitialDownY;
		if (Math.abs(yDiff) > mTouchSlop && !mIsBeingDragged) {
			mInitialMotionY = mInitialDownY + mTouchSlop;
			mIsBeingDragged = true;
			//			mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getActionMasked();
		int pointerIndex = -1;
		//
		//		if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
		//			mReturningToStart = false;
		//		}
		//
		//		if (!isEnabled() || mReturningToStart || canChildScrollUp()
		//			|| mRefreshing || mNestedScrollInProgress) {
		//			// Fail fast if we're not in a state where a swipe is possible
		//			return false;
		//		}

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				mActivePointerId = ev.getPointerId(0);
				mIsBeingDragged = false;
				mOriginalOffsetTop = mSwipeView.getTop();
				break;

			case MotionEvent.ACTION_MOVE: {
				pointerIndex = ev.findPointerIndex(mActivePointerId);
				if (pointerIndex < 0) {
					//					Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
					return false;
				}

				final float y = ev.getY(pointerIndex);
				startDragging(y);

				if (mIsBeingDragged) {
					final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
					if (mSwipeViewPosition == SWIPE_VIEW_ON_BOTTOM) {
						if (overscrollTop < 0) {
							moveSwipeView(overscrollTop);
						} else {
							return false;
						}
					} else {
						if (overscrollTop > 0) {
							moveSwipeView(overscrollTop);
						} else {
							return false;
						}
					}

				}
				break;
			}
			case MotionEvent.ACTION_POINTER_DOWN: {
				pointerIndex = ev.getActionIndex();
				if (pointerIndex < 0) {
					//					Log.e(LOG_TAG,
					//						  "Got ACTION_POINTER_DOWN event but have an invalid action index.");
					return false;
				}
				mActivePointerId = ev.getPointerId(pointerIndex);
				break;
			}

			case MotionEvent.ACTION_POINTER_UP:
				//				onSecondaryPointerUp(ev);
				break;

			case MotionEvent.ACTION_UP: {
				pointerIndex = ev.findPointerIndex(mActivePointerId);
				if (pointerIndex < 0) {
					//					Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
					return false;
				}

				if (mIsBeingDragged) {
					final float y = ev.getY(pointerIndex);
					final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
					mIsBeingDragged = false;
					finishSwipe(overscrollTop);
				}
				mActivePointerId = INVALID_POINTER;
				return false;
			}
			case MotionEvent.ACTION_CANCEL:
				return false;
		}

		return true;
	}

	private void finishSwipe(float overscrollTop) {
		if (mSwipeViewPosition == SWIPE_VIEW_ON_BOTTOM) {
			int targetTop = (int) (overscrollTop + mOriginalOffsetTop);
			if (targetTop < mTopView.getMeasuredHeight() / 2) {
				targetTop = 0;
				mTargetTop = targetTop;
				animateOffsetToStartPosition(mCurrentSwipeOffsetTop, null);
			} else {
				targetTop = mOriginalOffsetTop;
				mTargetTop = targetTop;
				animateOffsetToStartPosition(mCurrentSwipeOffsetTop, null);
			}
			setSwipeViewOffsetAndTop(targetTop - mCurrentSwipeOffsetTop);
		} else {
			int targetTop = (int) (overscrollTop + mOriginalOffsetTop);
			if (targetTop < mTopView.getMeasuredHeight() / 2) {
				targetTop = mOriginalOffsetTop;
				mTargetTop = targetTop;
				animateOffsetToTopPosition(mCurrentSwipeOffsetTop, null);
			} else {
				targetTop = mTopView.getMeasuredHeight();
				mTargetTop = targetTop;
				animateOffsetToTopPosition(mCurrentSwipeOffsetTop, null);
			}
			setSwipeViewOffsetAndTop(targetTop - mCurrentSwipeOffsetTop);
		}

	}

	void moveToStart(float interpolatedTime) {
		int targetTop = 0;
		targetTop = (mFrom + (int) ((this.mTargetTop - mFrom) * interpolatedTime));
		int offset = targetTop - mSwipeView.getTop();
		setSwipeViewOffsetAndTop(offset);
	}

	void moveToTop(float interpolatedTime) {
		int targetTop = 0;
		targetTop = (mFrom + (int) ((this.mTargetTop - mFrom) * interpolatedTime));
		int offset = targetTop - mSwipeView.getTop();
		setSwipeViewOffsetAndTop(offset);
	}

	private final Animation mAnimateToStartPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			moveToStart(interpolatedTime);
		}
	};

	private final Animation mAnimateToTopPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			moveToTop(interpolatedTime);
		}
	};


	final Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (mSwipeView.getTop() == 0) {
				mSwipeViewPosition = SWIPE_VIEW_ON_TOP;
			} else {
				mSwipeViewPosition = SWIPE_VIEW_ON_BOTTOM;
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	};
	private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
		mFrom = from;
		mAnimateToStartPosition.reset();
		mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
		mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);


		mAnimateToStartPosition.setAnimationListener(mAnimationListener);
		mSwipeView.clearAnimation();
		mSwipeView.startAnimation(mAnimateToStartPosition);

	}

	private void animateOffsetToTopPosition(int from, Animation.AnimationListener listener) {
		mFrom = from;
		mAnimateToTopPosition.reset();
		mAnimateToTopPosition.setDuration(ANIMATE_TO_START_DURATION);
		mAnimateToTopPosition.setInterpolator(mDecelerateInterpolator);


		mAnimateToTopPosition.setAnimationListener(mAnimationListener);
		mSwipeView.clearAnimation();
		mSwipeView.startAnimation(mAnimateToTopPosition);

	}

	private void moveSwipeView(float overscrollTop) {
		if (mSwipeViewPosition == SWIPE_VIEW_ON_BOTTOM) {
			int targetTop = (int) (overscrollTop + mOriginalOffsetTop);
			if (targetTop < 0) {
				targetTop = 0;
			}
			setSwipeViewOffsetAndTop(targetTop - mCurrentSwipeOffsetTop);
		} else {
			int targetTop = (int) (overscrollTop + mOriginalOffsetTop);
			if (targetTop > mTopView.getMeasuredHeight()) {
				targetTop = mTopView.getMeasuredHeight();
			}
			setSwipeViewOffsetAndTop(targetTop - mCurrentSwipeOffsetTop);
		}
	}

	private void setSwipeViewOffsetAndTop(int offset) {
		ViewCompat.offsetTopAndBottom(mSwipeView, offset);
		mCurrentSwipeOffsetTop = mSwipeView.getTop();
	}
}
