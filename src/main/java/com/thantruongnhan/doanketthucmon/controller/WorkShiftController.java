package com.thantruongnhan.doanketthucmon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.thantruongnhan.doanketthucmon.entity.WorkShift;
import com.thantruongnhan.doanketthucmon.service.WorkShiftService;

@RestController
@RequestMapping("/api/work-shifts")
@CrossOrigin(origins = "http://localhost:3000")
public class WorkShiftController {

    @Autowired
    private WorkShiftService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public WorkShift create(@RequestBody WorkShift shift) {
        return service.create(shift);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<WorkShift> updateShift(
            @PathVariable Long id,
            @RequestBody WorkShift updatedShift) {

        return ResponseEntity.ok(service.updateShift(id, updatedShift));
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<WorkShift> getByBranch(@PathVariable Long branchId) {
        return service.getByBranch(branchId);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','KITCHEN_STAFF','MANAGER','ADMIN')")
    public List<WorkShift> getByUser(@PathVariable Long userId) {
        return service.getByUser(userId);
    }

    @PostMapping("/{id}/check-in")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> checkIn(
            @PathVariable("id") Long workShiftId,
            @RequestParam Long userId) {
        service.checkIn(workShiftId, userId);
        return ResponseEntity.ok("Check-in thành công");
    }

    @PostMapping("/{id}/check-out")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> checkOut(
            @PathVariable("id") Long workShiftId,
            @RequestParam Long userId) {
        service.checkOut(workShiftId, userId);
        return ResponseEntity.ok("Check-out thành công");
    }
}
