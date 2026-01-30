package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.entity.Category;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface CategoryService {
    List<Category> getAllCategories();

    Category getCategoryById(Long id);

    public Category createCategory(String name, String description, MultipartFile image);

    public Category updateCategory(Long id, String name, String description, MultipartFile image);

    void deleteCategory(Long id);
}
