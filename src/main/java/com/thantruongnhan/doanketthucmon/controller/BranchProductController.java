package com.thantruongnhan.doanketthucmon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.thantruongnhan.doanketthucmon.dto.ProductWithPromotionDTO;
import com.thantruongnhan.doanketthucmon.entity.BranchProduct;
import com.thantruongnhan.doanketthucmon.service.BranchProductService;

@RestController
@RequestMapping("/api/branch-products")
@CrossOrigin(origins = "http://localhost:3000")
public class BranchProductController {

    @Autowired
    private BranchProductService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public BranchProduct save(@RequestBody BranchProduct bp) {
        return service.save(bp);
    }

    @GetMapping("/branch/{branchId}")
    public List<BranchProduct> getByBranch(@PathVariable Long branchId) {
        return service.getByBranch(branchId);
    }

    @GetMapping("/branch/{branchId}/with-promotions")
    public ResponseEntity<List<ProductWithPromotionDTO>> getProductsWithPromotions(
            @PathVariable Long branchId) {
        List<ProductWithPromotionDTO> products = service.getProductsWithPromotionByBranch(branchId);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{branchProductId}/toggle-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BranchProduct> toggleProductStatus(
            @PathVariable Long branchProductId,
            @RequestBody java.util.Map<String, Boolean> payload) {

        BranchProduct bp = service.findById(branchProductId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        bp.setIsActive(payload.get("isActive"));
        bp.setUpdatedAt(java.time.LocalDateTime.now());

        BranchProduct updated = service.save(bp);
        return ResponseEntity.ok(updated);
    }

}
