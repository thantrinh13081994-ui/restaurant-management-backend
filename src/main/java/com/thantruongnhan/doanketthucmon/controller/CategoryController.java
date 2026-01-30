package com.thantruongnhan.doanketthucmon.controller;

import com.thantruongnhan.doanketthucmon.entity.Category;
import com.thantruongnhan.doanketthucmon.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/employee/categories")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Xem tất cả danh mục (Admin, Nhân viên)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // Xem danh mục theo ID (Admin, Nhân viên)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    // Tạo danh mục (chỉ Admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Category createCategory(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        return categoryService.createCategory(name, description, image);
    }

    // Cập nhật danh mục (chỉ Admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Category updateCategory(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        return categoryService.updateCategory(id, name, description, image);
    }

    // Xóa danh mục (chỉ Admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
