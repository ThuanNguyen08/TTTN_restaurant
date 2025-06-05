package com.restaurant.Menu_service.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.Menu_service.Entity.Ingredient;
import com.restaurant.Menu_service.Entity.StockHistory;
import com.restaurant.Menu_service.Entity.StockHistory.TransactionType;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
    List<StockHistory> findByIngredient(Ingredient ingredient);
    List<StockHistory> findByType(TransactionType type);
    List<StockHistory> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<StockHistory> findByIngredientAndCreatedAtBetween(Ingredient ingredient, LocalDateTime startDate, LocalDateTime endDate);
}