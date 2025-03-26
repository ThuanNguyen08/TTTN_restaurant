package com.restaurant.Menu_service.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.restaurant.Menu_service.Security.JwtAuthorizationFilter;
import com.restaurant.Menu_service.Security.JwtUtil;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final JwtUtil jwtUtil;

	public SecurityConfig(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	 @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http.cors(cors -> cors.configure(http))
	            .csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(authorize -> authorize
	                // Cho phép truy cập không xác thực cho API lấy danh sách món ăn
	                .requestMatchers(HttpMethod.GET, "/api/foods").permitAll()
	                .requestMatchers(HttpMethod.GET, "/api/foods/{id}").permitAll()
	                .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
	                .requestMatchers(HttpMethod.GET, "/api/categories/{id}").permitAll()
	                // Yêu cầu ADMIN hoặc MANAGER để thêm, sửa, xóa
	                .requestMatchers(HttpMethod.POST, "/api/foods").hasAnyAuthority("MANAGER", "ADMIN")
	                .requestMatchers(HttpMethod.PUT, "/api/foods/**").hasAnyAuthority("MANAGER", "ADMIN")
	                .requestMatchers(HttpMethod.DELETE, "/api/foods/**").hasAnyAuthority("MANAGER", "ADMIN")
	                .requestMatchers(HttpMethod.POST, "/api/categories").hasAnyAuthority("MANAGER", "ADMIN")
	                .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasAnyAuthority("MANAGER", "ADMIN")
	                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasAnyAuthority("MANAGER", "ADMIN")
	                .anyRequest().authenticated())
	            .addFilterBefore(new JwtAuthorizationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

	        return http.build();
	    }
}
