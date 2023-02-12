package com.example.dekit.ui.main.storage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dekit.databinding.FragmentStorageBinding;
import com.example.dekit.data.room.enteties.Bird;
import com.example.dekit.ui.base.BaseFragment;
import com.example.dekit.ui.main.storage.adapter.BirdsAdapter;

import java.util.List;

public class StorageFragment extends BaseFragment {

    private FragmentStorageBinding binding;

    private BirdsAdapter adapter = new BirdsAdapter();
    private StorageViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStorageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(StorageViewModel.class);

        init();
    }

    private void init() {
        initView();
        initListeners();
        initObservers();
    }

    private void initObservers() {
        getMainActivity().showProgressBarDialog();
        viewModel.getBirds().observe(getViewLifecycleOwner(), (Observer<List<Bird>>) birds -> {
            adapter.setBirds(birds);
            getMainActivity().hideProgressBarDialog();
        });
    }


    private void initView() {
        binding.recyclerViewBirds.setAdapter(adapter);
    }

    private void initListeners() {
        adapter.setOnNoteClickListener(bird -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + bird.getUrl()));
            startActivity(intent);
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Bird bird = adapter.getBirds().get(position);
                viewModel.remove(bird);

            }
        });

        itemTouchHelper.attachToRecyclerView(binding.recyclerViewBirds);
    }

    @Override
    public void onResume() {
        viewModel.refreshList();
        super.onResume();
    }
}