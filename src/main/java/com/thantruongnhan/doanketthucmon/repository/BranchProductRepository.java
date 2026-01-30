package com.thantruongnhan.doanketthucmon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thantruongnhan.doanketthucmon.entity.BranchProduct;

public interface BranchProductRepository extends JpaRepository<BranchProduct, Long> {

    List<BranchProduct> findByBranchId(Long branchId);

    @Query("""
                SELECT bp FROM BranchProduct bp
                JOIN FETCH bp.product p
                JOIN FETCH bp.branch b
                WHERE bp.branch.id = :branchId
                AND bp.isActive = true
                AND p.isActive = true
            """)
    List<BranchProduct> findByBranchIdWithProduct(@Param("branchId") Long branchId);

    @Query("""
                SELECT bp FROM BranchProduct bp
                JOIN FETCH bp.product p
                WHERE bp.branch.id = :branchId
                AND bp.product.id = :productId
                AND bp.isActive = true
            """)
    Optional<BranchProduct> findByBranchIdAndProductId(
            @Param("branchId") Long branchId,
            @Param("productId") Long productId);

    @Query("SELECT bp FROM BranchProduct bp WHERE bp.branch.id = :branchId AND bp.isActive = true")
    List<BranchProduct> findAllByBranchId(@Param("branchId") Long branchId);

}
