package com.restaurant.Revenue_service.DTO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TableResponse {
	private Long id;
	private String name;
	private String status;
	private String description;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	public TableResponse() {
	}

	public TableResponse(Long id, String name, String status, String description, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.status = status;
		this.description = description;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}