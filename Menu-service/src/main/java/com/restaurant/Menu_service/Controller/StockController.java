package com.restaurant.Menu_service.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.Menu_service.DTO.StockRequest;
import com.restaurant.Menu_service.DTO.StockResponse;
import com.restaurant.Menu_service.Service.StockService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stocks")
@Tag(name = "Stock", description = "Quản lý tồn kho nguyên liệu của nhà hàng")
public class StockController {

    @Autowired
    private StockService stockService;
    
    @Operation(summary = "Lấy tất cả tồn kho", description = "Có thể xem thông tin tồn kho của tất cả nguyên liệu trong hệ thống")
    @GetMapping
    public ResponseEntity<List<StockResponse>> getAllStocks() {
        List<StockResponse> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }
    
    @Operation(summary = "Lấy thông tin tồn kho theo id", description = "Có thể xem thông tin chi tiết của tồn kho bằng id")
    @GetMapping("/{id}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable Long id) {
        StockResponse stock = stockService.getStockById(id);
        return ResponseEntity.ok(stock);
    }
    
    @Operation(summary = "Lấy thông tin tồn kho theo nguyên liệu", description = "Có thể xem thông tin tồn kho của một nguyên liệu cụ thể")
    @GetMapping("/ingredient/{ingredientId}")
    public ResponseEntity<StockResponse> getStockByIngredientId(@PathVariable Long ingredientId) {
        StockResponse stock = stockService.getStockByIngredientId(ingredientId);
        return ResponseEntity.ok(stock);
    }
    
    @Operation(summary = "Lấy danh sách tồn kho thấp", description = "Có thể xem danh sách các nguyên liệu có số lượng tồn kho thấp hơn mức tối thiểu")
    @GetMapping("/low")
    public ResponseEntity<List<StockResponse>> getLowStocks() {
        List<StockResponse> stocks = stockService.getLowStocks();
        return ResponseEntity.ok(stocks);
    }
    
    @Operation(summary = "Tạo hoặc cập nhật tồn kho", description = "Có thể tạo mới hoặc cập nhật thông tin tồn kho cho một nguyên liệu")
    @PostMapping
    public ResponseEntity<StockResponse> createOrUpdateStock(@Valid @RequestBody StockRequest request) {
        StockResponse stock = stockService.createOrUpdateStock(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(stock);
    }
    
    @Operation(summary = "Nhập kho nguyên liệu", description = "Có thể thực hiện nhập kho nguyên liệu và cập nhật số lượng tồn kho")
    @PostMapping("/import/{ingredientId}")
    public ResponseEntity<StockResponse> importStock(
            @PathVariable Long ingredientId,
            @RequestParam Double quantity,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) Long userId) {
        StockResponse stock = stockService.importStock(ingredientId, quantity, note, userId);
        return ResponseEntity.ok(stock);
    }
    
    @Operation(summary = "Xuất kho nguyên liệu", description = "Có thể thực hiện xuất kho nguyên liệu và cập nhật số lượng tồn kho")
    @PostMapping("/export/{ingredientId}")
    public ResponseEntity<StockResponse> exportStock(
            @PathVariable Long ingredientId,
            @RequestParam Double quantity,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) Long userId) {
        StockResponse stock = stockService.exportStock(ingredientId, quantity, note, userId);
        return ResponseEntity.ok(stock);
    }
    
    @Operation(summary = "Cập nhật số lượng tối thiểu", description = "Có thể cập nhật số lượng tồn kho tối thiểu cho một nguyên liệu")
    @PutMapping("/{id}/min-quantity")
    public ResponseEntity<StockResponse> updateMinQuantity(
            @PathVariable Long id,
            @RequestParam Double minQuantity) {
        StockResponse stock = stockService.updateMinQuantity(id, minQuantity);
        return ResponseEntity.ok(stock);
    }
}