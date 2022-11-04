<<<<<<<< HEAD:app/src/main/java/com/example/mlbirds/ui/main/storage/StorageViewModel.java
package com.example.mlbirds.ui.main.storage;
========
package com.example.dekit;
>>>>>>>> master:app/src/main/java/com/example/dekit/StorageViewModel.java

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mlbirds.data.room.enteties.Bird;
import com.example.mlbirds.data.room.BirdsDatabase;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
                .delay(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(birdsFromDb -> birds.setValue(birdsFromDb), throwable -> Log.e("ErrorMessage", throwable.getMessage()));
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
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.e("ErrorMessage", throwable.getMessage());
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