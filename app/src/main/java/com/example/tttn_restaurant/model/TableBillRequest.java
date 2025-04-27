package com.example.tttn_restaurant.model;

import java.util.List;

public class TableBillRequest {
    private Long tableId;
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