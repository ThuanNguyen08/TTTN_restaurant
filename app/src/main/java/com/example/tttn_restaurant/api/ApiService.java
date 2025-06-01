package com.example.tttn_restaurant.api;

import com.example.tttn_restaurant.model.Bill;
import com.example.tttn_restaurant.model.CategoryRequest;
import com.example.tttn_restaurant.model.CategoryResponse;
import com.example.tttn_restaurant.model.DetailBill;
import com.example.tttn_restaurant.model.FoodResponse;
import com.example.tttn_restaurant.model.ForgotPasswordRequest;
import com.example.tttn_restaurant.model.LoginResponse;
import com.example.tttn_restaurant.model.PageResponse;
import com.example.tttn_restaurant.model.PayBillRequest;
import com.example.tttn_restaurant.model.ResetPasswordRequest;
import com.example.tttn_restaurant.model.RevenueReportDTO;
import com.example.tttn_restaurant.model.TableBillRequest;
import com.example.tttn_restaurant.model.TableRequest;
import com.example.tttn_restaurant.model.TableResponse;
import com.example.tttn_restaurant.model.TokenRefreshResponse;
import com.example.tttn_restaurant.model.UserLoginRequest;
import com.example.tttn_restaurant.model.UserRegistrationRequest;
import com.example.tttn_restaurant.model.UserResponse;
import com.example.tttn_restaurant.model.UserUpdateRequest;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    // Quên mật khẩu (yêu cầu mã xác thực)
    @POST("/api/password/forgot")
    Call<Map<String, String>> forgotPassword(@Body ForgotPasswordRequest request);

    // Đặt lại mật khẩu
    @POST("/api/password/reset")
    Call<Map<String, String>> resetPassword(@Body ResetPasswordRequest request);

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

    // Category API-----------------------------------------------------------------------------------------
    //Lấy danh mục món ăn
    @GET("/api/categories")
    Call<List<CategoryResponse>> getAllCategories();

    //Lấy danh mục món ăn theo id
    @GET("/api/categories/{id}")
    Call<CategoryResponse> getCategoryById(@Path("id") Long id);

    @GET("/api/categories/disable")
    Call<List<CategoryResponse>> getDisableCategories();

    //Thêm danh mục món ăn
    @POST("/api/categories")
    Call<CategoryResponse> createCategory(@Body CategoryRequest request);

    //Cập nhật danh mục món ăn theo id
    @PUT("/api/categories/{id}")
    Call<CategoryResponse> updateCategory(@Path("id") Long id, @Body CategoryRequest request);

    //Cập nhật trạng thái hoạt động của món ăn
    @PUT("/api/categories/{id}/disable")
    Call<Void> disableCategory(@Path("id") Long id);

    //
    @PUT("/api/categories/{id}/restore")
    Call<CategoryResponse> restoreCategory(@Path("id") Long id);

    //Xóa danh mục món ăn
    @DELETE("/api/categories/{id}")
    Call<Void> deleteCategory(@Path("id") Long id);

    // Food API--------------------------------------------------------------------------------
    @GET("/api/foods")
    Call<List<FoodResponse>> getAllFoods();

    //Lấy món ăn theo trạng thái
    @GET("/api/foods/status")
    Call<List<FoodResponse>> getFoodsByStatus(@Query("status") String status);

    //Lấy món ăn theo danh mục món ăn
    @GET("/api/foods/category/{categoryId}")
    Call<List<FoodResponse>> getFoodsByCategory(@Path("categoryId") Long categoryId);

    //Lấy món ăn theo id
    @GET("/api/foods/{id}")
    Call<FoodResponse> getFoodById(@Path("id") Long id);

    @GET("/api/foods/disable")
    Call<List<FoodResponse>> getDisableFoods();

    //Thêm món ăn
    @Multipart
    @POST("/api/foods")
    Call<FoodResponse> createFood(
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part("categoryId") RequestBody categoryId,
            @Part MultipartBody.Part image
    );

    //Cập nhật món ăn
    @Multipart
    @PUT("/api/foods/{id}")
    Call<FoodResponse> updateFood(
            @Path("id") Long id,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part("categoryId") RequestBody categoryId,
            @Part MultipartBody.Part image
    );

    //Vô hiệu hóa món ăn
    @PUT("/api/foods/{id}/disable")
    Call<Void> disableFood(@Path("id") Long id);

    //Thay đổi trạng thái của món ăn(có sẵn/ tạm hết)
    @PUT("/api/foods/{id}/status")
    Call<FoodResponse> updateFoodStatus(@Path("id") Long id, @Query("status") String status);

    //Khôi phục món ăn đã bị vô hiệu hóa
    @PUT("/api/foods/{id}/restore")
    Call<FoodResponse> restoreFood(@Path("id") Long id);

    //Xóa món ăn
    @DELETE("/api/foods/{id}")
    Call<Void> deleteFood(@Path("id") Long id);

    // Table API------------------------------------------------------------------------------------
    @GET("/api/tables")
    Call<List<TableResponse>> getAllTables();

    @GET("/api/tables/{id}")
    Call<TableResponse> getTableById(@Path("id") Long id);

    @GET("/api/tables/status")
    Call<List<TableResponse>> getTablesByStatus(@Query("status") String status);

    @POST("/api/tables")
    Call<TableResponse> createTable(@Body TableRequest request);

    @PUT("/api/tables/{id}")
    Call<TableResponse> updateTable(@Path("id") Long id, @Body TableRequest request);

    @PUT("/api/tables/{id}/status")
    Call<TableResponse> updateTableStatus(@Path("id") Long id, @Query("status") String status);

    @DELETE("/api/tables/{id}")
    Call<Void> deleteTable(@Path("id") Long id);

    // Bill api-------------------------------------------------------------------------------------
    @POST("/api/bills")
    Call<Bill> createOrUpdateBill(@Body TableBillRequest request);

    @GET("/api/bills/{id}")
    Call<Bill> getBillById(@Path("id") Long id);

    @GET("/api/bills/{id}/items")
    Call<List<DetailBill>> getBillItems(@Path("id") Long id);

    @GET("/api/bills/pending")
    Call<List<Bill>> getPendingBills();

    @GET("/api/bills")
    Call<PageResponse<Bill>> getAllBills(@Query("page") int page,
                                         @Query("size") int size,
                                         @Query("sort") String sort,
                                         @Query("status") String status,
                                         @Query("date") String date);

    // Payment endpoints
    @POST("/api/payments/pay")
    Call<Bill> payBill(@Body PayBillRequest request);

    // Reports endpoints
    @GET("/api/reports/revenue")
    Call<RevenueReportDTO> getRevenueReport(
            @Query("type") RevenueReportDTO.DateRangeType type,
            @Query("start") String startDate,
            @Query("end") String endDate);
}