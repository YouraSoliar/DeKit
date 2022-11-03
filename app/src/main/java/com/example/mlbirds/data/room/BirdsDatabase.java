package com.example.mlbirds.data.room;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.mlbirds.data.room.dao.BirdsDao;
import com.example.mlbirds.util.Converter;
import com.example.mlbirds.data.room.enteties.Bird;

@Database(entities = {Bird.class}, version = 4, exportSchema = false)
@TypeConverters(Converter.class)
public abstract class BirdsDatabase extends RoomDatabase {
    private static BirdsDatabase instance = null;
    private static final String DB_NAME = "birds.db";

    public static BirdsDatabase getInstance(Application application) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    application,
                    BirdsDatabase.class,
                    DB_NAME
            ).build();
        }
        return instance;
    }

    public abstract BirdsDao birdsDao();
}
