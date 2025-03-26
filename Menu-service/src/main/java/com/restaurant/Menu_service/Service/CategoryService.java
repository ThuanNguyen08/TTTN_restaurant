package com.restaurant.Menu_service.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.restaurant.Menu_service.DTO.CategoryRequest;
import com.restaurant.Menu_service.DTO.CategoryResponse;
import com.restaurant.Menu_service.Entity.Category;
import com.restaurant.Menu_service.Exception.ResourceAlreadyExistsException;
import com.restaurant.Menu_service.Exception.ResourceNotFoundException;
import com.restaurant.Menu_service.Repository.CategoryRepository;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public List<CategoryResponse> getAllCategories() {
		return categoryRepository.findByIsActiveTrue().stream().map(CategoryResponse::fromEntity).toList();
	}

	public CategoryResponse getCategoryById(Long id) {
		return categoryRepository.findById(id).filter(Category::getIsActive).map(CategoryResponse::fromEntity)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + id));
	}

	public CategoryResponse createCategory(CategoryRequest request) {
		if (categoryRepository.existsByName(request.getName())) {
			throw new ResourceAlreadyExistsException("Danh mục với tên \"" + request.getName() + "\" đã tồn tại");
		}

		Category category = new Category();
		category.setName(request.getName());
		category.setIsActive(true);

		Category savedCategory = categoryRepository.save(category);
		return CategoryResponse.fromEntity(savedCategory);
	}

	public CategoryResponse updateCategory(Long id, CategoryRequest request) {
		Category category = categoryRepository.findById(id).filter(Category::getIsActive)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + id));

		Optional<Category> existingCategory = categoryRepository.findByNameAndIsActiveTrue(request.getName());
		if (existingCategory.isPresent() && !existingCategory.get().getId().equals(id)) {
			throw new ResourceAlreadyExistsException("Danh mục với tên \"" + request.getName() + "\" đã tồn tại");
		}

		category.setName(request.getName());
		Category updatedCategory = categoryRepository.save(category);
		return CategoryResponse.fromEntity(updatedCategory);
	}

	public void deleteCategory(Long id) {
		Category category = categoryRepository.findById(id).filter(Category::getIsActive)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + id));

		category.setIsActive(false);
		categoryRepository.save(category);
	}
	
	public CategoryResponse restoreCategory(Long id) {
	    Category category = categoryRepository.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + id));

	    // Chỉ cho phép khôi phục các danh mục đã bị vô hiệu hóa
	    if (category.getIsActive()) {
	        throw new ResourceAlreadyExistsException("Danh mục này đã được kích hoạt");
	    }

	    category.setIsActive(true);
	    Category restoredCategory = categoryRepository.save(category);
	    return CategoryResponse.fromEntity(restoredCategory);
	}
}