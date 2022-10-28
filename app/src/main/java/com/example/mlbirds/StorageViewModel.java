package com.example.mlbirds;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class StorageViewModel extends AndroidViewModel {

    private BirdsDatabase birdsDatabase;

    public StorageViewModel(@NonNull Application application) {
        super(application);
        birdsDatabase = BirdsDatabase.getInstance(application);
    }

    public LiveData<List<Bird>> getBirds() {
        return birdsDatabase.birdsDao().getBirds();
    }

    public void remove(Bird bird) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                birdsDatabase.birdsDao().remove(bird.getId());
            }
        });
        thread.start();
    }
}