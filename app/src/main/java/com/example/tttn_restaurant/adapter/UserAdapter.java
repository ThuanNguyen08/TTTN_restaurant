package com.example.tttn_restaurant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.model.UserResponse;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<UserResponse> userList;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(UserResponse user);
    }

    public UserAdapter(List<UserResponse> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserResponse user = userList.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateData(List<UserResponse> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername, tvFullName, tvRole, tvStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(final UserResponse user, final OnUserClickListener listener) {
            tvUsername.setText(user.getUsername());
            tvFullName.setText(user.getFullName());
            tvRole.setText(user.getRole());

            if (user.getIsActive()) {
                tvStatus.setText("Đang hoạt động");
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvStatus.setText("Vô hiệu hóa");
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(user);
                }
            });
        }
    }
}