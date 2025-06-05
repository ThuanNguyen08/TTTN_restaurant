package com.restaurant.Menu_service.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.Menu_service.Entity.Ingredient;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByIsActiveTrue();
    List<Ingredient> findByIsActiveFalse();
    Optional<Ingredient> findByNameAndIsActiveTrue(String name);
    boolean existsByNameAndIsActiveTrue(String name);
}