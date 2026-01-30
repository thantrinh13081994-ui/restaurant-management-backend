package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    Product getProductById(Long id);

    List<Product> getProductsByCategory(Long categoryId);

    Product createProduct(String name, BigDecimal price, Long categoryId, MultipartFile image);

    Product updateProduct(Long id, String name, BigDecimal price, Long categoryId,
            MultipartFile image);

    void deleteProduct(Long id);

    List<Product> searchProducts(String keyword);
}