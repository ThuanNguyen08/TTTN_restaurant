package com.restaurant.User_service.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.User_service.DTO.ForgotPasswordRequest;
import com.restaurant.User_service.DTO.ResetPasswordRequest;
import com.restaurant.User_service.Entity.User;
import com.restaurant.User_service.Entity.VerificationCode;
import com.restaurant.User_service.Exeption.ResourceNotFoundException;
import com.restaurant.User_service.Repository.UserRepository;
import com.restaurant.User_service.Repository.VerificationCodeRepository;

@Service
public class PasswordResetService {

	private final UserRepository userRepository;
	private final VerificationCodeRepository verificationCodeRepository;
	private final EmailService emailService;
	private final BCryptPasswordEncoder passwordEncoder;
	private final SecureRandom random = new SecureRandom();

	public PasswordResetService(UserRepository userRepository, VerificationCodeRepository verificationCodeRepository,
			EmailService emailService, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.verificationCodeRepository = verificationCodeRepository;
		this.emailService = emailService;
		this.passwordEncoder = passwordEncoder;
	}

	// Phương thức xử lý yêu cầu quên mật khẩu
	@Transactional
	public void processForgotPassword(ForgotPasswordRequest request) {
		String email = request.getEmail();

		// Kiểm tra email có tồn tại trong hệ thống không
		if (!userRepository.existsByEmail(email)) {
			throw new ResourceNotFoundException("Không tìm thấy người dùng với email: " + email);
		}

		// Kiểm tra xem email đã có mã xác thực còn hiệu lực chưa
		Optional<VerificationCode> existingCode = verificationCodeRepository.findTopByEmailOrderByExpiryDateDesc(email);

		if (existingCode.isPresent()) {
			VerificationCode code = existingCode.get();
			// Nếu mã chưa hết hạn và chưa được sử dụng thì thông báo
			if (!code.isExpired() && !code.isUsed()) {
				throw new IllegalStateException(
						"Mã xác thực đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư hoặc thử lại sau "
								+ calculateRemainingMinutes(code.getExpiryDate()) + " phút.");
			}
		}

		// Nếu không có mã hiệu lực hoặc mã đã hết hạn/đã dùng, tạo mã mới
		// Sinh mã xác thực ngẫu nhiên 6 chữ số
		String verificationCode = generateVerificationCode();

		// Lưu mã xác thực vào database
		VerificationCode verificode = new VerificationCode(email, verificationCode);
		verificationCodeRepository.save(verificode);

		// Gửi mã xác thực qua email
		emailService.sendVerificationEmail(email, verificationCode);
	}

	private long calculateRemainingMinutes(LocalDateTime expiryDate) {
		LocalDateTime now = LocalDateTime.now();
		long minutesRemaining = java.time.Duration.between(now, expiryDate).toMinutes();
		return Math.max(0, minutesRemaining);
	}

	// Phương thức xử lý đặt lại mật khẩu
	@Transactional
	public void resetPassword(ResetPasswordRequest request) {
		String email = request.getEmail();
		String verificationCode = request.getVerificationCode();
		String newPassword = request.getNewPassword();

		// Tìm kiếm code xác thực
		Optional<VerificationCode> tokenOpt = verificationCodeRepository.findByEmailAndVerificationCode(email,
				verificationCode);

		if (tokenOpt.isEmpty()) {
			throw new IllegalArgumentException("Mã xác thực không hợp lệ");
		}

		VerificationCode token = tokenOpt.get();

		// Kiểm tra token còn hiệu lực không
		if (token.isExpired()) {
			throw new IllegalArgumentException("Mã xác thực đã hết hạn");
		}

		// Kiểm tra token đã được sử dụng chưa
		if (token.isUsed()) {
			throw new IllegalArgumentException("Mã xác thực đã được sử dụng");
		}

		// Tìm user theo email và cập nhật mật khẩu
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email: " + email));

		// Kiểm tra mật khẩu mới không được giống mật khẩu cũ
		if (passwordEncoder.matches(newPassword, user.getPassword())) {
			throw new IllegalArgumentException("Mật khẩu mới không được giống mật khẩu cũ");
		}

		// Cập nhật mật khẩu mới
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);

		// Đánh dấu token đã được sử dụng
		token.setUsed(true);
		verificationCodeRepository.save(token);

		// Xóa tất cả các mã xác thực của email này
		deleteAllVerificationCodesForEmail(email);
	}
	
	@Transactional
	public void deleteAllVerificationCodesForEmail(String email) {
		// Tìm tất cả mã xác thực cho email và xóa
		List<VerificationCode> codes = verificationCodeRepository.findAllByEmail(email);
		verificationCodeRepository.deleteAll(codes);
//		verificationCodeRepository.findAll().stream()
//			.filter(code -> code.getEmail().equals(email))
//			.forEach(code -> verificationCodeRepository.delete(code));
	}


	// Phương thức sinh mã xác thực ngẫu nhiên 6 chữ số
	private String generateVerificationCode() {
		int code = 100000 + random.nextInt(900000); // Tạo số ngẫu nhiên từ 100000 đến 999999
		return String.valueOf(code);
	}
}