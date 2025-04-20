package com.example.tttn_restaurant.api;

import com.example.tttn_restaurant.model.LoginResponse;
import com.example.tttn_restaurant.model.TokenRefreshResponse;
import com.example.tttn_restaurant.model.UserLoginRequest;
import com.example.tttn_restaurant.model.UserRegistrationRequest;
import com.example.tttn_restaurant.model.UserResponse;
import com.example.tttn_restaurant.model.UserUpdateRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    // Đăng nhập
    @POST("/api/users/login")
    Call<LoginResponse> login(@Body UserLoginRequest loginRequest);

    // Thêm vào ApiService
    @POST("/api/users/refresh-token")
    Call<TokenRefreshResponse> refreshToken(@Body Map<String, String> refreshTokenRequest);

    // Đăng ký người dùng (chỉ ADMIN và MANAGER)
    @POST("/api/users/register")
    Call<UserResponse> register(@Body UserRegistrationRequest request);

    // Lấy danh sách người dùng
    @GET("/api/users")
    Call<List<UserResponse>> getAllUsers();

    // Lấy thông tin người dùng theo ID
    @GET("/api/users/{id}")
    Call<UserResponse> getUserById(@Path("id") Long id);

    // Lấy thông tin người dùng hiện tại
    @GET("/api/users/me")
    Call<UserResponse> getCurrentUser();

    // Cập nhật thông tin người dùng
    @PUT("/api/users/{id}")
    Call<UserResponse> updateUser(@Path("id") Long id,
                                  @Body UserUpdateRequest request);

    // Cập nhật trạng thái người dùng (kích hoạt/vô hiệu hóa)
    @PUT("/api/users/{id}/status")
    Call<UserResponse> updateUserStatus(@Path("id") Long id,
                                        @Body Map<String, Boolean> statusMap);

    // Xóa người dùng
    @DELETE("/api/users/{id}")
    Call<Map<String, String>> deleteUser(@Path("id") Long id);
}