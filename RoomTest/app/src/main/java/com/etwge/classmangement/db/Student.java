package com.etwge.classmangement.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "student", foreignKeys = @ForeignKey(entity = Classes.class,
	parentColumns = "classId",
	childColumns = "class_id",onDelete = CASCADE))
public class Student {

	@PrimaryKey(autoGenerate = true)
	private int studentId;
	@ColumnInfo(name = "student_name")
	private String studentName;
	@ColumnInfo(name = "student_gender")
	private String studentGender;
	@ColumnInfo(name = "class_id")
	private int classId;

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getStudentGender() {
		return studentGender;
	}

	public void setStudentGender(String studentGender) {
		this.studentGender = studentGender;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	@Override
	public String toString() {
		return "Student{" + "studentId=" + studentId + ", studentName='" + studentName + '\'' + ", classMonitor='" + studentGender + '\'' +
			   ", classId=" + classId + '}';
	}
}
