package com.thantruongnhan.doanketthucmon.dto;

import com.thantruongnhan.doanketthucmon.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderStatusDTO {
    private Long id;
    private String customerName;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TableDTO table;
    private PromotionDTO promotion;
    private List<OrderItemDTO> items;

    @Data
    @Builder
    public static class OrderItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private String productImageUrl;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
    }

    @Data
    @Builder
    public static class TableDTO {
        private Long id;
        private Integer number;
        private String status;
    }

    @Data
    @Builder
    public static class PromotionDTO {
        private Long id;
        private String name;
        private BigDecimal discountPercentage;
        private BigDecimal discountAmount;
    }
}