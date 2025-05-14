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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.Menu_service.Security.JwtAuthorizationFilter;
import com.restaurant.Menu_service.Security.JwtUtil;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final JwtUtil jwtUtil;
	private final ObjectMapper  objectMapper;

	public SecurityConfig(JwtUtil jwtUtil, ObjectMapper objectMapper) {
		this.jwtUtil = jwtUtil;
		this.objectMapper = objectMapper;
	}

	 @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http.cors(cors -> cors.configure(http))
	            .csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(authorize -> authorize
	            		.requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
	                // Yêu cầu ADMIN hoặc MANAGER để thêm, sửa, xóa
	                .requestMatchers(HttpMethod.POST, "/api/foods").hasAnyAuthority("MANAGER", "ADMIN")
	                .requestMatchers(HttpMethod.PUT, "/api/foods/**").hasAnyAuthority("MANAGER", "ADMIN")
	                .requestMatchers(HttpMethod.DELETE, "/api/foods/**").hasAnyAuthority("MANAGER", "ADMIN")
	                .requestMatchers(HttpMethod.POST, "/api/categories").hasAnyAuthority("MANAGER", "ADMIN")
	                .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasAnyAuthority("MANAGER", "ADMIN")
	                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasAnyAuthority("MANAGER", "ADMIN")
	                .anyRequest().permitAll())
	            .addFilterBefore(new JwtAuthorizationFilter(jwtUtil, objectMapper), UsernamePasswordAuthenticationFilter.class)
	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

	        return http.build();
	    }
}
