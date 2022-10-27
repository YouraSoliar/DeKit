package com.example.mlbirds;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BirdsAdapter extends RecyclerView.Adapter<BirdsAdapter.BirdsViewHolder> {

    private List<Bird> birds = new ArrayList<>();
    private OnNoteClickListener onNoteClickListener;

    public void setOnNoteClickListener(OnNoteClickListener onNoteClickListener) {
        this.onNoteClickListener = onNoteClickListener;
    }

    public void setBirds(List<Bird> birds) {
        this.birds = birds;
        notifyDataSetChanged();
    }

    public List<Bird> getBirds() {
        return new ArrayList<>(birds);
    }

    @NonNull
    @Override
    public BirdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bird_item, parent,false);
        return new BirdsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BirdsViewHolder holder, int position) {
        Bird bird = birds.get(position);

        holder.textViewItem.setText(bird.getUrl());
        //holder.imageViewItem.setImageBitmap(bird.getBitmapBird());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNoteClickListener.onNoteClick(bird);
            }
        });
    }

    @Override
    public int getItemCount() {
        return birds.size();
    }

    class BirdsViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayoutItem;
        private ImageView imageViewItem;
        private TextView textViewItem;

        public BirdsViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayoutItem = itemView.findViewById(R.id.relativeLayoutItem);
            imageViewItem = itemView.findViewById(R.id.imageViewItem);
            textViewItem = itemView.findViewById(R.id.textViewItem);
        }
    }

    interface OnNoteClickListener {
        void onNoteClick(Bird bird);
    }
}
