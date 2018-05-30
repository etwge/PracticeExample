package com.etwge.softkeyboarddemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		QuickInputEditText quickInputEditText1 = findViewById(R.id.edit_quick_input1);
		quickInputEditText1.seQuickInputList(obtainList1());

		QuickInputEditText quickInputEditText2 = findViewById(R.id.edit_quick_input2);
		quickInputEditText2.seQuickInputList(obtainList2());
	}

	private List<String> obtainList1() {
		return Arrays.asList("用户体验好", "界面流畅", "UI美观");
	}
	private List<String> obtainList2() {
		return Arrays.asList("功能不够丰富", "偶尔出现卡顿现象");
	}
}
