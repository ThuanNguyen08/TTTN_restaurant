package com.restaurant.Revenue_service.DTO;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class TableBillRequest {
	@NotNull(message = "id không được để trống")
	private Long tableId;
	
	@NotNull(message = "Món ăn không được để trống")
	@NotEmpty(message = "Danh sách món ăn không được rỗng")
	private List<BillItemDto> items;

	public TableBillRequest() {
	}

	public TableBillRequest(Long tableId, List<BillItemDto> items) {
		this.tableId = tableId;
		this.items = items;
	}

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public List<BillItemDto> getItems() {
		return items;
	}

	public void setItems(List<BillItemDto> items) {
		this.items = items;
	}

}
