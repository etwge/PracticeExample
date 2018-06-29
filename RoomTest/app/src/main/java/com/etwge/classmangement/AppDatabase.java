package com.etwge.classmangement;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Classes.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
	public abstract ClassDao classDao();
}
