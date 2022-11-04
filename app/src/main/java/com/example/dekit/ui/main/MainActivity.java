package com.example.dekit.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mlbirds.R;
import com.example.dekit.ui.base.BaseActivity;
import com.example.dekit.ui.main.scanner.ScannerFragment;
import com.example.dekit.ui.main.storage.StorageFragment;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

public class MainActivity extends BaseActivity {

    private final int CODE_CAMERA = 101;
    private Menu menu;

    //todo move to intent utils
    public void openGallery() {
        mGetContent.launch("image/*");
    }

    //todo move to file utils
    public interface OnImagePickedListener {
        void onImagePicked(Bitmap bitmap);
    }

    ActivityResultLauncher<String> mGetContent;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        configureToolbar();
    }

    //todo move to file utils
    private OnImagePickedListener onImagePickedListener;

    //todo move to file utils
    public void setOnImagePickedListener(OnImagePickedListener onImagePickedListener) {
        this.onImagePickedListener = onImagePickedListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initToolbar();
        getPermission();
        compressPNG();
        initGalleryListener();
        openScannerFragment();
    }

    private void initToolbar() {
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.green)));
        actionBar.setDisplayHomeAsUpEnabled(false);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24);
        assert upArrow != null;
        upArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);
    }

    //todo move to intent utils
    private void initGalleryListener() {
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            Bitmap imageBitmap = null;
            try {
                imageBitmap = UriToBitmap(result);
            } catch (IOException e) {
                e.printStackTrace();
            }

            onImagePickedListener.onImagePicked(imageBitmap);

            Log.d("TAG_URI", result + "");
        });
    }

    //todo move to file utils
    // Fix Compression
    private void compressPNG() {
        //this method for compressing and storing png images such as that format has too long converted row
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size // its not enough
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CODE_CAMERA) {
            if (data != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                onImagePickedListener.onImagePicked(imageBitmap);
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.next_page) {
            openStorageFragment();
        } else if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    //todo move to file utils
    private Bitmap UriToBitmap(Uri result) throws IOException {
        return MediaStore.Images.Media.getBitmap(this.getContentResolver(), result);
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CODE_CAMERA);
    }

    //-- NAVIGATION --//
    public void openScannerFragment() {
        replaceFragment(new ScannerFragment());
    }

    public void openStorageFragment() {
        setVisibilityNextPageBtn(false);
        addFragment(new StorageFragment());
    }

    private void configureToolbar() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof ScannerFragment) {
            setVisibilityNextPageBtn(true);
        }
    }

    private void setVisibilityNextPageBtn(Boolean visibility) {
        MenuItem item = menu.getItem(0);
        item.setVisible(visibility);
        getSupportActionBar().setDisplayHomeAsUpEnabled(!visibility);
    }
}