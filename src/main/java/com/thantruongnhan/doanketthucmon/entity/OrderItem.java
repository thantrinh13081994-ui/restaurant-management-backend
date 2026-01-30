package com.thantruongnhan.doanketthucmon.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thantruongnhan.doanketthucmon.entity.enums.KitchenStatus;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnoreProperties({ "items", "payment" }) // Tránh vòng lặp JSON
    private Order order;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Product product;

    @ManyToOne
    @JoinColumn(name = "branch_product_id")
    private BranchProduct branchProduct;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Giá tại thời điểm đặt hàng

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "kitchen_status", nullable = false)
    private KitchenStatus kitchenStatus = KitchenStatus.WAITING;

    @Column(nullable = false)
    private Boolean priority = false; // Món ưu tiên

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Tính subtotal
    public void calculateSubtotal() {
        if (price != null && quantity != null) {
            this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    // Tự động tính subtotal trước khi lưu
    @PrePersist
    @PreUpdate
    public void prePersist() {
        calculateSubtotal();
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }
}