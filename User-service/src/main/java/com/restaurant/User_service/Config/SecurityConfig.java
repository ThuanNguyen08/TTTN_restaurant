package com.restaurant.User_service.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.User_service.Security.JwtAuthenticationFilter;
import com.restaurant.User_service.Security.JwtAuthorizationFilter;
import com.restaurant.User_service.Security.JwtUtil;
import com.restaurant.User_service.Service.UserDetailsServiceImpl;
import com.restaurant.User_service.Service.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final UserDetailsServiceImpl userDetailsService;

	private final JwtUtil jwtUtil;

	private final UserService userService;
	
	private final BCryptPasswordEncoder passwordEncoder;
	
	private final ObjectMapper objectMapper;

	public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, UserService userService,
			BCryptPasswordEncoder passwordEncoder, ObjectMapper objectMapper) {
		this.userDetailsService = userDetailsService;
		this.jwtUtil = jwtUtil;
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.objectMapper = objectMapper;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authConfig) throws Exception {
		AuthenticationManager authenticationManager = authenticationManager(authConfig);

		// Tạo filter JWT với constructor đầy đủ
		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtUtil,
				userService, objectMapper);
//		jwtAuthenticationFilter.setFilterProcessesUrl("/api/users/login");

		http.cors(cors -> cors.configure(http)) // Cách viết mới thay thế cors().and()
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/api/users/login").permitAll()
						.requestMatchers("/api/users/register").hasAnyAuthority("MANAGER", "ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/users").hasAnyAuthority("MANAGER", "ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/users/{id}/status").hasAnyAuthority("MANAGER", "ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasAnyAuthority("MANAGER", "ADMIN")
						.anyRequest().authenticated())
				.addFilter(jwtAuthenticationFilter)
				.addFilterBefore(new JwtAuthorizationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}
