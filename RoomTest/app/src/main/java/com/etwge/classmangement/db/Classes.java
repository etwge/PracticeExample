package com.etwge.classmangement.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "class")
public class Classes {

	@PrimaryKey(autoGenerate = true)
	private int classId;
	@ColumnInfo(name = "class_name")
	private String className;
	@ColumnInfo(name = "class_monitor")
	private String classMonitor;

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassMonitor() {
		return classMonitor;
	}

	public void setClassMonitor(String classMonitor) {
		this.classMonitor = classMonitor;
	}

	@Override
	public String toString() {
		return "Classes{" + "classId=" + classId + ", className='" + className + '\'' + ", classMonitor='" + classMonitor + '\'' + '}';
	}
}
