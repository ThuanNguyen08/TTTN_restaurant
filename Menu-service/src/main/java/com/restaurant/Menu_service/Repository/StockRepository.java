package com.restaurant.Menu_service.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.restaurant.Menu_service.Entity.Ingredient;
import com.restaurant.Menu_service.Entity.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByIngredient(Ingredient ingredient);
    List<Stock> findByQuantityLessThanEqual(Double quantity);
    
    // Tìm những nguyên liệu có số lượng tồn kho ít hơn số lượng tối thiểu
    @Query("SELECT s FROM Stock s WHERE s.quantity < s.minQuantity AND s.ingredient.isActive = true")
    List<Stock> findByQuantityLessThanMinQuantity();
    
    //TÌm những nguyên liệu có số lượng tồn kho ít hơn hoặc bằng sô lượng tối thiểu
    @Query("SELECT s FROM Stock s WHERE s.quantity <= s.minQuantity AND s.ingredient.isActive = true")
    List<Stock> findByQuantityLessThanEqualMinQuantity();
}