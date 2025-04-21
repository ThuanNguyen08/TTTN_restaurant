package com.restaurant.User_service.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.User_service.DTO.UserRegistrationRequest;
import com.restaurant.User_service.DTO.UserResponse;
import com.restaurant.User_service.DTO.UserUpdateRequest;
import com.restaurant.User_service.Entity.User;
import com.restaurant.User_service.Exeption.ResourceNotFoundException;
import com.restaurant.User_service.Exeption.UnauthorizedAccessException;
import com.restaurant.User_service.Exeption.UserAlreadyExistsException;
import com.restaurant.User_service.Repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public User findByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User nào với username: " + username));
	}

	@Transactional
	public UserResponse register(UserRegistrationRequest request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new UserAlreadyExistsException("Username đã tồn tại");
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new UserAlreadyExistsException("Email đã tồn tại");
		}

		if (request.getPhone() != null && !request.getPhone().isEmpty()
				&& userRepository.existsByPhone(request.getPhone())) {
			throw new UserAlreadyExistsException("Số điện thoại đã tồn tại");
		}

		User.Role role = request.getRole();
		if (role != null) {
			// Kiểm tra xem role có thuộc một trong bốn vai trò không
			boolean isValidRole = false;
			for (User.Role validRole : User.Role.values()) {
				if (role == validRole) {
					isValidRole = true;
					break;
				}
			}

			if (!isValidRole) {
				throw new IllegalArgumentException(
						"Vai trò không hợp lệ. Vui lòng chọn một trong các vai trò: STAFF, MANAGER, ADMIN, DISABLE");
			}
		} else {
			// Nếu không nhập role thì gán STAFF
			role = User.Role.STAFF;
		}

		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setFullName(request.getFullName());
		user.setRole(role);
		user.setIsActive(true);
		user.setCreatedAt(LocalDateTime.now());

		User savedUser = userRepository.save(user);

		return UserResponse.fromEntity(savedUser);
	}

	@Transactional
	public void updateLastLogin(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id: " + userId));

		user.setLastLogin(LocalDateTime.now());
		userRepository.save(user);
	}

	public UserResponse getUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id: " + id));

		return UserResponse.fromEntity(user);
	}

	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream().map(UserResponse::fromEntity).collect(Collectors.toList());
	}

	@Transactional
	public UserResponse updateUser(Long id, UserUpdateRequest request, Long currentUserId) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id: " + id));

		User currentUser = userRepository.findById(currentUserId)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng hiện tại"));

		if (!currentUser.getId().equals(id) && !currentUser.getRole().equals(User.Role.MANAGER)
				&& !currentUser.getRole().equals(User.Role.ADMIN)) {
			throw new UnauthorizedAccessException("bạn không có quyền cập nhật người dùng này");
		}

		if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
			if (userRepository.existsByEmail(request.getEmail())) {
				throw new UserAlreadyExistsException("Email này đã được đăng kí");
			}
			user.setEmail(request.getEmail());
		}

		if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
			if (userRepository.existsByPhone(request.getPhone())) {
				throw new UserAlreadyExistsException("Số điện thoại này đã được đăng ký");
			}
			user.setPhone(request.getPhone());
		}

		if (request.getFullName() != null) {
			user.setFullName(request.getFullName());
		}

		if (request.getOldPassword() != null && request.getNewPassword() != null) {
			if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
				throw new IllegalArgumentException("Mật khẩu cũ không đúng");
			}
			 // Kiểm tra mật khẩu mới không được giống mật khẩu cũ
	        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
	            throw new IllegalArgumentException("Mật khẩu mới không được giống mật khẩu cũ");
	        }

			user.setPassword(passwordEncoder.encode(request.getNewPassword()));
			
		} else if ((request.getOldPassword() != null && request.getNewPassword() == null) ||
	               (request.getOldPassword() == null && request.getNewPassword() != null)) {
	        throw new IllegalArgumentException("Vui lòng cung cấp cả mật khẩu cũ và mật khẩu mới");
	    }

		User updatedUser = userRepository.save(user);

		return UserResponse.fromEntity(updatedUser);
	}

	@Transactional
	public UserResponse updateUserStatus(Long id, boolean isActive) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id: " + id));

		user.setIsActive(isActive);
		User updatedUser = userRepository.save(user);

		return UserResponse.fromEntity(updatedUser);
	}

	@Transactional
	public void deleteUser(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("Không tìm thấy user với id: " + id);
		}

		userRepository.deleteById(id);
	}
}
