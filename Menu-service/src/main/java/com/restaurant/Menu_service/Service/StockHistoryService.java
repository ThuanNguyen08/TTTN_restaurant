package com.restaurant.Menu_service.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.restaurant.Menu_service.DTO.StockHistoryRequest;
import com.restaurant.Menu_service.DTO.StockHistoryResponse;
import com.restaurant.Menu_service.Entity.Ingredient;
import com.restaurant.Menu_service.Entity.StockHistory;
import com.restaurant.Menu_service.Entity.StockHistory.TransactionType;
import com.restaurant.Menu_service.Exception.ResourceNotFoundException;
import com.restaurant.Menu_service.Repository.IngredientRepository;
import com.restaurant.Menu_service.Repository.StockHistoryRepository;

@Service
public class StockHistoryService {

    private final StockHistoryRepository stockHistoryRepository;
    private final IngredientRepository ingredientRepository;

    public StockHistoryService(StockHistoryRepository stockHistoryRepository, IngredientRepository ingredientRepository) {
        this.stockHistoryRepository = stockHistoryRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<StockHistoryResponse> getAllStockHistories() {
        return stockHistoryRepository.findAll().stream().map(StockHistoryResponse::fromEntity).toList();
    }

    public StockHistoryResponse getStockHistoryById(Long id) {
        return stockHistoryRepository.findById(id).map(StockHistoryResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch sử tồn kho với id: " + id));
    }

    public List<StockHistoryResponse> getStockHistoriesByIngredientId(Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + ingredientId));

        return stockHistoryRepository.findByIngredient(ingredient).stream().map(StockHistoryResponse::fromEntity).toList();
    }

    public List<StockHistoryResponse> getStockHistoriesByType(TransactionType type) {
        return stockHistoryRepository.findByType(type).stream().map(StockHistoryResponse::fromEntity).toList();
    }

    public List<StockHistoryResponse> getStockHistoriesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return stockHistoryRepository.findByCreatedAtBetween(startDate, endDate).stream()
                .map(StockHistoryResponse::fromEntity).toList();
    }

    public List<StockHistoryResponse> getStockHistoriesByIngredientAndDateRange(
            Long ingredientId, LocalDateTime startDate, LocalDateTime endDate) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + ingredientId));

        return stockHistoryRepository.findByIngredientAndCreatedAtBetween(ingredient, startDate, endDate).stream()
                .map(StockHistoryResponse::fromEntity).toList();
    }

    public StockHistoryResponse createStockHistory(StockHistoryRequest request) {
        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId()).filter(Ingredient::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + request.getIngredientId()));

        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        StockHistory stockHistory = new StockHistory();
        stockHistory.setIngredient(ingredient);
        stockHistory.setQuantity(request.getQuantity());
        stockHistory.setType(request.getType());
        stockHistory.setNote(request.getNote());
        stockHistory.setUserId(request.getUserId());

        StockHistory savedStockHistory = stockHistoryRepository.save(stockHistory);
        return StockHistoryResponse.fromEntity(savedStockHistory);
    }
}