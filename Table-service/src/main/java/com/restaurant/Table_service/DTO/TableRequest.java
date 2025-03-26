package com.restaurant.Table_service.DTO;

import com.restaurant.Table_service.Entity.Tables;
import com.restaurant.Table_service.Entity.Tables.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TableRequest {
	@NotBlank(message = "Tên bàn không được để trống")
	@Size(max = 100, message = "Tên bàn không được quá 100 ký tự")
	private String name;

	private Tables.Status status = Tables.Status.AVAILABLE;

	private String description;

	public TableRequest() {
	}

	public TableRequest(String name, Status status, String description) {
		this.name = name;
		this.status = status;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Tables.Status getStatus() {
		return status;
	}

	public void setStatus(Tables.Status status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}