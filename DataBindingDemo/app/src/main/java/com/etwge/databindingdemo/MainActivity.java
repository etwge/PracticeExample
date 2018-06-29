package com.etwge.databindingdemo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.etwge.databindingdemo.databinding.ActivityMainBinding;
import com.etwge.databindingdemo.ui.BaseOperationActivity;
import com.etwge.databindingdemo.ui.ObservableFieldChangeActivity;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
		binding.basicOperationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, BaseOperationActivity.class));
			}
		});
		binding.observableFieldChangeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, ObservableFieldChangeActivity.class));
			}
		});
	}

}
