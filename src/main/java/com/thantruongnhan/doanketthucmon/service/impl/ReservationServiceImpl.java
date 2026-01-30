package com.thantruongnhan.doanketthucmon.service.impl;

import com.thantruongnhan.doanketthucmon.entity.Reservation;
import com.thantruongnhan.doanketthucmon.entity.enums.ReservationStatus;
import com.thantruongnhan.doanketthucmon.repository.ReservationRepository;
import com.thantruongnhan.doanketthucmon.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    @Override
    public Reservation createReservation(Reservation reservation) {
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.PENDING);
        return reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> getPendingReservations() {
        return reservationRepository.findByStatus(ReservationStatus.PENDING);
    }

    @Override
    public Reservation updateStatus(Long reservationId, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(status);
        reservation.setUpdatedAt(LocalDateTime.now());

        return reservationRepository.save(reservation);
    }
}
