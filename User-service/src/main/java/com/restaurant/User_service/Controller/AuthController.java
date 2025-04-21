// AuthController.java
package com.restaurant.User_service.Controller;

import com.restaurant.User_service.DTO.TokenRefreshRequest;
import com.restaurant.User_service.DTO.TokenRefreshResponse;
import com.restaurant.User_service.Entity.RefreshToken;
import com.restaurant.User_service.Exeption.TokenRefreshException;
import com.restaurant.User_service.Security.JwtUtil;
import com.restaurant.User_service.Service.RefreshTokenService;
import com.restaurant.User_service.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class AuthController {
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserDetailsService userDetailsService;

    public AuthController(RefreshTokenService refreshTokenService, JwtUtil jwtUtil, 
                         UserService userService, UserDetailsService userDetailsService) {
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
                    String newAccessToken = jwtUtil.generateToken(userDetails, user);
                    
                    return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token không tồn tại trong cơ sở dữ liệu!"));
    }
}