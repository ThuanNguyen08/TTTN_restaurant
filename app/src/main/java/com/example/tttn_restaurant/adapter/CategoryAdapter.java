package com.example.tttn_restaurant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.model.CategoryResponse;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    
    private List<CategoryResponse> categoryList;
    private OnCategoryClickListener listener;
    
    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryResponse category);
    }
    
    public CategoryAdapter(List<CategoryResponse> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryResponse category = categoryList.get(position);
        holder.bind(category);
    }
    
    @Override
    public int getItemCount() {
        return categoryList.size();
    }
    
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryName, tvStatus, tvCreatedAt;
        
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCategoryClick(categoryList.get(position));
                }
            });
        }

        public void bind(CategoryResponse category) {
            tvCategoryName.setText(category.getName());

            // Thay đổi màu và nội dung dựa trên trạng thái
            if (category.isActive()) {
                tvStatus.setText("Hoạt động");
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.green));
            } else {
                tvStatus.setText("Không hoạt động");
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
            }

            tvCreatedAt.setText("Ngày tạo: " + category.getCreatedAt());
        }
    }
}