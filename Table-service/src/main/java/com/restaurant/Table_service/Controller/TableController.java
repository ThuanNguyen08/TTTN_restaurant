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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tables")
public class TableController {
    private final TableService tableService;
    

    public TableController(TableService tableService) {
		this.tableService = tableService;
	}

	@GetMapping
    public ResponseEntity<List<TableResponse>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableResponse> getTableById(@PathVariable Long id) {
        return ResponseEntity.ok(tableService.getTableById(id));
    }

    @GetMapping("/status")
    public ResponseEntity<List<TableResponse>> getTablesByStatus(@RequestParam Tables.Status status) {
        return ResponseEntity.ok(tableService.getTablesByStatus(status));
    }

    @PostMapping
    public ResponseEntity<TableResponse> createTable(@Valid @RequestBody TableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.createTable(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableResponse> updateTable(@PathVariable Long id, @Valid @RequestBody TableRequest request) {
        return ResponseEntity.ok(tableService.updateTable(id, request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TableResponse> updateTableStatus(@PathVariable Long id, @RequestParam Tables.Status status) {
        return ResponseEntity.ok(tableService.updateTableStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}