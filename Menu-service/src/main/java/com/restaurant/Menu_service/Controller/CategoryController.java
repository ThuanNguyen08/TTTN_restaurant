package com.restaurant.Menu_service.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.Menu_service.DTO.CategoryRequest;
import com.restaurant.Menu_service.DTO.CategoryResponse;
import com.restaurant.Menu_service.Service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category", description = "Quản lý thông tin danh mục món của nhà hàng")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    
    @Operation(summary = "Lấy tất cả danh mục", description = "Có thể xem thông tin tất cả danh mục đang hoạt động trong hệ thống")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @Operation(summary = "Lấy danh mục bằng id", description = "Có thể xem thông tin chi tiết của danh mục bằng id")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }
    
    @Operation(summary = "Lấy danh sách danh mục đã vô hiệu hóa", description = "Có thể xem danh sách các danh mục đã bị vô hiệu hóa trong hệ thống")
    @GetMapping("/disable")
    public ResponseEntity<List<CategoryResponse>> getDisabledCategories(){
    	List<CategoryResponse> categories = categoryService.getCategoriesByIsActiveFalse();
    	return ResponseEntity.ok(categories);
    }
    
    @Operation(summary = "Tạo danh mục mới", description = "Có thể tạo một danh mục mới trong hệ thống")
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse createdCategory = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }
    
    @Operation(summary = "Cập nhật thông tin danh mục", description = "Có thể cập nhật thông tin của danh mục bằng id")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        CategoryResponse updatedCategory = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(updatedCategory);
    }
    
    @Operation(summary = "Vô hiệu hóa danh mục", description = "Có thể vô hiệu hóa một danh mục trong hệ thống bằng id")
    @PutMapping("/{id}/disable")
    public ResponseEntity<Void> disableCategory(@PathVariable Long id) {
        categoryService.disableCategory(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Khôi phục danh mục", description = "Có thể khôi phục một danh mục đã bị vô hiệu hóa trong hệ thống")
    @PutMapping("/{id}/restore")
    public ResponseEntity<CategoryResponse> restoreCategory(@PathVariable Long id) {
        CategoryResponse restoredCategory = categoryService.restoreCategory(id);
        return ResponseEntity.ok(restoredCategory);
    }
    
    @Operation(summary = "Xóa danh mục", description = "Có thể xóa hoàn toàn một danh mục khỏi hệ thống (chỉ khi không có món ăn thuộc danh mục)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}