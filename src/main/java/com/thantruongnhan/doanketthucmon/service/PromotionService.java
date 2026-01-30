package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.dto.PromotionDTO;

import java.util.List;
import java.util.Optional;

public interface PromotionService {

    List<PromotionDTO> getAllPromotions();

    Optional<PromotionDTO> getPromotionById(Long id);

    PromotionDTO createPromotion(PromotionDTO promotionDTO);

    PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO);

    void deletePromotion(Long id);
}
