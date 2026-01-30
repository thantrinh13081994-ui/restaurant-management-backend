package com.thantruongnhan.doanketthucmon.service;

import java.util.List;
import java.util.Optional;

import com.thantruongnhan.doanketthucmon.dto.ProductWithPromotionDTO;
import com.thantruongnhan.doanketthucmon.entity.BranchProduct;

public interface BranchProductService {
    BranchProduct save(BranchProduct bp);

    List<BranchProduct> getByBranch(Long branchId);

    List<ProductWithPromotionDTO> getProductsWithPromotionByBranch(Long branchId);

    Optional<BranchProduct> findById(Long id);
}
