package com.restaurant.Revenue_service.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.restaurant.Revenue_service.DTO.TableDto;
import com.restaurant.Revenue_service.DTO.TableDto.TableStatus;
import com.restaurant.Revenue_service.DTO.TableResponse;
import com.restaurant.Revenue_service.Exception.ServiceUnavailableException;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class TableServiceClient {

	private final RestTemplate restTemplate;
	private final String tableServiceUrl;

	public TableServiceClient(RestTemplate restTemplate, @Value("${services.table.url}") String tableServiceUrl) {
		this.restTemplate = restTemplate;
		this.tableServiceUrl = tableServiceUrl;
	}

	public TableDto getTableById(Long tableId, HttpServletRequest request) {
		try {
			String authHeader = request.getHeader("Authorization");

			HttpHeaders headers = new HttpHeaders();
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				headers.set("Authorization", authHeader);
			}

			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<TableResponse> response = restTemplate.exchange(tableServiceUrl + "/api/tables/{id}",
					HttpMethod.GET, entity, TableResponse.class, tableId);

			if (response.getBody() == null) {
				throw new RuntimeException("Không tìm thấy bàn với id: " + tableId);
			}
			TableResponse table = response.getBody();
			return new TableDto(table.getId(), table.getName(), TableStatus.valueOf(table.getStatus()));
		} catch (Exception e) {
			e.printStackTrace(); // In stack trace đầy đủ
			System.err.println("Chi tiết lỗi: " + e.getMessage());
			throw new ServiceUnavailableException("Table service không hoạt động1");
		}
	}

	public void updateTableStatus(Long tableId, TableStatus status, HttpServletRequest request) {
		try {
			String authHeader = request.getHeader("Authorization");

			HttpHeaders headers = new HttpHeaders();
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				headers.set("Authorization", authHeader);
			}

			HttpEntity<TableStatus> entity = new HttpEntity<>(headers);

			restTemplate.exchange(tableServiceUrl + "/api/tables/{id}/status?status={status}", HttpMethod.PUT, entity,
					Void.class, tableId, status);
		} catch (Exception e) {
			e.printStackTrace(); // In stack trace đầy đủ
			System.err.println("Chi tiết lỗi: " + e.getMessage());
			throw new ServiceUnavailableException("Table service không hoạt độngg2");
		}
	}
}
