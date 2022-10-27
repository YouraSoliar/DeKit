package com.example.mlbirds;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mlbirds.ml.BirdsModel;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button buttonLoadPhoto;
    private Button buttonTakePhoto;
    private TextView textViewSave;
    private TextView textViewResult;
    private LinearLayout linearLayoutResult;
    private ImageView imageBird;
    private Bitmap imageBitmap;
    private BirdsDatabase birdsDatabase;
    private Handler handler = new Handler(Looper.getMainLooper());
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission();
        compressPNG();
        birdsDatabase = BirdsDatabase.getInstance(getApplication());

        initView();
        initAction();
    }

    private void initView() {
        this.buttonLoadPhoto = findViewById(R.id.buttonLoadPhoto);
        this.buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        this.textViewResult = findViewById(R.id.textViewResult);
        this.textViewSave = findViewById(R.id.textViewSave);
        this.linearLayoutResult = findViewById(R.id.linearLayoutResult);
        this.imageBird = findViewById(R.id.imageBird);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.green)));

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

        textViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBird();
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

    private void saveBird() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String imageSource = Converter.fromBitmap(imageBitmap);
                birdsDatabase.birdsDao().add(new Bird(textViewResult.getText().toString(), imageSource));
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        textViewSave.setVisibility(View.GONE);
                    }
                });
            }
        });
        thread.start();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.next_page) {
            startActivity(StorageActivity.getIntent(this));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        return super.onOptionsItemSelected(item);
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
            linearLayoutResult.setVisibility(View.VISIBLE);
            textViewResult.setText(output.getLabel());
            if (!output.getLabel().toString().equals("None")) {
                textViewSave.setVisibility(View.VISIBLE);
            } else {
                textViewSave.setVisibility(View.GONE);
            }
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