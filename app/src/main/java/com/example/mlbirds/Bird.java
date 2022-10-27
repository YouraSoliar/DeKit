package com.example.mlbirds;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "birds")
public class Bird {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String url;
    //private Bitmap bitmapBird;

    public Bird(int id, String url) {
        this.id = id;
        this.url = url;
        //this.bitmapBird = bitmapBird;
    }

    @Ignore
    public Bird(String url) {
        this(0, url);
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

//    public Bitmap getBitmapBird() {
//        return bitmapBird;
//    }
}
