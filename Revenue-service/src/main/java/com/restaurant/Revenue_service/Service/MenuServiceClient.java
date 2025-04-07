package com.restaurant.Revenue_service.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.restaurant.Revenue_service.DTO.FoodDto;
import com.restaurant.Revenue_service.Exception.ServiceUnavailableException;

@Component
public class MenuServiceClient {
	private final RestTemplate restTemplate;
	private final String menuServiceUrl;

	public MenuServiceClient(RestTemplate restTemplate, @Value("${services.menu.url}") String menuServiceUrl) {
		this.restTemplate = restTemplate;
		this.menuServiceUrl = menuServiceUrl;
	}

	public FoodDto getFoodById(Long foodId) {
		try {
			ResponseEntity<FoodDto> response = restTemplate.getForEntity(menuServiceUrl + "/api/foods/{id}",
					FoodDto.class, foodId);
			if (response.getBody() == null) {
				throw new RuntimeException("Không tìm thấy food với id: " + foodId);
			}
			return response.getBody();
		} catch (Exception e) {
			throw new ServiceUnavailableException("Menu service không hoạt động");
		}
	}
}
