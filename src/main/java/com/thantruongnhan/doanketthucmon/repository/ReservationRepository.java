package com.thantruongnhan.doanketthucmon.repository;

import com.thantruongnhan.doanketthucmon.entity.Reservation;
import com.thantruongnhan.doanketthucmon.entity.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByBranchId(Long branchId);
}
