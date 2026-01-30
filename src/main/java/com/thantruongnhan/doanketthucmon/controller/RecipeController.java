package com.thantruongnhan.doanketthucmon.controller;

import com.thantruongnhan.doanketthucmon.entity.Recipe;
import com.thantruongnhan.doanketthucmon.service.RecipeService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Recipe create(@RequestParam Long productId,
            @RequestParam Long ingredientId,
            @RequestParam Double quantityRequired) {
        return recipeService.create(productId, ingredientId, quantityRequired);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Recipe update(@PathVariable Long id,
            @RequestParam Double quantityRequired) {
        return recipeService.update(id, quantityRequired);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        recipeService.delete(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Recipe getById(@PathVariable Long id) {
        return recipeService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Recipe> getAll() {
        return recipeService.getAll();
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Recipe> getByProduct(@PathVariable Long productId) {
        return recipeService.getByProduct(productId);
    }

    @GetMapping("/ingredient/{ingredientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Recipe> getByIngredient(@PathVariable Long ingredientId) {
        return recipeService.getByIngredient(ingredientId);
    }
}
