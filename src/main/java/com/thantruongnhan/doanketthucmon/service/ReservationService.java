package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.entity.Reservation;
import com.thantruongnhan.doanketthucmon.entity.enums.ReservationStatus;

import java.util.List;

public interface ReservationService {

    Reservation createReservation(Reservation reservation);

    List<Reservation> getPendingReservations();

    Reservation updateStatus(Long reservationId, ReservationStatus status);
}
