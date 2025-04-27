package com.example.tttn_restaurant.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.UserResponse;
import com.example.tttn_restaurant.ui.fragments.BillHistoryFragment;
import com.example.tttn_restaurant.ui.fragments.MenuFragment;
import com.example.tttn_restaurant.ui.fragments.RevenueFragment;
import com.example.tttn_restaurant.ui.fragments.TableFragment;
import com.example.tttn_restaurant.ui.fragments.UserFragment;
import com.example.tttn_restaurant.utils.ErrorHandler;
import com.example.tttn_restaurant.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SessionManager sessionManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        // Kiểm tra nếu chưa đăng nhập thì chuyển qua màn hình đăng nhập
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        // Xác thực token với server
        validateTokenWithServer();

        // Khởi tạo BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.user_service) {
                selectedFragment = new UserFragment();
            } else if (itemId == R.id.menu_service) {
                selectedFragment = new MenuFragment();
            } else if (itemId == R.id.table_service) {
                selectedFragment = new TableFragment();
            } else if (itemId == R.id.bill_service) {
                selectedFragment = new BillHistoryFragment();
            } else if (itemId == R.id.revenue_service) {
                selectedFragment = new RevenueFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Mặc định hiển thị UserFragment khi mở ứng dụng
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UserFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        sessionManager.clearSession();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirectToLogin() {
        sessionManager.clearSession();  // Xóa thông tin phiên
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void validateTokenWithServer() {
        RetrofitClient.getUserApiService().getCurrentUser().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Token hợp lệ, cập nhật thông tin người dùng nếu cần
                    sessionManager.saveUser(response.body());
                } else {
                    ErrorHandler.handleErrorResponse(MainActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e(TAG, "Không thể kết nối đến server: " + t.getMessage());
                // Không chuyển hướng đến đăng nhập ngay trong trường hợp lỗi mạng
                // Chỉ hiển thị thông báo và để người dùng tiếp tục sử dụng với dữ liệu offline
                Toast.makeText(MainActivity.this, "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}