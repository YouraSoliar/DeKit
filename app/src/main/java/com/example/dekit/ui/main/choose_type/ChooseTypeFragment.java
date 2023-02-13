package com.example.dekit.ui.main.choose_type;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dekit.databinding.FragmentChooseTypeBinding;
import com.example.dekit.ui.base.BaseFragment;
import com.example.dekit.ui.main.choose_type.adapter.ChooseTypeAdapter;

public class ChooseTypeFragment extends BaseFragment {

    private ChooseTypeViewModel viewModel;
    private FragmentChooseTypeBinding binding;
    private ChooseTypeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChooseTypeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        viewModel = new ViewModelProvider(this).get(ChooseTypeViewModel.class);
        viewModel.setData();
        initAdapter();
        initListeners();
        initObservers();
    }

    private void initAdapter() {
        binding.typeRecycler.setLayoutManager(new GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false));
        adapter = new ChooseTypeAdapter();
        binding.typeRecycler.setAdapter(adapter);
    }

    private void initObservers() {
        viewModel.data.observe(getViewLifecycleOwner(), chooseTypes -> adapter.setChooseTypeList(chooseTypes));
    }


    private void initListeners() {
        adapter.setOnItemClickListener(data -> {
            if (!data.path.isEmpty())
                getMainActivity().openScannerFragment(data.path);
            else
                Toast.makeText(requireContext(), "Sorry, this feature isn't ready for now", Toast.LENGTH_SHORT).show();
        });
    }
}
