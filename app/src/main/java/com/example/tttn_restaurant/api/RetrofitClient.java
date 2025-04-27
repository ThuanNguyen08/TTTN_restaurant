package com.example.tttn_restaurant.api;

import android.content.Context;

import com.example.tttn_restaurant.interceptors.AuthInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String USER_SERVICE_URL = "http://192.168.1.9:8081"; // Use 10.0.2.2 for localhost in emulator
    private static final String MENU_SERVICE_URL = "http://192.168.1.9:8082";
    private static final String TABLE_SERVICE_URL = "http://192.168.1.9:8083";
    private static final String REVENUE_SERVICE_URL = "http://192.168.1.9:8084";

    private static Retrofit userServiceRetrofit  = null;
    private static Retrofit menuServiceRetrofit = null;
    private static Retrofit tableServiceRetrofit = null;
    private static Retrofit revenueServiceRetrofit = null;
    private static Context appContext = null;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    private static OkHttpClient getHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        AuthInterceptor authInterceptor = new AuthInterceptor(appContext);

        return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build();
    }

    public static Retrofit getUserServiceClient() {
        if (userServiceRetrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            userServiceRetrofit = new Retrofit.Builder()
                    .baseUrl(USER_SERVICE_URL)
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return userServiceRetrofit;
    }

    public static Retrofit getMenuServiceClient() {
        if (menuServiceRetrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            menuServiceRetrofit = new Retrofit.Builder()
                    .baseUrl(MENU_SERVICE_URL)
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return menuServiceRetrofit;
    }

    public static Retrofit getTableServiceClient() {
        if (tableServiceRetrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            tableServiceRetrofit = new Retrofit.Builder()
                    .baseUrl(TABLE_SERVICE_URL)
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return tableServiceRetrofit;
    }

    public static Retrofit getRevenueServiceClient() {

        if (revenueServiceRetrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            revenueServiceRetrofit = new Retrofit.Builder()
                    .baseUrl(REVENUE_SERVICE_URL)
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return revenueServiceRetrofit;
    }

    public static ApiService getUserApiService() {
        return getUserServiceClient().create(ApiService.class);
    }

    public static ApiService getMenuApiService() {
        return getMenuServiceClient().create(ApiService.class);
    }
    public static ApiService getTableApiService() {
        return getTableServiceClient().create(ApiService.class);
    }

    public static ApiService getRevenueApiService() {
        return getRevenueServiceClient().create(ApiService.class);
    }
}