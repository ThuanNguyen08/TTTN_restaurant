package com.restaurant.Revenue_service.DTO;

public class TableDto {
	private Long id;
	private String name;
	private TableStatus status;

	public enum TableStatus {
		AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE
	}

	public TableDto() {
	}

	public TableDto(Long id, String name, TableStatus status) {
		this.id = id;
		this.name = name;
		this.status = status;
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

	public TableStatus getStatus() {
		return status;
	}

	public void setStatus(TableStatus status) {
		this.status = status;
	}

}
