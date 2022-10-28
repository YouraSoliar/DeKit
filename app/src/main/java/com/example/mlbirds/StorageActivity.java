package com.example.mlbirds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class StorageActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBirds;
    private BirdsAdapter adapter;
    private StorageViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        viewModel = new ViewModelProvider(this).get(StorageViewModel.class);

        initView();
        initAction();
    }

    private void initView() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.green)));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24);
        upArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        this.recyclerViewBirds = findViewById(R.id.recyclerViewBirds);
        adapter = new BirdsAdapter();
        recyclerViewBirds.setAdapter(adapter);

        viewModel.getBirds().observe(this, new Observer<List<Bird>>() {
            @Override
            public void onChanged(List<Bird> birds) {
                adapter.setBirds(birds);
            }
        });
    }

    private void initAction() {
        adapter.setOnNoteClickListener(new BirdsAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(Bird bird) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + bird.getUrl().toString()));
                startActivity(intent);
            }
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

        itemTouchHelper.attachToRecyclerView(recyclerViewBirds);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, StorageActivity.class);
        return intent;
    }
}