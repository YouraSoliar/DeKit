package com.example.dekit.ui.main.scanner;

import android.content.Intent;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.dekit.data.room.enteties.Bird;
import com.example.dekit.databinding.FragmentScannerBinding;
import com.example.dekit.ui.base.BaseFragment;
import com.example.dekit.util.Converter;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;

import java.lang.reflect.Field;
import java.util.List;

import kotlin.coroutines.CoroutineContext;

public class ScannerFragment extends BaseFragment {
    private Bitmap imageBitmap;

    private ScannerViewModel viewModel;
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
        viewModel = new ViewModelProvider(this).get(ScannerViewModel.class);

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
            Glide
                    .with(requireContext())
                    .load(imageBitmap)
                    .into(binding.imageBird);
            Bitmap bmp = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
            test(bmp);
            //outputGenerator(bmp);
        });
    }

    private void saveBird() {
        String imageSource = Converter.fromBitmap(imageBitmap);
        viewModel.add(new Bird(binding.textViewResult.getText().toString(), imageSource));
        binding.textViewSave.setVisibility(View.GONE);
    }

    private void test(Bitmap imageBitmap) {
        LocalModel localModel = new LocalModel.Builder().setAssetFilePath("ml_models/PlantsModel.tflite").build();

        CustomImageLabelerOptions customImageLabelerOptions = new CustomImageLabelerOptions.Builder(localModel).build();
        Preconditions.checkNotNull(customImageLabelerOptions, "options cannot be null");
        ImageLabeler labeler = ImageLabeling.getClient(
                customImageLabelerOptions);
        try {

            InputImage image =
                    InputImage.fromBitmap(imageBitmap, 0);
            labeler.process(image).addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                @Override
                public void onSuccess(List<ImageLabel> imageLabels) {
                    // Runs model inference and gets result.
                    int index = 0;
                    float max = imageLabels.get(0).getConfidence();

                    for (int i = 0; i < imageLabels.size(); ++i) {
                        if (max < imageLabels.get(i).getConfidence()) {
                            max = imageLabels.get(i).getConfidence();
                            index = i;
                        }
                    }
                    ImageLabel label = imageLabels.get(index);

                    String name = label.getText().replace("_", " ");
                    binding.linearLayoutResult.setVisibility(View.VISIBLE);
                    binding.textViewResult.setText(name);
                    if (!label.getText().equals("None")) {
                        binding.textViewSave.setVisibility(View.VISIBLE);
                    } else {
                        binding.textViewSave.setVisibility(View.GONE);
                    }
                    labeler.close();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("Error, image doesn't recognised", ex.toString());
            labeler.close();
        }
    }
}
