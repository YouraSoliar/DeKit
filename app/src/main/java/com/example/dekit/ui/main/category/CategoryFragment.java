package com.example.dekit.ui.main.category;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dekit.databinding.FragmentCategoryBinding;
import com.example.dekit.room.enteties.Bird;
import com.example.dekit.ui.base.BaseFragment;
import com.example.dekit.ui.main.category.adapter.CategoryAdapter;

public class CategoryFragment extends BaseFragment {

    private FragmentCategoryBinding binding;

    private CategoryAdapter adapter = new CategoryAdapter();
    private CategoryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        init();
    }

    private void init() {
        initView();
        initListeners();
        initObservers();
    }

    private void initObservers() {
        viewModel.getBirds().observe(getViewLifecycleOwner(), categories -> adapter.setCategories(categories));
    }


    private void initView() {
        binding.recyclerViewCategory.setAdapter(adapter);
    }

    private void initListeners() {
        adapter.setOnCategoryClickListener(category -> {

        });

        adapter.setOnCategoryLongClickListener(category -> {
           // viewModel.remove(category);
        });
    }

    @Override
    public void onResume() {
        //viewModel.refreshList();
        super.onResume();
    }
}