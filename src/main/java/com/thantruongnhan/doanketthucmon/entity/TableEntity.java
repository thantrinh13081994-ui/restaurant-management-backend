package com.thantruongnhan.doanketthucmon.entity;

import com.thantruongnhan.doanketthucmon.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tables", uniqueConstraints = {
        @UniqueConstraint(name = "uk_branch_table_number_area", columnNames = { "branch_id", "number", "area" })
})
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bàn thuộc chi nhánh nào
    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    // Số bàn (theo chi nhánh)
    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private Integer capacity;

    // Khu vực / tầng
    @Column(nullable = false)
    private String area;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.FREE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
