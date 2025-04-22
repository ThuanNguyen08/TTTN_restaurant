package com.example.tttn_restaurant.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.tttn_restaurant.R;
import com.example.tttn_restaurant.api.RetrofitClient;
import com.example.tttn_restaurant.model.CategoryResponse;
import com.example.tttn_restaurant.model.FoodResponse;
import com.example.tttn_restaurant.utils.ErrorHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFoodFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private ImageView imgFoodPreview;
    private EditText edtName, edtPrice, edtDescription;
    private Spinner spnCategory;
    private Button btnTakePhoto, btnChooseImage, btnSave;
    private ProgressBar progressBar;

    private List<CategoryResponse> categoryList = new ArrayList<>();
    private File currentPhotoFile;
    private String currentPhotoPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_food, container, false);

        // Khởi tạo views
        imgFoodPreview = view.findViewById(R.id.imgFoodPreview);
        edtName = view.findViewById(R.id.edtName);
        edtPrice = view.findViewById(R.id.edtPrice);
        edtDescription = view.findViewById(R.id.edtDescription);
        spnCategory = view.findViewById(R.id.spnCategory);
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        btnChooseImage = view.findViewById(R.id.btnChooseImage);
        btnSave = view.findViewById(R.id.btnSave);
        progressBar = view.findViewById(R.id.progressBar);

        // Thiết lập sự kiện click cho các nút
        btnTakePhoto.setOnClickListener(v -> dispatchTakePictureIntent());
        btnChooseImage.setOnClickListener(v -> dispatchPickImageIntent());
        btnSave.setOnClickListener(v -> saveFood());

        // Tải danh sách danh mục
        loadCategories();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hiển thị nút back trong ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Đặt tiêu đề cho ActionBar
        activity.getSupportActionBar().setTitle("Thêm món ăn mới");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getMenuApiService().getAllCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());

                    // Chỉ hiển thị các danh mục đang hoạt động
                    List<CategoryResponse> activeCategories = new ArrayList<>();
                    for (CategoryResponse category : categoryList) {
                        if (category.isActive()) {
                            activeCategories.add(category);
                        }
                    }

                    // Thiết lập adapter cho Spinner
                    ArrayAdapter<CategoryResponse> categoryAdapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            activeCategories);
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCategory.setAdapter(categoryAdapter);
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Toast.makeText(requireContext(), "Chưa phát triển chức năng này", Toast.LENGTH_SHORT).show();
    }

    private void dispatchPickImageIntent() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    private File createImageFile() throws IOException {
        // Tạo tên file dựa vào timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Lưu đường dẫn file
        currentPhotoPath = image.getAbsolutePath();
        currentPhotoFile = image;
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Hiển thị ảnh đã chụp
                setPic();
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImage);

                    // Tạo file ảnh
                    currentPhotoFile = createImageFile();

                    // Ghi bitmap vào file
                    FileOutputStream out = new FileOutputStream(currentPhotoFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                    imgFoodPreview.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Lỗi xử lý ảnh", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void setPic() {
        // Đọc kích thước ảnh để tránh OutOfMemoryError
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        // Tính toán tỷ lệ nén
        int scaleFactor = 1;
        if (bmOptions.outHeight > 700 || bmOptions.outWidth > 700) {
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            scaleFactor = Math.min(photoW / 700, photoH / 700);
        }

        // Đọc file với kích thước đã tính toán
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imgFoodPreview.setImageBitmap(bitmap);
    }

    private void saveFood() {
        // Kiểm tra dữ liệu đầu vào
        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Vui lòng nhập tên món ăn");
            edtName.requestFocus();
            return;
        }

        if (priceStr.isEmpty()) {
            edtPrice.setError("Vui lòng nhập giá tiền");
            edtPrice.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                edtPrice.setError("Giá tiền phải lớn hơn 0");
                edtPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtPrice.setError("Giá tiền không hợp lệ");
            edtPrice.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            edtDescription.setError("Vui lòng nhập mô tả");
            edtDescription.requestFocus();
            return;
        }

        if (currentPhotoFile == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn ảnh cho món ăn", Toast.LENGTH_SHORT).show();
            return;
        }

        CategoryResponse selectedCategory = (CategoryResponse) spnCategory.getSelectedItem();
        if (selectedCategory == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // Tạo MultipartBody.Part từ file ảnh
        RequestBody requestFile = RequestBody.create(currentPhotoFile ,MediaType.parse("image/*"));
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", currentPhotoFile.getName(), requestFile);

        // Tạo các RequestBody cho các trường dữ liệu khác
        RequestBody nameBody = RequestBody.create(name, MediaType.parse("text/plain"));
        RequestBody priceBody = RequestBody.create(String.valueOf(price), MediaType.parse("text/plain"));
        RequestBody descriptionBody = RequestBody.create(description, MediaType.parse("text/plain"));
        RequestBody categoryIdBody = RequestBody.create(String.valueOf(selectedCategory.getId()), MediaType.parse("text/plain"));

        // Gọi API để tạo món ăn mới
        RetrofitClient.getMenuApiService().createFood(nameBody, descriptionBody, priceBody, categoryIdBody, imagePart)
                .enqueue(new Callback<FoodResponse>() {
                    @Override
                    public void onResponse(Call<FoodResponse> call, Response<FoodResponse> response) {
                        if (!isAdded()) return;

                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(requireContext(), "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed(); // Quay lại màn hình danh sách
                        } else {
                            ErrorHandler.handleErrorResponse(requireContext(), response);
                        }
                    }

                    @Override
                    public void onFailure(Call<FoodResponse> call, Throwable t) {
                        if (!isAdded()) return;

                        progressBar.setVisibility(View.GONE);
                        ErrorHandler.handleFailure(requireContext(), t);
                    }
                });
    }
}