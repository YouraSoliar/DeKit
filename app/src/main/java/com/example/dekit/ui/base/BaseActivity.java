package com.example.dekit.ui.base;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mlbirds.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            finish();
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