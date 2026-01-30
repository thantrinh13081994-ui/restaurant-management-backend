package com.thantruongnhan.doanketthucmon.service.impl;

import com.thantruongnhan.doanketthucmon.entity.Ingredient;
import com.thantruongnhan.doanketthucmon.entity.Product;
import com.thantruongnhan.doanketthucmon.entity.Recipe;
import com.thantruongnhan.doanketthucmon.repository.IngredientRepository;
import com.thantruongnhan.doanketthucmon.repository.ProductRepository;
import com.thantruongnhan.doanketthucmon.repository.RecipeRepository;
import com.thantruongnhan.doanketthucmon.service.RecipeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository,
            ProductRepository productRepository,
            IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public Recipe create(Long productId, Long ingredientId, Double quantityRequired) {

        // Check trùng công thức
        recipeRepository.findByProductIdAndIngredientId(productId, ingredientId)
                .ifPresent(r -> {
                    throw new RuntimeException("Recipe already exists for this product and ingredient");
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        Recipe recipe = Recipe.builder()
                .product(product)
                .ingredient(ingredient)
                .quantityRequired(quantityRequired)
                .build();

        return recipeRepository.save(recipe);
    }

    @Override
    public Recipe update(Long id, Double quantityRequired) {
        Recipe recipe = getById(id);
        recipe.setQuantityRequired(quantityRequired);
        return recipeRepository.save(recipe);
    }

    @Override
    public void delete(Long id) {
        Recipe recipe = getById(id);
        recipeRepository.delete(recipe);
    }

    @Override
    public Recipe getById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));
    }

    @Override
    public List<Recipe> getAll() {
        return recipeRepository.findAll();
    }

    @Override
    public List<Recipe> getByProduct(Long productId) {
        return recipeRepository.findByProductId(productId);
    }

    @Override
    public List<Recipe> getByIngredient(Long ingredientId) {
        return recipeRepository.findByIngredientId(ingredientId);
    }
}
