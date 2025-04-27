// BillItemAdapter.java
package com.example.tttn_restaurant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.model.DetailBill;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BillItemAdapter extends RecyclerView.Adapter<BillItemAdapter.BillItemViewHolder> {
    private List<DetailBill> billItems;
    private Map<Long, String> foodNames;
    private NumberFormat currencyFormat;

    public BillItemAdapter(List<DetailBill> billItems, Map<Long, String> foodNames) {
        this.billItems = billItems;
        this.foodNames = foodNames;
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public BillItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_detail, parent, false);
        return new BillItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillItemViewHolder holder, int position) {
        DetailBill item = billItems.get(position);

        holder.tvFoodName.setText(foodNames.get(item.getFoodId()));
        holder.tvQuantity.setText("x" + item.getQuantity());
        holder.tvPrice.setText(currencyFormat.format(item.getPrice()));
        holder.tvAmount.setText(currencyFormat.format(item.getPrice().multiply(new BigDecimal(item.getQuantity()))));
    }

    @Override
    public int getItemCount() {
        return billItems == null ? 0 : billItems.size();
    }

    public void updateData(List<DetailBill> newBillItems) {
        this.billItems = newBillItems;
        notifyDataSetChanged();
    }

    public void updateFoodMap(Map<Long, String> foodNames) {
        this.foodNames = foodNames;
        notifyDataSetChanged();
    }

    static class BillItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvQuantity, tvPrice, tvAmount;

        public BillItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}