package com.example.tttn_restaurant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.model.FoodResponse;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    
    private List<FoodResponse> foodList;
    private OnFoodClickListener listener;
    
    public interface OnFoodClickListener {
        void onFoodClick(FoodResponse food);
    }
    
    public FoodAdapter(List<FoodResponse> foodList, OnFoodClickListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
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
        private ImageView imgFood;
        private TextView tvFoodName, tvPrice, tvStatus;
        
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onFoodClick(foodList.get(position));
                }
            });
        }
        
        public void bind(FoodResponse food) {
            tvFoodName.setText(food.getName());
            
            // Định dạng giá tiền theo VND
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvPrice.setText(currencyFormatter.format(food.getPrice()));
            
            // Hiển thị trạng thái
            String status = "";
            int statusColor = R.color.black;
            
            if (!food.getActive()) {
                status = "Đã vô hiệu hóa";
                statusColor = R.color.red;
            } else if ("OUT_OF_STOCK".equals(food.getStatus())) {
                status = "Tạm hết";
                statusColor = R.color.orange;
            } else {
                status = "Có sẵn";
                statusColor = R.color.green;
            }
            
            tvStatus.setText(status);
            tvStatus.setTextColor(itemView.getContext().getResources().getColor(statusColor));
            
            // Hiển thị hình ảnh từ Base64
            if (food.getImageBitmap() != null) {
                imgFood.setImageBitmap(food.getImageBitmap());
            } else {
                imgFood.setImageResource(R.drawable.food_placeholder); // Thay bằng drawable mặc định của bạn
            }
        }
    }
}