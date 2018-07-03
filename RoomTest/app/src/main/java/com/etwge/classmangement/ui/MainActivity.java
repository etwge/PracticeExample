package com.etwge.classmangement.ui;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.etwge.classmangement.R;
import com.etwge.classmangement.db.AppDataBaseManger;
import com.etwge.classmangement.db.Classes;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class MainActivity extends AppCompatActivity {

	RecyclerView mClassList;
	ClassAdapter mClassAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		setTitle(R.string.class_manage);
	}

	private void initView() {
		mClassList = findViewById(R.id.lv_class);
		mClassList.setLayoutManager(new LinearLayoutManager(this));
		final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, VERTICAL);
		final Drawable drawable = ContextCompat.getDrawable(this, R.drawable.divider);
		if (drawable != null) {
			dividerItemDecoration.setDrawable(drawable);
		}
		mClassList.addItemDecoration(dividerItemDecoration);
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback() {
			@Override
			public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
				Observable.unsafeCreate(new Observable.OnSubscribe<Integer>() {
					@Override
					public void call(Subscriber<? super Integer> subscriber) {
						Classes classes = mClassAdapter.getData().get(viewHolder.getAdapterPosition());
						AppDataBaseManger.getSingleton(MainActivity.this).classDao().delete(classes);
						subscriber.onNext(viewHolder.getAdapterPosition());
						subscriber.onCompleted();
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
					@Override
					public void call(Integer integer) {
						mClassAdapter.onItemDismiss(integer);
					}
				});
			}
		});
		itemTouchHelper.attachToRecyclerView(mClassList);
		mClassList.setAdapter(mClassAdapter = new ClassAdapter());
		mClassAdapter.setOnItemClickListener(new ClassAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(int position) {
				StudentActivity.startup(MainActivity.this, mClassAdapter.getData().get(position).getClassId());
			}
		});
		FloatingActionButton floatingActionButton = findViewById(R.id.btn_add);
		floatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_input_class, null);
				final EditText classNameEdit = contentView.findViewById(R.id.ev_class_name);
				final EditText classMonitorEdit = contentView.findViewById(R.id.ev_class_monitor);
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("请输入：").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final Classes classes = new Classes();
						classes.setClassName(classNameEdit.getText().toString());
						classes.setClassMonitor(classMonitorEdit.getText().toString());
						storeClass(classes);
						dialog.dismiss();
					}
				}).setView(contentView).show();
			}
		});

		loadClassData();
	}

	private void loadClassData() {
		Observable.unsafeCreate(new Observable.OnSubscribe<List<Classes>>() {
			@Override
			public void call(Subscriber<? super List<Classes>> subscriber) {
				final List<Classes> classes = AppDataBaseManger.getSingleton(MainActivity.this).classDao().getAll();
				subscriber.onNext(classes);
				subscriber.onCompleted();
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<Classes>>() {
			@Override
			public void call(List<Classes> classes) {
				mClassAdapter.setData(classes);
			}
		});
	}

	private void storeClass(final Classes classes) {
		Observable.unsafeCreate(new Observable.OnSubscribe<List<Classes>>() {
			@Override
			public void call(Subscriber<? super List<Classes>> subscriber) {
				AppDataBaseManger.getSingleton(MainActivity.this).classDao().insert(classes);
				final List<Classes> classes = AppDataBaseManger.getSingleton(MainActivity.this).classDao().getAll();
				subscriber.onNext(classes);
				subscriber.onCompleted();
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<Classes>>() {
			@Override
			public void call(List<Classes> classes) {
				mClassAdapter.setData(classes);
			}
		});
	}

}
