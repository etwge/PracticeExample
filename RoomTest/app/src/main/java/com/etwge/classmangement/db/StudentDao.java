package com.etwge.classmangement.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface StudentDao {

	@Query("select * from student where class_id = :classId")
	List<Student> getAll(int classId);

	@Insert
	void insert(Student... students);

	@Delete
	void delete(Student... students);
}
