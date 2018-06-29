package com.etwge.classmangement;

import android.arch.persistence.room.Room;
import android.content.Context;

class AppDataBaseManger {

	private volatile static AppDatabase ourInstance;

	private AppDataBaseManger() {
	}

	public static AppDatabase getSingleton(Context context) {
		if (ourInstance == null) {
			synchronized (AppDatabase.class) {
				if (ourInstance == null) {
					ourInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "class_management").build();
				}
			}
		}
		return ourInstance;
	}
}