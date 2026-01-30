package com.thantruongnhan.doanketthucmon.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thantruongnhan.doanketthucmon.entity.BranchIngredient;
import com.thantruongnhan.doanketthucmon.entity.InventoryRequest;
import com.thantruongnhan.doanketthucmon.entity.InventoryRequestItem;
import com.thantruongnhan.doanketthucmon.entity.User;
import com.thantruongnhan.doanketthucmon.entity.enums.RequestStatus;
import com.thantruongnhan.doanketthucmon.repository.BranchIngredientRepository;
import com.thantruongnhan.doanketthucmon.repository.InventoryRequestItemRepository;
import com.thantruongnhan.doanketthucmon.repository.InventoryRequestRepository;
import com.thantruongnhan.doanketthucmon.service.InventoryRequestService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryRequestServiceImpl implements InventoryRequestService {

    private final InventoryRequestRepository repository;
    private final InventoryRequestItemRepository itemRepo;
    private final BranchIngredientRepository branchIngredientRepo;

    @Override
    public InventoryRequest create(InventoryRequest request, User requester) {
        request.setRequestedBy(requester);
        request.setStatus(RequestStatus.PENDING);
        return repository.save(request);
    }

    @Override
    @Transactional
    public InventoryRequest approve(Long id, User approver) {
        InventoryRequest req = getById(id);

        if (req.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Only PENDING requests can be approved");
        }

        // 1. Lấy danh sách nguyên liệu yêu cầu
        List<InventoryRequestItem> items = itemRepo.findByRequestId(req.getId());

        // 2. Cộng kho cho từng nguyên liệu
        for (InventoryRequestItem item : items) {

            BranchIngredient bi = branchIngredientRepo
                    .findByBranchIdAndIngredientId(
                            req.getBranch().getId(),
                            item.getIngredient().getId())
                    .orElse(
                            BranchIngredient.builder()
                                    .branch(req.getBranch())
                                    .ingredient(item.getIngredient())
                                    .quantity(0.0)
                                    .build());

            bi.setQuantity(bi.getQuantity() + item.getQuantity());
            branchIngredientRepo.save(bi);
        }

        // 3. Update trạng thái request
        req.setStatus(RequestStatus.APPROVED);
        req.setApprovedBy(approver);
        req.setApprovedAt(LocalDateTime.now());

        return repository.save(req);
    }

    @Override
    public InventoryRequest reject(Long id, String note, User approver) {
        InventoryRequest req = getById(id);

        if (req.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Only PENDING requests can be rejected");
        }

        req.setStatus(RequestStatus.REJECTED);
        req.setNote(note);
        req.setApprovedBy(approver);
        req.setApprovedAt(LocalDateTime.now());

        return repository.save(req);
    }

    @Override
    public List<InventoryRequest> getAll() {
        return repository.findAll();
    }

    @Override
    public List<InventoryRequest> getByBranch(Long branchId) {
        return repository.findByBranchId(branchId);
    }

    @Override
    public InventoryRequest getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + id));
    }
}
