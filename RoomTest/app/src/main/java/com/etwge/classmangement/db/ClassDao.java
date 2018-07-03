package com.etwge.classmangement.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ClassDao {

	@Query("select * from class")
	List<Classes> getAll();

	@Insert
	void insert(Classes... classes);

	@Delete
	void delete(Classes... classes);
}
