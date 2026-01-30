package com.thantruongnhan.doanketthucmon.service;

import java.util.List;
import java.util.Optional;

import com.thantruongnhan.doanketthucmon.entity.WorkShift;

public interface WorkShiftService {
    WorkShift create(WorkShift shift);

    public WorkShift updateShift(Long id, WorkShift updatedShift);

    List<WorkShift> getByBranch(Long branchId);

    List<WorkShift> getByUser(Long userId);

    void checkIn(Long workShiftId, Long userId);

    void checkOut(Long workShiftId, Long userId);

    Optional<WorkShift> getCurrentActiveShift();

    WorkShift getWorkShiftById(Long id);
}
