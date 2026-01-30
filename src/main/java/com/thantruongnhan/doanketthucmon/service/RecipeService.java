package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.entity.Recipe;

import java.util.List;

public interface RecipeService {

    Recipe create(Long productId, Long ingredientId, Double quantityRequired);

    Recipe update(Long id, Double quantityRequired);

    void delete(Long id);

    Recipe getById(Long id);

    List<Recipe> getAll();

    List<Recipe> getByProduct(Long productId);

    List<Recipe> getByIngredient(Long ingredientId);
}
