package com.example.tttn_restaurant.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.tttn_restaurant.model.ErrorResponse;
import com.example.tttn_restaurant.ui.LoginActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Response;

public class ErrorHandler {

    // Xử lý lỗi từ Response (HTTP)
    public static void handleErrorResponse(Context context, Response<?> response) {
        try {
            if (response.code() == 401) {
                handleUnauthorized(context);
            } else if (response.code() == 403) {
                handleForbidden(context);
            } else if (response.code() >= 500) {
                showServerErrorDialog(context);
            } else {
                // Các lỗi 400, 404, 409...
                String message = getErrorMessage(response);
                Toast.makeText(context, message != null ? message : "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Đã xảy ra lỗi không xác định", Toast.LENGTH_SHORT).show();
        }
    }

    // Xử lý lỗi khi gọi API thất bại (network fail, timeout, no internet...)
    public static void handleFailure(Context context, Throwable t) {
        if (t instanceof SocketTimeoutException) {
            Toast.makeText(context, "Kết nối quá thời gian, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        } else if (t instanceof UnknownHostException) {
            Toast.makeText(context, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Đã xảy ra lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ================== PRIVATE SUPPORT ==================

    private static void handleUnauthorized(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        sessionManager.clearSession();

        Toast.makeText(context, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private static void handleForbidden(Context context) {
        Toast.makeText(context, "Bạn không có quyền thực hiện chức năng này", Toast.LENGTH_SHORT).show();
    }

    private static void showServerErrorDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Lỗi hệ thống")
                .setMessage("Máy chủ đang gặp sự cố. Vui lòng thử lại sau!")
                .setPositiveButton("OK", null)
                .show();
    }

    private static String getErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                ErrorResponse errorResponse = new Gson().fromJson(errorJson, ErrorResponse.class);
                return errorResponse.getMessage();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
