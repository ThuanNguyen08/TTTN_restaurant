package com.restaurant.Table_service.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.Table_service.DTO.TableRequest;
import com.restaurant.Table_service.DTO.TableResponse;
import com.restaurant.Table_service.Entity.Tables;
import com.restaurant.Table_service.Service.TableService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tables")
@Tag(name = "Table", description = "Quản lý thông tin của bàn trong nhà hàng")
public class TableController {
    private final TableService tableService;
    

    public TableController(TableService tableService) {
		this.tableService = tableService;
	}

    @Operation(summary = "Lấy thông tin tất cả bàn", description = "Có thể xem thông tin tất cả bàn có trong hệ thống")
	@GetMapping
    public ResponseEntity<List<TableResponse>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }
    @Operation(summary = "Lấy thông tin bàn bằng id", description = "Có thể xem thông tin bàn có trong hệ thống bằng id")
    @GetMapping("/{id}")
    public ResponseEntity<TableResponse> getTableById(@PathVariable Long id) {
        return ResponseEntity.ok(tableService.getTableById(id));
    }
    
    @Operation(summary = "Lấy danh sách bàn theo trạng thái", description = "Có thể xem danh sách bàn theo trạng thái cụ thể (AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE)")
    @GetMapping("/status")
    public ResponseEntity<List<TableResponse>> getTablesByStatus(@RequestParam Tables.Status status) {
        return ResponseEntity.ok(tableService.getTablesByStatus(status));
    }

    @Operation(summary = "Tạo bàn mới", description = "Có thể tạo một bàn mới trong hệ thống")
    @PostMapping
    public ResponseEntity<TableResponse> createTable(@Valid @RequestBody TableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.createTable(request));
    }

    @Operation(summary = "Cập nhật thông tin bàn", description = "Có thể cập nhật thông tin bàn trong hệ thống bằng id")
    @PutMapping("/{id}")
    public ResponseEntity<TableResponse> updateTable(@PathVariable Long id, @Valid @RequestBody TableRequest request) {
        return ResponseEntity.ok(tableService.updateTable(id, request));
    }

    @Operation(summary = "Cập nhật trạng thái bàn", description = "Có thể cập nhật trạng thái bàn (AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE) trong hệ thống bằng id")
    @PutMapping("/{id}/status")
    public ResponseEntity<TableResponse> updateTableStatus(@PathVariable Long id, @RequestParam Tables.Status status) {
        return ResponseEntity.ok(tableService.updateTableStatus(id, status));
    }

    @Operation(summary = "Xóa bàn", description = "Có thể xóa thông tin bàn có trong hệ thống bằng id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}