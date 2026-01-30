package com.thantruongnhan.doanketthucmon.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thantruongnhan.doanketthucmon.entity.WorkShift;

public interface WorkShiftRepository extends JpaRepository<WorkShift, Long> {

    List<WorkShift> findByBranchId(Long branchId);

    List<WorkShift> findByUserId(Long userId);

    // Lấy ca hôm nay của 1 nhân viên
    List<WorkShift> findByUserIdAndShiftDate(Long userId, LocalDate shiftDate);

    // Lấy ca theo chi nhánh & ngày (Manager xem)
    List<WorkShift> findByBranchIdAndShiftDate(Long branchId, LocalDate shiftDate);

    @Query("SELECT ws FROM WorkShift ws WHERE :currentTime BETWEEN ws.startTime AND ws.endTime")
    Optional<WorkShift> findActiveShiftByTime(@Param("currentTime") LocalTime currentTime);
}
