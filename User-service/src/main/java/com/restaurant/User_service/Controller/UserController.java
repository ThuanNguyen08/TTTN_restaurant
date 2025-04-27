package com.restaurant.User_service.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.restaurant.User_service.DTO.UserRegistrationRequest;
import com.restaurant.User_service.DTO.UserResponse;
import com.restaurant.User_service.DTO.UserUpdateRequest;
import com.restaurant.User_service.Entity.User;
import com.restaurant.User_service.Service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "Quản lý thông tin của người dùng")
public class UserController {

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@Operation(summary = "Đăng ký người dùng mới", description = "Tạo một tài khoản người dùng mới trong hệ thống")
	@PostMapping("/register")
	public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
		UserResponse response = userService.register(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Operation(summary = "Lấy thông tin tất cả người dùng", description = "Có thể xem thông tin tất cả người dùng có trong hệ thống")
	@GetMapping
	public ResponseEntity<List<UserResponse>> getAllUsers() {
		List<UserResponse> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}

	@Operation(summary = "Lấy thông tin người dùng bằng id", description = "Có thể xem thông tin người dùng có trong hệ thống bằng id")
	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long id, HttpServletRequest request) {

		Long currentUserId = (Long) request.getAttribute("userId");

		// If it's not the current user and not a manager/admin, reject
		UserResponse currentUser = userService.getUserById(currentUserId);
		if (!currentUserId.equals(id) && !currentUser.getRole().equals(User.Role.MANAGER)
				&& !currentUser.getRole().equals(User.Role.ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		UserResponse user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}

	
	@Operation(summary = "Lấy thông tin của chính mình", description = "Có thể xem thông tin của chính mình(lấy thông tin từ token)")
	@GetMapping("/me")
	public ResponseEntity<UserResponse> getCurrentUser(HttpServletRequest request) {
		Long userId = (Long) request.getAttribute("userId");
		UserResponse user = userService.getUserById(userId);
		return ResponseEntity.ok(user);
	}

	@Operation(summary = "Cập nhật thông tin người dùng bằng id", description = "Có thể cập nhật thông tin người dùng có trong hệ thống bằng id")
	@PutMapping("/{id}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request,
			HttpServletRequest servletRequest) {

		Long currentUserId = (Long) servletRequest.getAttribute("userId");
		UserResponse updatedUser = userService.updateUser(id, request, currentUserId);
		return ResponseEntity.ok(updatedUser);
	}

	@Operation(summary = "Cập nhật trạng thái người dùng", description = "Có thể cập nhật trạng thái(hoạt động/không hoạt động) người dùng có trong hệ thống bằng id")
	@PutMapping("/{id}/status")
	public ResponseEntity<UserResponse> updateUserStatus(@PathVariable Long id,
			@RequestBody Map<String, Boolean> statusMap) {

		Boolean isActive = statusMap.get("isActive");
		if (isActive == null) {
			return ResponseEntity.badRequest().build();
		}

		UserResponse updatedUser = userService.updateUserStatus(id, isActive);
		return ResponseEntity.ok(updatedUser);
	}

	@Operation(summary = "Xóa người dùng bằng id", description = "Có thể xóa thông tin người dùng có trong hệ thống bằng id")
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);

		Map<String, String> response = new HashMap<>();
		response.put("status", "Xóa thành công");
		return ResponseEntity.ok(response);
	}
}
