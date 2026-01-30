package com.thantruongnhan.doanketthucmon.repository;

import com.thantruongnhan.doanketthucmon.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByProductId(Long productId);

    List<Recipe> findByIngredientId(Long ingredientId);

    Optional<Recipe> findByProductIdAndIngredientId(Long productId, Long ingredientId);
}
