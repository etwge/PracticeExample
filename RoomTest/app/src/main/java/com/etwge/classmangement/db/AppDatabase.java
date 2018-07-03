package com.etwge.classmangement.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Classes.class, Student.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
	public abstract ClassDao classDao();
	public abstract StudentDao studentDao();
}
