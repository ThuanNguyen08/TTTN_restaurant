package com.example.tttn_restaurant.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.model.FoodResponse;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderFoodAdapter extends RecyclerView.Adapter<OrderFoodAdapter.FoodViewHolder> {
    
    private List<FoodResponse> foodList;
    private Map<Long, Integer> quantityMap = new HashMap<>(); // FoodId -> Quantity
    private OnFoodQuantityChangeListener listener;
    
    public interface OnFoodQuantityChangeListener {
        void onQuantityChanged(FoodResponse food, int quantity);
    }
    
    public OrderFoodAdapter(List<FoodResponse> foodList, Map<Long, Integer> quantityMap, OnFoodQuantityChangeListener listener) {
        this.foodList = foodList;
        this.quantityMap =  new HashMap<>(quantityMap);
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_food, parent, false);
        return new FoodViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodResponse food = foodList.get(position);
        holder.bind(food);
    }
    
    @Override
    public int getItemCount() {
        return foodList.size();
    }
    
    class FoodViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivFoodImage;
        private TextView tvFoodName;
        private TextView tvFoodPrice;
        private Button btnDecrease;
        private Button btnIncrease;
        private TextView tvQuantity;
        
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
        
        public void bind(FoodResponse food) {
            tvFoodName.setText(food.getName());
            
            // Định dạng giá tiền
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvFoodPrice.setText(currencyFormatter.format(food.getPrice()));

            // Hiển thị ảnh món ăn
            if (food.getImageBitmap() != null) {
                ivFoodImage.setImageBitmap(food.getImageBitmap());
            } else {
                ivFoodImage.setImageResource(R.drawable.food_placeholder); // Thay bằng drawable mặc định của bạn
            }
            
            // Hiển thị số lượng
            int quantity = quantityMap.containsKey(food.getId()) ? quantityMap.get(food.getId()) : 0;
            tvQuantity.setText(String.valueOf(quantity));
            
            // Xử lý sự kiện tăng/giảm số lượng
            btnDecrease.setOnClickListener(v -> {
                int currentQuantity = quantityMap.containsKey(food.getId()) ? quantityMap.get(food.getId()) : 0;
                if (currentQuantity > 0) {
                    currentQuantity--;
                    if (currentQuantity == 0) {
                        quantityMap.remove(food.getId());
                    } else {
                        quantityMap.put(food.getId(), currentQuantity);
                    }
                    tvQuantity.setText(String.valueOf(currentQuantity));
                    if (listener != null) {
                        listener.onQuantityChanged(food, currentQuantity);
                    }
                }
            });
            
            btnIncrease.setOnClickListener(v -> {
                int currentQuantity = quantityMap.containsKey(food.getId()) ? quantityMap.get(food.getId()) : 0;
                currentQuantity++;
                quantityMap.put(food.getId(), currentQuantity);
                tvQuantity.setText(String.valueOf(currentQuantity));
                if (listener != null) {
                    listener.onQuantityChanged(food, currentQuantity);
                }
            });
        }
    }

    public void updateQuantities(Map<Long, Integer> newQuantities) {
        // Cập nhật map số lượng
        this.quantityMap.clear();
        if (newQuantities != null) {
            this.quantityMap.putAll(newQuantities);
        }
        // Cập nhật lại UI
        notifyDataSetChanged();
    }
}