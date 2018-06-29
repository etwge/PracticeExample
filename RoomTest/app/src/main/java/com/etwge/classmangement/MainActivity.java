package com.etwge.classmangement;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {

	RecyclerView mClassList;
	ClassAdapter mClassAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		mClassList = findViewById(R.id.lv_class);
		mClassList.setLayoutManager(new LinearLayoutManager(this));
		mClassList.setAdapter(mClassAdapter = new ClassAdapter());
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
		new Thread(new Runnable() {
			@Override
			public void run() {
				final List<Classes> data = AppDataBaseManger.getSingleton(MainActivity.this).classDao().getAll();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mClassAdapter.setData(data);
					}
				});
			}
		}).start();
	}

	private void storeClass(final Classes classes) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				AppDataBaseManger.getSingleton(MainActivity.this).classDao().insert(classes);
			}
		}).start();
	}

}
