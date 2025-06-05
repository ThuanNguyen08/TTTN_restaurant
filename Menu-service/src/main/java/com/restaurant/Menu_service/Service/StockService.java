package com.restaurant.Menu_service.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.Menu_service.DTO.StockRequest;
import com.restaurant.Menu_service.DTO.StockResponse;
import com.restaurant.Menu_service.Entity.Ingredient;
import com.restaurant.Menu_service.Entity.Stock;
import com.restaurant.Menu_service.Entity.StockHistory;
import com.restaurant.Menu_service.Entity.StockHistory.TransactionType;
import com.restaurant.Menu_service.Exception.ResourceNotFoundException;
import com.restaurant.Menu_service.Repository.IngredientRepository;
import com.restaurant.Menu_service.Repository.StockHistoryRepository;
import com.restaurant.Menu_service.Repository.StockRepository;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final IngredientRepository ingredientRepository;
    private final StockHistoryRepository stockHistoryRepository;

    public StockService(StockRepository stockRepository, IngredientRepository ingredientRepository,
            StockHistoryRepository stockHistoryRepository) {
        this.stockRepository = stockRepository;
        this.ingredientRepository = ingredientRepository;
        this.stockHistoryRepository = stockHistoryRepository;
    }

    public List<StockResponse> getAllStocks() {
        return stockRepository.findAll().stream().map(StockResponse::fromEntity).toList();
    }

    public StockResponse getStockById(Long id) {
        return stockRepository.findById(id).map(StockResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tồn kho với id: " + id));
    }
    
    public StockResponse getStockByIngredientId(Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + ingredientId));
        
        return stockRepository.findByIngredient(ingredient).map(StockResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tồn kho cho nguyên liệu: " + ingredient.getName()));
    }

    public List<StockResponse> getLowStocks() {
        return stockRepository.findByQuantityLessThanMinQuantity().stream()
                .map(StockResponse::fromEntity).toList();
    }

    @Transactional
    public StockResponse createOrUpdateStock(StockRequest request) {
        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId()).filter(Ingredient::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + request.getIngredientId()));

        Optional<Stock> existingStock = stockRepository.findByIngredient(ingredient);
        
        Stock stock;
        if (existingStock.isPresent()) {
            stock = existingStock.get();
            stock.setQuantity(request.getQuantity());
            stock.setMinQuantity(request.getMinQuantity());
        } else {
            stock = new Stock();
            stock.setIngredient(ingredient);
            stock.setQuantity(request.getQuantity());
            stock.setMinQuantity(request.getMinQuantity());
        }
         
        Stock savedStock = stockRepository.save(stock);
        return StockResponse.fromEntity(savedStock);
    }

    @Transactional
    public StockResponse importStock(Long ingredientId, Double quantity, String note, Long userId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng nhập kho phải lớn hơn 0");
        }
        
        Ingredient ingredient = ingredientRepository.findById(ingredientId).filter(Ingredient::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + ingredientId));

        // Tìm hoặc tạo mới Stock nếu chưa có
        Stock stock = stockRepository.findByIngredient(ingredient).orElse(new Stock());
        stock.setIngredient(ingredient);
        
        // Cập nhật số lượng
        if (stock.getQuantity() == null) {
            stock.setQuantity(quantity);
        } else {
            stock.setQuantity(stock.getQuantity() + quantity);
        }
        
        // Đặt giá trị mặc định cho minQuantity nếu chưa có
        if (stock.getMinQuantity() == null) {
            stock.setMinQuantity(0.0);
        }
        
        Stock updatedStock = stockRepository.save(stock);
        
        // Ghi lại lịch sử nhập kho
        StockHistory history = new StockHistory();
        history.setIngredient(ingredient);
        history.setQuantity(quantity);
        history.setType(TransactionType.IMPORT);
        history.setNote(note);
        history.setUserId(userId);
        stockHistoryRepository.save(history);
        
        return StockResponse.fromEntity(updatedStock);
    }

    @Transactional
    public StockResponse exportStock(Long ingredientId, Double quantity, String note, Long userId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng xuất kho phải lớn hơn 0");
        }
        
        Ingredient ingredient = ingredientRepository.findById(ingredientId).filter(Ingredient::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với id: " + ingredientId));

        Stock stock = stockRepository.findByIngredient(ingredient)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tồn kho cho nguyên liệu: " + ingredient.getName()));
        
        if (stock.getQuantity() < quantity) {
            throw new IllegalArgumentException("Số lượng xuất kho không được vượt quá số lượng tồn kho hiện tại");
        }
        
        // Cập nhật số lượng
        stock.setQuantity(stock.getQuantity() - quantity);
        Stock updatedStock = stockRepository.save(stock);
        
        // Ghi lại lịch sử xuất kho
        StockHistory history = new StockHistory();
        history.setIngredient(ingredient);
        history.setQuantity(quantity);
        history.setType(TransactionType.EXPORT);
        history.setNote(note);
        history.setUserId(userId);
        stockHistoryRepository.save(history);
        
        return StockResponse.fromEntity(updatedStock);
    }
    
    public StockResponse updateMinQuantity(Long stockId, Double minQuantity) {
        if (minQuantity < 0) {
            throw new IllegalArgumentException("Số lượng tối thiểu không được nhỏ hơn 0");
        }
        
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tồn kho với id: " + stockId));
        
        stock.setMinQuantity(minQuantity);
        Stock updatedStock = stockRepository.save(stock);
        
        return StockResponse.fromEntity(updatedStock);
    }
}