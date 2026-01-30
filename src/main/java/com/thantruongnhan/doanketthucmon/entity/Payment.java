package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalAmount;

    @Column(length = 30)
    private String method;

    @Column(length = 30)
    private String status;

    private LocalDateTime paymentDate;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
