package com.example.tttn_restaurant.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.TokenRefreshResponse;
import com.example.tttn_restaurant.model.UserResponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionManager {
    private static final String PREF_NAME = "RestaurantManagerSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_USER = "user";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String TAG = "SessionManager";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public interface TokenRefreshCallback {
        void onTokenRefreshed(boolean success);
    }

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void saveRefreshToken(String refreshToken) {
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public String getAuthToken() {
        return "Bearer " + getToken();
    }

    public void saveUser(UserResponse user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        editor.apply();
    }

    public UserResponse getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, UserResponse.class);
        }
        return null;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        boolean hasLoginFlag = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        boolean hasToken = getToken() != null && !getToken().isEmpty();
        boolean hasUser = getUser() != null;

        // Kiểm tra basic và thời hạn token
        if (hasLoginFlag && hasToken && hasUser) {
            return true;
        }
        clearSession();
        return false;
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public void refreshToken(final TokenRefreshCallback callback) {
        String refreshToken = getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            Log.e(TAG, "Không có refresh token");
            if (callback != null) {
                callback.onTokenRefreshed(false);
            }
            return;
        }

        Map<String, String> refreshTokenRequest = new HashMap<>();
        refreshTokenRequest.put("refreshToken", refreshToken);

        RetrofitClient.getUserApiService().refreshToken(refreshTokenRequest).enqueue(new Callback<TokenRefreshResponse>() {
            @Override
            public void onResponse(Call<TokenRefreshResponse> call, Response<TokenRefreshResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TokenRefreshResponse tokenRefreshResponse = response.body();
                    saveToken(tokenRefreshResponse.getAccessToken());
                    saveRefreshToken(tokenRefreshResponse.getRefreshToken());

                    Log.d(TAG, "Token refresh successful");
                    if (callback != null) {
                        callback.onTokenRefreshed(true);
                    }
                } else {
                    Log.e(TAG, "Token refresh failed: " + response.code());
                    clearSession(); // Clear session on failed refresh
                    if (callback != null) {
                        callback.onTokenRefreshed(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenRefreshResponse> call, Throwable t) {
                Log.e(TAG, "Token refresh network error", t);
                if (callback != null) {
                    callback.onTokenRefreshed(false);
                }
            }
        });
    }
}