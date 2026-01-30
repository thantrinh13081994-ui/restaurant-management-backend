package com.thantruongnhan.doanketthucmon.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thantruongnhan.doanketthucmon.dto.ProductWithPromotionDTO;
import com.thantruongnhan.doanketthucmon.entity.BranchProduct;
import com.thantruongnhan.doanketthucmon.entity.Product;
import com.thantruongnhan.doanketthucmon.entity.Promotion;
import com.thantruongnhan.doanketthucmon.repository.BranchProductRepository;
import com.thantruongnhan.doanketthucmon.repository.ProductRepository;
import com.thantruongnhan.doanketthucmon.repository.PromotionRepository;
import com.thantruongnhan.doanketthucmon.service.BranchProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchProductServiceImpl implements BranchProductService {

    private final BranchProductRepository repository;
    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;

    @Override
    public BranchProduct save(BranchProduct bp) {
        return repository.save(bp);
    }

    @Override
    public List<BranchProduct> getByBranch(Long branchId) {
        return repository.findByBranchId(branchId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductWithPromotionDTO> getProductsWithPromotionByBranch(Long branchId) {
        List<Product> allProducts = productRepository.findAll();
        System.out.println("Tổng products: " + allProducts.size());

        if (allProducts.isEmpty()) {
            System.out.println("KHÔNG CÓ PRODUCTS!");
            return List.of();
        }

        // Lấy branch_products đã phân phối
        List<BranchProduct> branchProducts = repository.findByBranchIdWithProduct(branchId);
        System.out.println("🏢 Đã phân phối: " + branchProducts.size());

        // Map để tra cứu
        var branchProductMap = branchProducts.stream()
                .collect(Collectors.toMap(
                        bp -> bp.getProduct().getId(),
                        bp -> bp,
                        (existing, replacement) -> existing));

        LocalDate today = LocalDate.now();

        // Build DTO
        return allProducts.stream()
                .map(product -> {
                    BranchProduct bp = branchProductMap.get(product.getId());

                    BigDecimal branchPrice;
                    Boolean isActive;
                    Integer stockQuantity;
                    Long branchProductId = null;

                    if (bp != null) {
                        // Đã phân phối
                        branchPrice = (bp.getCustomPrice() != null && bp.getCustomPrice() > 0)
                                ? BigDecimal.valueOf(bp.getCustomPrice())
                                : product.getPrice();
                        isActive = bp.getIsActive();
                        stockQuantity = bp.getStockQuantity();
                        branchProductId = bp.getId();
                    } else {
                        // Chưa phân phối
                        branchPrice = product.getPrice();
                        isActive = false;
                        stockQuantity = 0;
                    }

                    ProductWithPromotionDTO dto = ProductWithPromotionDTO.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .originalPrice(product.getPrice())
                            .branchPrice(branchPrice)
                            .finalPrice(branchPrice)
                            .imageUrl(product.getImageUrl())
                            .stockQuantity(stockQuantity)
                            .isActive(isActive)
                            .hasPromotion(false)
                            .branchProductId(branchProductId)
                            .branchId(branchId)
                            .build();

                    // Khuyến mãi (chỉ cho sản phẩm đã phân phối)
                    if (bp != null) {
                        List<Promotion> activePromotions = promotionRepository.findAll().stream()
                                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                                .filter(p -> p.getStartDate() == null || !p.getStartDate().isAfter(today))
                                .filter(p -> p.getEndDate() == null || !p.getEndDate().isBefore(today))
                                .filter(p -> p.getProducts() != null &&
                                        p.getProducts().stream()
                                                .anyMatch(prod -> prod.getId().equals(product.getId())))
                                .filter(p -> p.getBranches() != null &&
                                        p.getBranches().stream()
                                                .anyMatch(b -> b.getId().equals(branchId)))
                                .toList();

                        if (!activePromotions.isEmpty()) {
                            Promotion bestPromotion = findBestPromotion(activePromotions, branchPrice);

                            dto.setHasPromotion(true);
                            dto.setPromotionId(bestPromotion.getId());
                            dto.setPromotionName(bestPromotion.getName());
                            dto.setDiscountPercentage(bestPromotion.getDiscountPercentage());
                            dto.setDiscountAmount(bestPromotion.getDiscountAmount());
                            dto.setFinalPrice(calculateFinalPrice(branchPrice, bestPromotion));
                        }
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    private Promotion findBestPromotion(List<Promotion> promotions, BigDecimal basePrice) {
        return promotions.stream()
                .max((p1, p2) -> {
                    BigDecimal discount1 = calculateDiscount(basePrice, p1);
                    BigDecimal discount2 = calculateDiscount(basePrice, p2);
                    return discount1.compareTo(discount2);
                })
                .orElse(promotions.get(0));
    }

    private BigDecimal calculateDiscount(BigDecimal basePrice, Promotion promotion) {
        if (promotion.getDiscountPercentage() != null) {
            return basePrice
                    .multiply(promotion.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (promotion.getDiscountAmount() != null) {
            return promotion.getDiscountAmount();
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateFinalPrice(BigDecimal basePrice, Promotion promotion) {
        BigDecimal discount = calculateDiscount(basePrice, promotion);
        BigDecimal finalPrice = basePrice.subtract(discount);

        // Đảm bảo giá không âm
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        return finalPrice.setScale(0, RoundingMode.HALF_UP);
    }

    @Override
    public Optional<BranchProduct> findById(Long id) {
        return repository.findById(id);
    }
}
