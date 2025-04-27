package com.example.tttn_restaurant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.model.Bill;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BillHistoryAdapter extends RecyclerView.Adapter<BillHistoryAdapter.BillViewHolder> {
    private List<Bill> billList;
    private OnBillClickListener listener;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    
    public interface OnBillClickListener {
        void onBillClick(Bill bill);
    }
    
    public BillHistoryAdapter(List<Bill> billList, OnBillClickListener listener) {
        this.billList = billList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bill_history, parent, false);
        return new BillViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = billList.get(position);
        holder.bind(bill);
    }
    
    @Override
    public int getItemCount() {
        return billList.size();
    }
    
    class BillViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBillId, tvCustomerName, tvAmount, tvDate, tvStatus;
        private CardView cardView;
        
        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBillId = itemView.findViewById(R.id.tvBillId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            cardView = itemView.findViewById(R.id.cardViewBill);
        }
        
        public void bind(Bill bill) {
            tvBillId.setText("Hóa đơn #" + bill.getId());
            tvCustomerName.setText(bill.getCustomerName());
            tvAmount.setText(currencyFormat.format(bill.getFinalPrice()));
            tvDate.setText(bill.getCreatedAt());
            tvStatus.setText(getStatusText(bill.getStatus()));
            
            // Set màu sắc tùy theo trạng thái
            int statusColor = getStatusColor(bill.getStatus());
            tvStatus.setTextColor(statusColor);
            
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBillClick(bill);
                }
            });
        }
        
        private String getStatusText(Bill.BillStatus status) {
            switch (status) {
                case PAID:
                    return "Đã thanh toán";
                case PENDING:
                    return "Chờ thanh toán";
                case CANCELLED:
                    return "Đã hủy";
                case REFUNDED:
                    return "Đã hoàn tiền";
                default:
                    return "";
            }
        }
        
        private int getStatusColor(Bill.BillStatus status) {
            Context context = itemView.getContext();
            switch (status) {
                case PAID:
                    return ContextCompat.getColor(context, R.color.green);
                case PENDING:
                    return ContextCompat.getColor(context, R.color.orange);
                case CANCELLED:
                    return ContextCompat.getColor(context, R.color.red);
                case REFUNDED:
                    return ContextCompat.getColor(context, R.color.purple_700);
                default:
                    return ContextCompat.getColor(context, R.color.black);
            }
        }
    }
}