package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.thantruongnhan.doanketthucmon.entity.enums.ShiftStatus;

@Getter
@Setter
@Entity
@Table(name = "work_shifts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Enumerated(EnumType.STRING)
    private ShiftStatus status;

    private LocalDate shiftDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    private String role;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @Transient
    public String getName() {
        if (startTime != null && endTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return "Ca " + startTime.format(formatter) + " - " + endTime.format(formatter);
        }
        return "Ca #" + id;
    }
}
