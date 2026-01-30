package com.thantruongnhan.doanketthucmon.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        String productName,
        int quantity,
        BigDecimal price,
        BigDecimal subtotal) {
}
