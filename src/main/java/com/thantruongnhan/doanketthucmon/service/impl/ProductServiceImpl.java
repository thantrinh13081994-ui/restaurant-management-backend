package com.thantruongnhan.doanketthucmon.service.impl;

import com.thantruongnhan.doanketthucmon.entity.Category;
import com.thantruongnhan.doanketthucmon.entity.Product;
import com.thantruongnhan.doanketthucmon.repository.CategoryRepository;
import com.thantruongnhan.doanketthucmon.repository.ProductRepository;
import com.thantruongnhan.doanketthucmon.service.ProductService;
import com.thantruongnhan.doanketthucmon.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StorageService storageService;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public Product createProduct(String name, BigDecimal price, Long categoryId,
            MultipartFile image) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // Set category nếu có
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
            product.setCategory(category);
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = storageService.saveImage(image);
            product.setImageUrl(imageUrl);
        }

        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, String name, BigDecimal price, Long categoryId,
            MultipartFile image) {
        Product existing = getProductById(id);
        existing.setName(name);
        existing.setPrice(price);
        existing.setUpdatedAt(LocalDateTime.now());

        // Update category nếu có
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
            existing.setCategory(category);
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = storageService.saveImage(image);
            existing.setImageUrl(imageUrl);
        }

        return productRepository.save(existing);
    }

    @Override
    public void deleteProduct(Long id) {
        Product existing = getProductById(id);
        productRepository.delete(existing);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

}