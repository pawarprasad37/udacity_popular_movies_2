package com.example.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.popularmovies.model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "my_movie_database";
    private static final Object LOCK = new Object();
    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context mContext) {
        if (INSTANCE == null) {
            synchronized (LOCK) {
                INSTANCE = Room.databaseBuilder(mContext.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return INSTANCE;
    }

    public abstract MovieDao getMovieDao();

}
