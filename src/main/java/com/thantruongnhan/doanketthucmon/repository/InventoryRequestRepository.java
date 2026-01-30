package com.thantruongnhan.doanketthucmon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thantruongnhan.doanketthucmon.entity.InventoryRequest;
import com.thantruongnhan.doanketthucmon.entity.enums.RequestStatus;

public interface InventoryRequestRepository extends JpaRepository<InventoryRequest, Long> {

    List<InventoryRequest> findByBranchId(Long branchId);

    List<InventoryRequest> findByStatus(RequestStatus status);
}
