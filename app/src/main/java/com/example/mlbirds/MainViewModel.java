package com.example.mlbirds;

import android.app.Application;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

    private BirdsDao birdsDao;
    private MutableLiveData<Boolean> isFinish = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        birdsDao = BirdsDatabase.getInstance(application).birdsDao();
    }

    public LiveData<Boolean> getIsFinish() {
        return isFinish;
    }

    public void add (Bird bird) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                birdsDao.add(bird);
                isFinish.postValue(true);
            }
        });
        thread.start();
    }
}