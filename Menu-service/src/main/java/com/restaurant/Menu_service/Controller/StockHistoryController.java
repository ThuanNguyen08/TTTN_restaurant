package com.restaurant.Menu_service.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.Menu_service.DTO.StockHistoryRequest;
import com.restaurant.Menu_service.DTO.StockHistoryResponse;
import com.restaurant.Menu_service.Entity.StockHistory.TransactionType;
import com.restaurant.Menu_service.Service.StockHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stock-histories")
@Tag(name = "Stock History", description = "Quản lý lịch sử nhập xuất kho nguyên liệu")
public class StockHistoryController {

    private final StockHistoryService stockHistoryService;

    public StockHistoryController(StockHistoryService stockHistoryService) {
        this.stockHistoryService = stockHistoryService;
    }

    @Operation(summary = "Lấy tất cả lịch sử tồn kho", description = "Có thể xem tất cả lịch sử nhập xuất kho trong hệ thống")
    @GetMapping
    public ResponseEntity<List<StockHistoryResponse>> getAllStockHistories() {
        List<StockHistoryResponse> histories = stockHistoryService.getAllStockHistories();
        return ResponseEntity.ok(histories);
    }

    @Operation(summary = "Lấy lịch sử tồn kho theo id", description = "Có thể xem chi tiết một giao dịch nhập xuất kho bằng id")
    @GetMapping("/{id}")
    public ResponseEntity<StockHistoryResponse> getStockHistoryById(@PathVariable Long id) {
        StockHistoryResponse history = stockHistoryService.getStockHistoryById(id);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Lấy lịch sử tồn kho theo nguyên liệu", description = "Có thể xem lịch sử nhập xuất kho của một nguyên liệu cụ thể")
    @GetMapping("/ingredient/{ingredientId}")
    public ResponseEntity<List<StockHistoryResponse>> getStockHistoriesByIngredientId(@PathVariable Long ingredientId) {
        List<StockHistoryResponse> histories = stockHistoryService.getStockHistoriesByIngredientId(ingredientId);
        return ResponseEntity.ok(histories);
    }

    @Operation(summary = "Lấy lịch sử tồn kho theo loại giao dịch", description = "Có thể xem danh sách giao dịch nhập kho (IMPORT) hoặc xuất kho (EXPORT)")
    @GetMapping("/type")
    public ResponseEntity<List<StockHistoryResponse>> getStockHistoriesByType(@RequestParam TransactionType type) {
        List<StockHistoryResponse> histories = stockHistoryService.getStockHistoriesByType(type);
        return ResponseEntity.ok(histories);
    }

    @Operation(summary = "Lấy lịch sử tồn kho theo khoảng thời gian", description = "Có thể xem danh sách giao dịch nhập xuất kho trong một khoảng thời gian")
    @GetMapping("/date-range")
    public ResponseEntity<List<StockHistoryResponse>> getStockHistoriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<StockHistoryResponse> histories = stockHistoryService.getStockHistoriesByDateRange(startDate, endDate);
        return ResponseEntity.ok(histories);
    }

    @Operation(summary = "Lấy lịch sử tồn kho theo nguyên liệu và khoảng thời gian", description = "Có thể xem lịch sử nhập xuất kho của một nguyên liệu trong một khoảng thời gian")
    @GetMapping("/ingredient/{ingredientId}/date-range")
    public ResponseEntity<List<StockHistoryResponse>> getStockHistoriesByIngredientAndDateRange(
            @PathVariable Long ingredientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<StockHistoryResponse> histories = stockHistoryService.getStockHistoriesByIngredientAndDateRange(
                ingredientId, startDate, endDate);
        return ResponseEntity.ok(histories);
    }

//    @Operation(summary = "Tạo một ghi chép lịch sử tồn kho", description = "Có thể tạo thủ công một ghi chép nhập xuất kho (chủ yếu dùng cho mục đích kiểm tra)")
//    @PostMapping
//    public ResponseEntity<StockHistoryResponse> createStockHistory(@Valid @RequestBody StockHistoryRequest request) {
//        StockHistoryResponse createdHistory = stockHistoryService.createStockHistory(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdHistory);
//    }
}