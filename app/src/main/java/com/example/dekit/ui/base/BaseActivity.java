package com.example.dekit.ui.base;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.dekit.R;
import com.example.dekit.ui.progress_bar.ProgressBarDialog;

public class BaseActivity extends AppCompatActivity {

    private ProgressBarDialog progressBarDialog = null;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            finish();
        }
    }

    private FrameLayout getProgressContainer() {
        return findViewById(R.id.progress_container);
    }

    public void showProgressBarDialog() {
        if (getProgressContainer() != null) {
            getProgressContainer().setVisibility(View.VISIBLE);
        } else {
            if (progressBarDialog == null) {
                progressBarDialog = new ProgressBarDialog();
                progressBarDialog.show(getSupportFragmentManager(), null);
            }
        }
    }

    public void hideProgressBarDialog() {
        if (getProgressContainer() != null) {
            getProgressContainer().setVisibility(View.GONE);
        } else {
            progressBarDialog.dismiss();
            progressBarDialog = null;
        }
    }


    protected void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right,
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                )
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    protected void replaceFragment(Fragment fragment, Bundle bundle) {
        if (bundle != null) fragment.setArguments(bundle);
        replaceFragment(fragment);
    }

    protected void addFragment(Fragment fragment, Bundle bundle) {
        if (bundle != null) fragment.setArguments(bundle);
        addFragment(fragment);
    }

    protected void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right,
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                )
                .add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    protected Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    void clearBackStack() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }
}