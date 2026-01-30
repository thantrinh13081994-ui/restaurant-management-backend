package com.thantruongnhan.doanketthucmon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithPromotionDTO {
    private Long id;
    private String name;
    private String description;
    private String categoryName;

    // Giá của chi nhánh (từ BranchProduct)
    private BigDecimal branchPrice; // Giá tại chi nhánh này (customPrice hoặc giá gốc)
    private BigDecimal originalPrice; // Giá gốc từ Product
    private BigDecimal finalPrice; // Giá sau khi áp dụng khuyến mãi

    private String imageUrl;
    private Integer stockQuantity; // Số lượng tồn kho tại chi nhánh
    private Boolean isActive;

    // Thông tin khuyến mãi
    private Long promotionId;
    private String promotionName;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private Boolean hasPromotion;

    // Thông tin chi nhánh
    private Long branchProductId;
    private Long branchId;
    private String branchName;
}