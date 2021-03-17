package com.example.mymovieviewer.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {MoviesEntry.class, ReviewsEntry.class}, version = 4, exportSchema = false)
@TypeConverters({BooleanConverter.class, ImageConverter.class})
public abstract class AppMovieDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "favoriteMovies";
    private static AppMovieDatabase sInstance;

    public static AppMovieDatabase getInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppMovieDatabase.class, AppMovieDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();

            }
        }
        return sInstance;
    }



    public abstract MoviesDao moviesDao();


}
