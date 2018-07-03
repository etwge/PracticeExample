package com.etwge.classmangement.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.etwge.classmangement.R;
import com.etwge.classmangement.db.AppDataBaseManger;
import com.etwge.classmangement.db.Student;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class StudentActivity extends AppCompatActivity {

	private static final String KEY_CLASS_ID = "classId";

	public static void startup(Activity activity, int classId) {
		Intent intent = new Intent(activity, StudentActivity.class);
		intent.putExtra(KEY_CLASS_ID, classId);
		activity.startActivity(intent);
	}

	RecyclerView   mStudentList;
	StudentAdapter mStudentAdapter;
	private int mClassId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student);
		getClassId();
		initView();
		setTitle(R.string.student_manage);
	}

	private void getClassId() {
		mClassId = getIntent().getIntExtra(KEY_CLASS_ID, 0);
	}

	private void initView() {
		mStudentList = findViewById(R.id.lv_student);
		mStudentList.setLayoutManager(new LinearLayoutManager(this));
		final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, VERTICAL);
		final Drawable drawable = ContextCompat.getDrawable(this, R.drawable.divider);
		if (drawable != null) {
			dividerItemDecoration.setDrawable(drawable);
		}
		mStudentList.addItemDecoration(dividerItemDecoration);
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback() {
			@Override
			public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
				Observable.unsafeCreate(new Observable.OnSubscribe<Integer>() {
					@Override
					public void call(Subscriber<? super Integer> subscriber) {
						Student student = mStudentAdapter.getData().get(viewHolder.getAdapterPosition());
						AppDataBaseManger.getSingleton(StudentActivity.this).studentDao().delete(student);
						subscriber.onNext(viewHolder.getAdapterPosition());
						subscriber.onCompleted();
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
					@Override
					public void call(Integer integer) {
						mStudentAdapter.onItemDismiss(integer);
					}
				});
			}
		});
		itemTouchHelper.attachToRecyclerView(mStudentList);
		mStudentList.setAdapter(mStudentAdapter = new StudentAdapter());
		FloatingActionButton floatingActionButton = findViewById(R.id.btn_add);
		floatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View contentView = LayoutInflater.from(StudentActivity.this).inflate(R.layout.view_input_stuent, null);
				final EditText classNameEdit = contentView.findViewById(R.id.ev_student_name);
				final RadioGroup radioGroup = contentView.findViewById(R.id.radio_group);
				AlertDialog.Builder builder = new AlertDialog.Builder(StudentActivity.this);
				builder.setTitle("请输入：").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final Student student = new Student();
						student.setStudentName(classNameEdit.getText().toString());
						student.setStudentGender(radioGroup.getCheckedRadioButtonId() == R.id.radio_man ? "男" : "女");
						student.setClassId(mClassId);
						storeStudent(student);
						dialog.dismiss();
					}
				}).setView(contentView).show();
			}
		});

		loadClassData();
	}

	private void loadClassData() {
		Observable.unsafeCreate(new Observable.OnSubscribe<List<Student>>() {
			@Override
			public void call(Subscriber<? super List<Student>> subscriber) {
				final List<Student> data = AppDataBaseManger.getSingleton(StudentActivity.this).studentDao().getAll(mClassId);
				subscriber.onNext(data);
				subscriber.onCompleted();
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<Student>>() {
			@Override
			public void call(List<Student> studentList) {
				mStudentAdapter.setData(studentList);
			}
		});
	}

	private void storeStudent(final Student student) {
		Observable.unsafeCreate(new Observable.OnSubscribe<List<Student>>() {
			@Override
			public void call(Subscriber<? super List<Student>> subscriber) {
				AppDataBaseManger.getSingleton(StudentActivity.this).studentDao().insert(student);
				final List<Student> data = AppDataBaseManger.getSingleton(StudentActivity.this).studentDao().getAll(mClassId);
				subscriber.onNext(data);
				subscriber.onCompleted();
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<Student>>() {
			@Override
			public void call(List<Student> studentList) {
				mStudentAdapter.setData(studentList);
			}
		});
	}
}
