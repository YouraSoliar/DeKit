package com.example.dekit.ui.main.choose_type.adapter;

import android.view.LayoutInflater;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
        }
    }

    public interface OnItemClickListener {
        void onNoteClick(ChooseType data);
    }
}
