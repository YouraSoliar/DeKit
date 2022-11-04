package com.example.dekit.ui.main.scanner;

import android.content.Intent;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.dekit.room.enteties.Bird;
import com.example.mlbirds.databinding.FragmentScannerBinding;
import com.example.mlbirds.ml.BirdsModel;
import com.example.dekit.ui.base.BaseFragment;
import com.example.dekit.util.Converter;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class ScannerFragment extends BaseFragment {
    private Bitmap imageBitmap;

    private MainViewModel viewModel;
    private FragmentScannerBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScannerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        compressPNG();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initListeners();
    }

    private void compressPNG() {
        //this method for compressing and storing png images such as that format has too long converted row
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {
        binding.buttonLoadPhoto.setOnClickListener(view -> getMainActivity().openGallery());

        binding.textViewResult.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + binding.textViewResult.getText().toString()));
            startActivity(intent);
        });

        binding.buttonTakePhoto.setOnClickListener(view -> {
            getMainActivity().takePhoto();
        });

        binding.textViewSave.setOnClickListener(view -> saveBird());

        getMainActivity().setOnImagePickedListener(bitmap -> {
            imageBitmap = bitmap;
            binding.imageBird.setImageBitmap(imageBitmap);
            Bitmap bmp = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
            outputGenerator(bmp);
        });
    }

    private void saveBird() {
        String imageSource = Converter.fromBitmap(imageBitmap);
        viewModel.add(new Bird(binding.textViewResult.getText().toString(), imageSource));
        binding.textViewSave.setVisibility(View.GONE);
    }

    private void outputGenerator(Bitmap imageBitmap) {
        try {
            BirdsModel model = BirdsModel.newInstance(requireContext());

            // Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(imageBitmap);

            // Runs model inference and gets result.
            BirdsModel.Outputs outputs = model.process(image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();

            int index = 0;
            float max = probability.get(0).getScore();

            for (int i = 0; i < probability.size(); ++i) {
                if (max < probability.get(i).getScore()) {
                    max = probability.get(i).getScore();
                    index = i;
                }
            }

            Category output = probability.get(index);
            binding.linearLayoutResult.setVisibility(View.VISIBLE);
            binding.textViewResult.setText(output.getLabel());
            if (!output.getLabel().equals("None")) {
                binding.textViewSave.setVisibility(View.VISIBLE);
            } else {
                binding.textViewSave.setVisibility(View.GONE);
            }
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
