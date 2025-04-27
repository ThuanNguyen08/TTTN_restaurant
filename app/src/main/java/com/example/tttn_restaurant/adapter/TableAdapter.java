package com.example.tttn_restaurant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.model.TableResponse;
import com.example.tttn_restaurant.model.TableStatus;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {
    
    private List<TableResponse> tableList;
    private Context context;
    private OnTableActionListener listener;
    
    public interface OnTableActionListener {
        void onTableClick(TableResponse table);
        void onOrderClick(TableResponse table);
        void onPaymentClick(TableResponse table);
    }
    
    public TableAdapter(Context context, List<TableResponse> tableList, OnTableActionListener listener) {
        this.context = context;
        this.tableList = tableList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false);
        return new TableViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        TableResponse table = tableList.get(position);
        holder.bind(table);
    }
    
    @Override
    public int getItemCount() {
        return tableList.size();
    }
    
    public void updateData(List<TableResponse> newTableList) {
        this.tableList = newTableList;
        notifyDataSetChanged();
    }
    
    class TableViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTableName;
        private final TextView tvTableStatus;
        private final ImageView ivTableImage;
        private final Button btnOrder;
        private final Button btnPayment;
        private final CardView cardView;
        
        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvTableName = itemView.findViewById(R.id.tvTableName);
            tvTableStatus = itemView.findViewById(R.id.tvTableStatus);
            ivTableImage = itemView.findViewById(R.id.ivTableImage);
            btnOrder = itemView.findViewById(R.id.btnOrder);
            btnPayment = itemView.findViewById(R.id.btnPayment);
            
            // Xử lý sự kiện click vào item
            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTableClick(tableList.get(position));
                }
            });
            
            // Xử lý sự kiện click vào nút Order
            btnOrder.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onOrderClick(tableList.get(position));
                }
            });
            
            // Xử lý sự kiện click vào nút Thanh toán
            btnPayment.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPaymentClick(tableList.get(position));
                }
            });
        }
        
        public void bind(TableResponse table) {
            tvTableName.setText(table.getName());
            
            // Hiển thị trạng thái và đặt màu tương ứng
            String statusText = "Trạng thái: ";
            int statusColor = R.color.table_default;
            
            switch (table.getStatus()) {
                case TableStatus.AVAILABLE:
                    statusText += "Có sẵn";
                    statusColor = R.color.table_available;
                    break;
                case TableStatus.OCCUPIED:
                    statusText += "Có khách";
                    statusColor = R.color.table_occupied;
                    break;
                case TableStatus.RESERVED:
                    statusText += "Đã đặt";
                    statusColor = R.color.table_reserved;
                    break;
                case TableStatus.MAINTENANCE:
                    statusText += "Bảo trì";
                    statusColor = R.color.table_maintenance;
                    break;
                default:
                    statusText += table.getStatus();
                    break;
            }
            
            tvTableStatus.setText(statusText);
            tvTableStatus.setTextColor(ContextCompat.getColor(context, statusColor));
            
            // Điều chỉnh trạng thái các nút dựa vào trạng thái bàn
            boolean isAvailableForOrder = table.getStatus().equals(TableStatus.AVAILABLE) || 
                                          table.getStatus().equals(TableStatus.OCCUPIED);
            
            boolean isOccupiedForPayment = table.getStatus().equals(TableStatus.OCCUPIED);
            
            btnOrder.setEnabled(isAvailableForOrder);
            btnPayment.setEnabled(isOccupiedForPayment);
        }
    }
}