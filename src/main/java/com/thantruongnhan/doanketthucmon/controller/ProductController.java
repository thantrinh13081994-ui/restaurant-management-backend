package com.thantruongnhan.doanketthucmon.controller;

import com.thantruongnhan.doanketthucmon.entity.Product;
import com.thantruongnhan.doanketthucmon.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE', 'CUSTOMER','KITCHEN')")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE', 'CUSTOMER','KITCHEN')")
    public EntityModel<Product> getProductById(@PathVariable Long id) {

        Product product = productService.getProductById(id);

        return EntityModel.of(product,
                linkTo(methodOn(ProductController.class).getProductById(id)).withSelfRel(),
                linkTo(methodOn(ProductController.class).getAllProducts()).withRel("all-products"));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE', 'CUSTOMER')")
    public List<Product> searchProducts(@RequestParam("keyword") String keyword) {
        return productService.searchProducts(keyword);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product createProduct(
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        return productService.createProduct(name, price, categoryId, image);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        return productService.updateProduct(id, name, price, categoryId, image);
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE', 'CUSTOMER')")
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
