package com.example.mlbirds;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class StorageViewModel extends AndroidViewModel {

    private BirdsDatabase birdsDatabase;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<Bird>> birds = new MutableLiveData<>();

    public StorageViewModel(@NonNull Application application) {
        super(application);
        birdsDatabase = BirdsDatabase.getInstance(application);
    }

    public LiveData<List<Bird>> getBirds() {
        return birds;
    }

    public void refreshList() {
        Disposable disposable = birdsDatabase.birdsDao().getBirds()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Bird>>() {
                    @Override
                    public void accept(List<Bird> birdsFromDb) throws Throwable {
                        birds.setValue(birdsFromDb);
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void remove(Bird bird) {
        Disposable disposable = birdsDatabase.birdsDao().remove(bird.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Throwable {
                        refreshList();
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}