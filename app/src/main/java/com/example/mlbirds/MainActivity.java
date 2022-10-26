package com.example.mlbirds;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mlbirds.ml.BirdsModel;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button buttonLoadPhoto;
    private Button buttonTakePhoto;
    private TextView textViewResult;
    private ImageView imageBird;
    private Bitmap imageBitmap;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();

        initView();
        initAction();
    }

    private void initView() {
        this.buttonLoadPhoto = findViewById(R.id.buttonLoadPhoto);
        this.buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        this.textViewResult = findViewById(R.id.textViewResult);
        this.imageBird = findViewById(R.id.imageBird);
    }

    private void initAction() {

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                imageBitmap = null;
                try{
                    imageBitmap = UriToBitmap(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imageBird.setImageBitmap(imageBitmap);
                outputGenerator(imageBitmap);

                Log.d("TAG_URI", result + "");
            }
        });

        buttonLoadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        textViewResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + textViewResult.getText().toString()));
                startActivity(intent);
            }
        });

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 12) {
            imageBitmap = (Bitmap) data.getExtras().get("data");
            imageBird.setImageBitmap(imageBitmap);
            Bitmap bmp = imageBitmap.copy(Bitmap.Config.ARGB_8888,true) ;
            outputGenerator(bmp);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 11);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 11) {
            if (grantResults.length > 0) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    this.getPermission();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void outputGenerator(Bitmap imageBitmap) {
        try {
            BirdsModel model = BirdsModel.newInstance(MainActivity.this);

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

            textViewResult.setText(output.getLabel());
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Bitmap UriToBitmap (Uri result) throws IOException {
        return MediaStore.Images.Media.getBitmap(this.getContentResolver(), result);
    }
}