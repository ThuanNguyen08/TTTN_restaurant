// RefreshTokenService.java
package com.restaurant.User_service.Service;

import com.restaurant.User_service.Entity.RefreshToken;
import com.restaurant.User_service.Entity.User;
import com.restaurant.User_service.Exeption.TokenRefreshException;
import com.restaurant.User_service.Repository.RefreshTokenRepository;
import com.restaurant.User_service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
	@Value("${jwt.refresh.expiration}")
	private Long refreshTokenDurationSec;

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;

	public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.userRepository = userRepository;
	}

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken createRefreshToken(Long userId) {
		RefreshToken refreshToken = new RefreshToken();

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy user với id: " + userId));

		// Kiểm tra nếu user đã có refresh token thì xóa đi
		Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
		existingToken.ifPresent(refreshTokenRepository::delete);

		refreshToken.setUser(user);
		refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationSec*1000));
		refreshToken.setToken(UUID.randomUUID().toString());

		refreshToken = refreshTokenRepository.save(refreshToken);
		return refreshToken;
	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().isBefore(Instant.now())) {
			refreshTokenRepository.delete(token);
			throw new TokenRefreshException(token.getToken(), "Refresh token đã hết hạn. Vui lòng đăng nhập lại");
		}
		return token;
	}

	@Transactional
	public int deleteByUserId(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy user với id: " + userId));
		return refreshTokenRepository.deleteByUser(user);
	}
}