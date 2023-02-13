package com.example.dekit.ui.main.choose_type.adapter;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dekit.R;
import com.example.dekit.data.model.ChooseType;
import com.example.dekit.data.room.enteties.Bird;

import java.util.ArrayList;
import java.util.List;

public class ChooseTypeAdapter extends RecyclerView.Adapter<ChooseTypeAdapter.ViewHolder> {

    private List<ChooseType> chooseTypeList = new ArrayList<>();
    private OnItemClickListener onNoteClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onNoteClickListener = onItemClickListener;
    }

    public void setChooseTypeList(List<ChooseType> chooseTypeList) {
        this.chooseTypeList = chooseTypeList;
        notifyDataSetChanged();
    }

    public List<Bird> getChooseTypeList() {
        return new ArrayList(chooseTypeList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChooseType bird = chooseTypeList.get(position);
        holder.bind(bird);
    }

    @Override
    public int getItemCount() {
        return chooseTypeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardView;
        private final ImageView icon;
        private final TextView title;

        public void bind(ChooseType data) {
            title.setText(data.title);
            icon.setImageResource(data.img);
            cardView.setOnClickListener(view -> onNoteClickListener.onNoteClick(data));
        }

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);

            cardView.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.getBackground().setColorFilter(-0x0db26edf, PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.getBackground().clearColorFilter();
                    v.invalidate();
                }
                return false;
            });
        }
    }

    public interface OnItemClickListener {
        void onNoteClick(ChooseType data);
    }
}
