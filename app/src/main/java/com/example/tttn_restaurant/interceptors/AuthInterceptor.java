package com.example.tttn_restaurant.interceptors;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.example.tttn_restaurant.ui.LoginActivity;
import com.example.tttn_restaurant.utils.SessionManager;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private static final String TAG = "AuthInterceptor";
    private SessionManager sessionManager;
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Nếu không cần xác thực, gửi request nguyên bản
        if (!requiresAuthentication(originalRequest)) {
            return chain.proceed(originalRequest);
        }

        // Kiểm tra token và thêm vào header
        String token = sessionManager.getToken();
        if (token == null) {
            // Không có token, chuyển đến màn hình đăng nhập
            redirectToLogin();
            return chain.proceed(originalRequest);
        }

        // Thêm token vào header
        Request authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        // Tiến hành gửi request với token
        Response response = chain.proceed(authenticatedRequest);

        if (response.code() == 401) {
            // Clone request (không thể tái sử dụng request cũ)
            Request clonedRequest = authenticatedRequest.newBuilder().build();
            response.close(); // Đóng response cũ

            // Thử refresh token
            final CountDownLatch refreshLatch = new CountDownLatch(1);
            final AtomicBoolean refreshed = new AtomicBoolean(false);

            sessionManager.refreshToken(success -> {
                refreshed.set(success);
                refreshLatch.countDown();
            });

            try {
                refreshLatch.await(5, TimeUnit.SECONDS);
                if (refreshed.get()) {
                    // Thêm token mới vào request
                    String newToken = sessionManager.getToken();
                    Request newRequest = clonedRequest.newBuilder()
                            .header("Authorization", "Bearer " + newToken)
                            .build();

                    // Thử lại request với token mới
                    return chain.proceed(newRequest);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Nếu refresh thất bại, xử lý logout
            sessionManager.clearSession();
            redirectToLogin();
        }

        return response;
    }

    // Kiểm tra xem request có cần xác thực không
    private boolean requiresAuthentication(Request request) {
        String url = request.url().toString();
        // Loại trừ các endpoint không cần token như login, register và refresh-token
        return !url.contains("/api/users/login") &&
                !url.contains("/api/users/refresh-token");
    }

    // Chuyển đến màn hình đăng nhập
    private void redirectToLogin() {
        // Sử dụng Handler để chạy trên UI thread
        new Handler(Looper.getMainLooper()).post(() -> {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        });
    }
}