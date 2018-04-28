package com.example.swiperefreshlayoutdemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	SwipeRefreshLayout mSwipeRefreshLayout;

	SetSizeDialog.SetSizeBean                                   mSetSizeBean;
	SetProgressViewOffsetDialog.SetProgressViewOffsetBean       mSetProgressViewOffsetBean;
	SetProgressViewEndTargetDialog.SetProgressViewEndTargetBean mSetProgressViewEndTargetBean;
	SetDistanceToTriggerSyncDialog.SetDistanceToTriggerSyncBean mSetDistanceToTriggerSyncBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ListView listView = findViewById(R.id.listView);
		listView.setAdapter(new MyAdapter(this, obtainItemString()));

		mSwipeRefreshLayout = findViewById(R.id.refreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				//模拟网络请求需要3000毫秒，请求完成，设置setRefreshing 为false
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mSwipeRefreshLayout.setRefreshing(false);
					}
				}, 3000);
			}
		});
		mSetSizeBean = new SetSizeDialog.SetSizeBean(SwipeRefreshLayout.DEFAULT);
		mSetProgressViewOffsetBean = new SetProgressViewOffsetDialog.SetProgressViewOffsetBean();
		mSetProgressViewEndTargetBean = new SetProgressViewEndTargetDialog.SetProgressViewEndTargetBean();
		mSetDistanceToTriggerSyncBean = new SetDistanceToTriggerSyncDialog.SetDistanceToTriggerSyncBean();


		//设置进度View的组合颜色，在手指上下滑时使用第一个颜色，在刷新中，会一个个颜色进行切换
		mSwipeRefreshLayout.setColorSchemeColors(Color.BLACK, Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE);

		//如果child是自己自定义的view，可以通过这个回调，告诉mSwipeRefreshLayout的child是否可以滑动
		mSwipeRefreshLayout.setOnChildScrollUpCallback(null);

	}

	private List<String> obtainItemString() {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			list.add("item:" + i);
		}
		return list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		//通过调用item.getItemId()来判断菜单项
		switch (item.getItemId()) {
			case R.id.setSize:
				SetSizeDialog dialog = new SetSizeDialog(MainActivity.this, mSetSizeBean);
				dialog.setOnOKListener(new SetSizeDialog.OnOKListener() {
					@Override
					public void onOKClick() {
						//设置进度View样式的大小，只有两个值DEFAULT和LARGE
						mSwipeRefreshLayout.setSize(mSetSizeBean.size);
					}
				});
				dialog.show();
				break;
			case R.id.setProgressViewOffset:
				SetProgressViewOffsetDialog setProgressViewOffsetDialog = new SetProgressViewOffsetDialog(MainActivity.this,
																										  mSetProgressViewOffsetBean);
				setProgressViewOffsetDialog.setOnOKListener(new SetProgressViewOffsetDialog.OnOKListener() {
					@Override
					public void onOKClick() {
						//设置进度View下拉的起始点和结束点，scale 是指设置是否需要放大或者缩小动画
						mSwipeRefreshLayout.setProgressViewOffset(mSetProgressViewOffsetBean.scale, mSetProgressViewOffsetBean.start,
																  mSetProgressViewOffsetBean.end);
					}
				});
				setProgressViewOffsetDialog.show();
				break;
			case R.id.setProgressViewEndTarget:
				SetProgressViewEndTargetDialog setProgressViewEndTargetDialog = new SetProgressViewEndTargetDialog(MainActivity.this,
																												   mSetProgressViewEndTargetBean);
				setProgressViewEndTargetDialog.setOnOKListener(new SetProgressViewEndTargetDialog.OnOKListener() {
					@Override
					public void onOKClick() {
						//设置进度View下拉的结束点，scale 是指设置是否需要放大或者缩小动画
						mSwipeRefreshLayout.setProgressViewEndTarget(mSetProgressViewOffsetBean.scale, mSetProgressViewOffsetBean.end);
					}
				});
				setProgressViewEndTargetDialog.show();
				break;
			case R.id.setDistanceToTriggerSync:
				SetDistanceToTriggerSyncDialog setDistanceToTriggerSyncDialog = new SetDistanceToTriggerSyncDialog(MainActivity.this,
																												   mSetDistanceToTriggerSyncBean);
				setDistanceToTriggerSyncDialog.setOnOKListener(new SetDistanceToTriggerSyncDialog.OnOKListener() {
					@Override
					public void onOKClick() {
						//设置触发刷新的距离
						mSwipeRefreshLayout.setDistanceToTriggerSync(mSetDistanceToTriggerSyncBean.distance);
					}
				});
				setDistanceToTriggerSyncDialog.show();
				break;
			default:
		}
		return true;
	}


	public class MyAdapter extends ArrayAdapter<String> {

		private MyAdapter(Context context, List<String> objects) {
			super(context, 0, objects);

		}

		@NonNull
		@Override
		public View getView(int position, View convertView, @NonNull ViewGroup parent) {

			String c = getItem(position);

			ViewHolder holder;

			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tvName.setText(c);
			return convertView;
		}

		private class ViewHolder {

			TextView tvName;

			ViewHolder(View itemView) {
				this.tvName = itemView.findViewById(android.R.id.text1);
			}
		}
	}

}
