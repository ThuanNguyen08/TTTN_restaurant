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

public class EditFoodFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private ImageView imgFoodPreview;
    private EditText edtName, edtPrice, edtDescription;
    private Spinner spnCategory;
    private Button btnTakePhoto, btnChooseImage, btnSave;
    private ProgressBar progressBar;

    private Long foodId;
    private FoodResponse currentFood;
    private List<CategoryResponse> categoryList = new ArrayList<>();
    private File currentPhotoFile;
    private String currentPhotoPath;
    private boolean imageChanged = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Lấy foodId từ Bundle arguments
        Bundle args = getArguments();
        if (args != null) {
            foodId = args.getLong("foodId", -1);
        }
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
        btnSave.setOnClickListener(v -> updateFood());

        // Tải danh sách danh mục
        loadCategories();

        // Tải thông tin món ăn hiện tại
        if (foodId != -1) {
            loadFoodDetails();
        } else {
            Toast.makeText(requireContext(), "Không tìm thấy món ăn", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }

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
        activity.getSupportActionBar().setTitle("Chỉnh sửa món ăn");
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

                    // Nếu đã tải thông tin món ăn, chọn danh mục tương ứng
                    if (currentFood != null) {
                        selectCurrentCategory();
                    }
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }

                // Ẩn ProgressBar nếu đã tải xong cả food và category
                if (currentFood != null) {
                    progressBar.setVisibility(View.GONE);
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

    private void loadFoodDetails() {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getMenuApiService().getFoodById(foodId).enqueue(new Callback<FoodResponse>() {
            @Override
            public void onResponse(Call<FoodResponse> call, Response<FoodResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    currentFood = response.body();
                    updateUI();

                    // Chọn danh mục tương ứng nếu đã tải danh sách danh mục
                    if (spnCategory.getAdapter() != null && spnCategory.getAdapter().getCount() > 0) {
                        selectCurrentCategory();
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
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

    private void updateUI() {
        if (currentFood != null) {
            edtName.setText(currentFood.getName());
            edtPrice.setText(String.valueOf(currentFood.getPrice()));
            edtDescription.setText(currentFood.getDescription());

            // Hiển thị ảnh từ Base64
            if (currentFood.getImageBitmap() != null) {
                imgFoodPreview.setImageBitmap(currentFood.getImageBitmap());
            }
        }
    }

    private void selectCurrentCategory() {
        if (currentFood != null && currentFood.getCategory() != null) {
            ArrayAdapter<CategoryResponse> adapter = (ArrayAdapter<CategoryResponse>) spnCategory.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                CategoryResponse category = adapter.getItem(i);
                if (category != null && category.getId().equals(currentFood.getCategory().getId())) {
                    spnCategory.setSelection(i);
                    break;
                }
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Toast.makeText(requireContext(), "Không tìm thấy ứng dụng camera", Toast.LENGTH_SHORT).show();
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
        imageChanged = true;
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Hiển thị ảnh đã chụp
                setPic();
                imageChanged = true;
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
                    imageChanged = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Lỗi xử lý ảnh", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void updateFood() {
        // Kiểm tra dữ liệu đầu vào
        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        // Kiểm tra các trường bắt buộc
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

        if (description.isEmpty()) {
            edtDescription.setError("Vui lòng nhập mô tả");
            edtDescription.requestFocus();
            return;
        }

        // Kiểm tra danh mục
        if (spnCategory.getSelectedItem() == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // Tạo các RequestBody
        RequestBody nameBody = RequestBody.create(name, MediaType.parse("text/plain"));
        RequestBody descriptionBody = RequestBody.create(description, MediaType.parse("text/plain"));
        RequestBody priceBody = RequestBody.create(priceStr, MediaType.parse("text/plain"));

        // Lấy ID của danh mục được chọn
        CategoryResponse selectedCategory = (CategoryResponse) spnCategory.getSelectedItem();
        RequestBody categoryIdBody = RequestBody.create(selectedCategory.getId().toString(), MediaType.parse("text/plain"));

        // Tạo phần cho ảnh
        MultipartBody.Part imagePart = null;
        if (imageChanged && currentPhotoFile != null) {
            RequestBody requestFile = RequestBody.create(currentPhotoFile, MediaType.parse("image/*"));
            imagePart = MultipartBody.Part.createFormData("image", currentPhotoFile.getName(), requestFile);
        }

        // Gọi API để cập nhật món ăn
        RetrofitClient.getMenuApiService().updateFood(
                foodId,
                nameBody,
                descriptionBody,
                priceBody,
                categoryIdBody,
                imagePart
        ).enqueue(new Callback<FoodResponse>() {
            @Override
            public void onResponse(Call<FoodResponse> call, Response<FoodResponse> response) {
                // Kiểm tra Fragment còn tồn tại không
                if (!isAdded()) return;

                // Ẩn ProgressBar
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Cập nhật món ăn thành công", Toast.LENGTH_SHORT).show();

                    // Quay lại màn hình chi tiết
                    requireActivity().onBackPressed();
                } else {
                    ErrorHandler.handleErrorResponse(requireContext(), response);
                }
            }

            @Override
            public void onFailure(Call<FoodResponse> call, Throwable t) {
                // Kiểm tra Fragment còn tồn tại không
                if (!isAdded()) return;

                // Ẩn ProgressBar
                progressBar.setVisibility(View.GONE);

                ErrorHandler.handleFailure(requireContext(), t);
            }
        });
    }

    private void setPic() {
        // Lấy kích thước của ImageView
        int targetW = imgFoodPreview.getWidth();
        int targetH = imgFoodPreview.getHeight();

        // Lấy kích thước của hình ảnh
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Tính toán tỷ lệ thu nhỏ
        int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

        // Đọc hình ảnh với tỷ lệ thu nhỏ
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imgFoodPreview.setImageBitmap(bitmap);
    }
}