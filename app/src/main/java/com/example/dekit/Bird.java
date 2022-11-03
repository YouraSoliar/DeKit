package com.example.dekit;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "birds")
public class Bird {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String url;
    private String stringBitmap;

    public Bird(int id, String url, String stringBitmap) {
        this.id = id;
        this.url = url;
        this.stringBitmap = stringBitmap;
    }

    @Ignore
    public Bird(String url, String bitmapBird) {
        this(0, url, bitmapBird);
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getStringBitmap() {
        return stringBitmap;
    }
}
