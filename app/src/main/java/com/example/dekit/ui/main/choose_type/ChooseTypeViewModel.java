package com.example.dekit.ui.main.choose_type;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.dekit.R;
import com.example.dekit.data.model.ChooseType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class ChooseTypeViewModel extends AndroidViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public MutableLiveData<List<ChooseType>> data = new MutableLiveData<>();

    public ChooseTypeViewModel(@NonNull Application application) {
        super(application);
    }

    public void setData() {
        List<ChooseType> dataList = Arrays.asList(
                new ChooseType(R.string.add, R.drawable.ic_add, ""),
                new ChooseType(R.string.birds, R.drawable.ic_bird, "BirdsModel.tflite"),
                new ChooseType(R.string.plants, R.drawable.ic_flower, "PlantsModel.tflite")
        );
        data.postValue(dataList);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}