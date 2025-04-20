package com.example.tttn_restaurant;

import android.app.Application;

import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.utils.SessionManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitClient.init(this);
    }
}