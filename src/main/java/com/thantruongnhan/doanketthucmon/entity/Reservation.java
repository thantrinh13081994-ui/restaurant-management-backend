package com.thantruongnhan.doanketthucmon.entity;

import com.thantruongnhan.doanketthucmon.entity.enums.ReservationStatus;
import com.thantruongnhan.doanketthucmon.entity.enums.ReservationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String phone;

    private String email;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableEntity table; // nếu bạn đặt tên entity là TableEntity

    @Column(nullable = false)
    private Integer guestCount;

    @Column(nullable = false)
    private LocalDateTime reservationTime;

    @Enumerated(EnumType.STRING)
    private ReservationType type;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;

    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
