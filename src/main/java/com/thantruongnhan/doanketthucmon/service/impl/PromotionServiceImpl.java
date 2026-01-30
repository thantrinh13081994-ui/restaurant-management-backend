package com.thantruongnhan.doanketthucmon.service.impl;

import com.thantruongnhan.doanketthucmon.dto.ProductDTO;
import com.thantruongnhan.doanketthucmon.dto.PromotionDTO;
import com.thantruongnhan.doanketthucmon.entity.Branch;
import com.thantruongnhan.doanketthucmon.entity.Product;
import com.thantruongnhan.doanketthucmon.entity.Promotion;
import com.thantruongnhan.doanketthucmon.repository.BranchRepository;
import com.thantruongnhan.doanketthucmon.repository.ProductRepository;
import com.thantruongnhan.doanketthucmon.repository.PromotionRepository;
import com.thantruongnhan.doanketthucmon.service.PromotionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository, ProductRepository productRepository,
            BranchRepository branchRepository) {
        this.promotionRepository = promotionRepository;
        this.productRepository = productRepository;
        this.branchRepository = branchRepository;
    }

    // ===================== GET ALL =====================
    @Override
    @Transactional(readOnly = true)
    public List<PromotionDTO> getAllPromotions() {
        return promotionRepository.findAllWithProducts()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // ===================== GET BY ID =====================
    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionDTO> getPromotionById(Long id) {
        return promotionRepository.findByIdWithProducts(id)
                .map(this::mapToDTO);
    }

    // ===================== CREATE =====================
    @Override
    @Transactional
    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        Promotion promotion = new Promotion();
        promotion.setName(promotionDTO.getName());
        promotion.setDescription(promotionDTO.getDescription());
        promotion.setDiscountPercentage(promotionDTO.getDiscountPercentage());
        promotion.setDiscountAmount(promotionDTO.getDiscountAmount());
        promotion.setStartDate(promotionDTO.getStartDate());
        promotion.setEndDate(promotionDTO.getEndDate());
        promotion.setIsActive(promotionDTO.getIsActive());
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());

        // Lấy products từ productIds
        if (promotionDTO.getProductIds() != null && !promotionDTO.getProductIds().isEmpty()) {
            Set<Product> products = new HashSet<>(
                    productRepository.findAllById(promotionDTO.getProductIds()));
            promotion.setProducts(products);
        }

        // Lấy branches từ branchIds
        if (promotionDTO.getBranchIds() != null && !promotionDTO.getBranchIds().isEmpty()) {
            Set<Branch> branches = new HashSet<>(
                    branchRepository.findAllById(promotionDTO.getBranchIds()));

            System.out.println("Branches found: " + branches.size()); // Debug log

            promotion.setBranches(branches);
        } else {
            promotion.setBranches(new HashSet<>());
        }

        // Save và flush để đảm bảo cascade
        Promotion saved = promotionRepository.saveAndFlush(promotion);

        // Fetch lại để có đầy đủ data
        Promotion fetched = promotionRepository.findByIdWithProducts(saved.getId())
                .orElseThrow(() -> new RuntimeException("Failed to fetch saved promotion"));

        return mapToDTO(fetched);
    }

    // ===================== UPDATE =====================
    @Override
    @Transactional
    public PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO) {
        return promotionRepository.findByIdWithProducts(id)
                .map(existing -> {
                    existing.setName(promotionDTO.getName());
                    existing.setDescription(promotionDTO.getDescription());
                    existing.setDiscountPercentage(promotionDTO.getDiscountPercentage());
                    existing.setDiscountAmount(promotionDTO.getDiscountAmount());
                    existing.setStartDate(promotionDTO.getStartDate());
                    existing.setEndDate(promotionDTO.getEndDate());
                    existing.setIsActive(promotionDTO.getIsActive());
                    existing.setUpdatedAt(LocalDateTime.now());

                    // Cập nhật products từ productIds
                    if (promotionDTO.getProductIds() != null) {
                        Set<Product> products = new HashSet<>(
                                productRepository.findAllById(promotionDTO.getProductIds()));
                        existing.setProducts(products);
                    }

                    if (promotionDTO.getBranchIds() != null && !promotionDTO.getBranchIds().isEmpty()) {
                        Set<Branch> branches = new HashSet<>(
                                branchRepository.findAllById(promotionDTO.getBranchIds()));
                        existing.setBranches(branches);
                    } else {
                        existing.setBranches(new HashSet<>());
                    }

                    Promotion updated = promotionRepository.save(existing);
                    return mapToDTO(updated);
                })
                .orElseThrow(() -> new RuntimeException("Promotion not found with id " + id));
    }

    // ===================== DELETE =====================
    @Override
    @Transactional
    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }

    // ===================== MAPPING =====================
    private PromotionDTO mapToDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setId(promotion.getId());
        dto.setName(promotion.getName());
        dto.setDescription(promotion.getDescription());
        dto.setDiscountPercentage(promotion.getDiscountPercentage());
        dto.setDiscountAmount(promotion.getDiscountAmount());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setIsActive(promotion.getIsActive());
        dto.setCreatedAt(promotion.getCreatedAt());
        dto.setUpdatedAt(promotion.getUpdatedAt());

        // Map productIds
        if (promotion.getProducts() != null && !promotion.getProducts().isEmpty()) {
            dto.setProductIds(
                    promotion.getProducts().stream()
                            .map(Product::getId)
                            .collect(Collectors.toList()));
            dto.setProducts(
                    promotion.getProducts().stream()
                            .map(this::mapProductToDTO)
                            .toList());
        } else {
            dto.setProductIds(new ArrayList<>());
            dto.setProducts(new ArrayList<>());
        }
        // Map products
        if (promotion.getProducts() != null && !promotion.getProducts().isEmpty()) {
            dto.setProducts(
                    promotion.getProducts().stream()
                            .map(this::mapProductToDTO) // ← Phải có method này
                            .toList());
        } else {
            dto.setProducts(new ArrayList<>());
        }
        // Map branchIds
        if (promotion.getBranches() != null && !promotion.getBranches().isEmpty()) {
            try {
                dto.setBranchIds(
                        promotion.getBranches().stream()
                                .map(Branch::getId)
                                .collect(Collectors.toList()));
            } catch (Exception e) {
                System.err.println("Error mapping branches: " + e.getMessage());
                dto.setBranchIds(new ArrayList<>());
            }
        } else {
            dto.setBranchIds(new ArrayList<>());
        }

        return dto;
    }

    private ProductDTO mapProductToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setIsActive(product.getIsActive());
        return dto;
    }
}
