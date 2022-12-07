package com.example.dekit.ui.main.category.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dekit.R;
import com.example.dekit.room.enteties.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories = new ArrayList<>();
    private OnCategoryClickListener onCategoryClickListener; //open category
    private OnCategoryLongClickListener onCategoryLongClickListener; //delete category

    public void setOnCategoryClickListener(OnCategoryClickListener onCategoryClickListener) {
        this.onCategoryClickListener = onCategoryClickListener;
    }

    public void setOnCategoryLongClickListener(OnCategoryLongClickListener onCategoryLongClickListener) {
        this.onCategoryLongClickListener = onCategoryLongClickListener;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryAdapter.CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.textViewItem.setText(category.getTitle());

        holder.itemView.setOnClickListener(view -> onCategoryClickListener.onCategoryClick(category));

        holder.itemView.setOnLongClickListener(view -> onCategoryLongClickListener.onCategoryLongClick(category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewItem;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.textViewCategory);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public interface OnCategoryLongClickListener {
        void onCategoryLongClick(Category category);
    }
}
