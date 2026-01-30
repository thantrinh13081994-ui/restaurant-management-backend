package com.thantruongnhan.doanketthucmon.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thantruongnhan.doanketthucmon.entity.WorkShift;
import com.thantruongnhan.doanketthucmon.entity.enums.ShiftStatus;
import com.thantruongnhan.doanketthucmon.repository.WorkShiftRepository;
import com.thantruongnhan.doanketthucmon.service.WorkShiftService;

import lombok.RequiredArgsConstructor;

@Service
public class WorkShiftServiceImpl implements WorkShiftService {

    private final WorkShiftRepository repository;

    @Autowired
    public WorkShiftServiceImpl(WorkShiftRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<WorkShift> getCurrentActiveShift() {
        LocalTime now = LocalTime.now();
        // Tìm ca làm việc đang active dựa trên thời gian hiện tại
        return repository.findActiveShiftByTime(now);
    }

    @Override
    public WorkShift getWorkShiftById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm việc có ID: " + id));
    }

    @Override
    public WorkShift create(WorkShift shift) {
        return repository.save(shift);
    }

    @Override
    public WorkShift updateShift(Long id, WorkShift updated) {
        WorkShift existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm"));

        if (existing.getStatus() == ShiftStatus.DONE) {
            throw new RuntimeException("Không thể sửa ca đã hoàn thành");
        }

        existing.setShiftDate(updated.getShiftDate());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setRole(updated.getRole());

        // Chỉ cho đổi nhân viên khi ca chưa bắt đầu
        if (existing.getStatus() == ShiftStatus.SCHEDULED) {
            existing.setUser(updated.getUser());
        }

        return repository.save(existing);
    }

    @Override
    public List<WorkShift> getByBranch(Long branchId) {
        return repository.findByBranchId(branchId);
    }

    @Override
    public List<WorkShift> getByUser(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public void checkIn(Long workShiftId, Long userId) {
        WorkShift shift = repository.findById(workShiftId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm"));

        // Chỉ cho check-in ca của chính mình
        if (!shift.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền chấm công ca này");
        }

        if (shift.getStatus() != ShiftStatus.SCHEDULED) {
            throw new RuntimeException("Ca làm không hợp lệ để check-in");
        }

        shift.setCheckInTime(LocalDateTime.now());
        shift.setStatus(ShiftStatus.WORKING);

        repository.save(shift);
    }

    @Override
    public void checkOut(Long workShiftId, Long userId) {
        WorkShift shift = repository.findById(workShiftId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm"));

        if (!shift.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền chấm công ca này");
        }

        if (shift.getStatus() != ShiftStatus.WORKING) {
            throw new RuntimeException("Ca làm chưa check-in hoặc đã kết thúc");
        }

        shift.setCheckOutTime(LocalDateTime.now());
        shift.setStatus(ShiftStatus.DONE);

        repository.save(shift);
    }
}
