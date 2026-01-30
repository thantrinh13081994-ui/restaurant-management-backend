package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "branch_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "custom_price")
    private Double customPrice;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (isActive == null) {
            isActive = true;
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}