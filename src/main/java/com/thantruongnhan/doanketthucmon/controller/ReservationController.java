package com.thantruongnhan.doanketthucmon.controller;

import com.thantruongnhan.doanketthucmon.entity.Reservation;
import com.thantruongnhan.doanketthucmon.entity.enums.ReservationStatus;
import com.thantruongnhan.doanketthucmon.service.ReservationService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return reservationService.createReservation(reservation);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','EMPLOYEE')")
    public List<Reservation> getPendingReservations() {
        return reservationService.getPendingReservations();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','EMPLOYEE')")
    public Reservation updateStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status) {
        return reservationService.updateStatus(id, status);
    }
}
