package com.thantruongnhan.doanketthucmon.service.impl;

import com.thantruongnhan.doanketthucmon.entity.Branch;
import com.thantruongnhan.doanketthucmon.repository.BranchRepository;
import com.thantruongnhan.doanketthucmon.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    public Branch create(Branch branch) {
        return branchRepository.save(branch);
    }

    @Override
    public List<Branch> getAll() {
        return branchRepository.findAll();
    }

    @Override
    public Branch getById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
    }

    @Override
    public Branch update(Long id, Branch branch) {
        Branch existing = getById(id);
        existing.setName(branch.getName());
        existing.setAddress(branch.getAddress());
        existing.setPhone(branch.getPhone());
        existing.setIsActive(branch.getIsActive());
        return branchRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Branch branch = getById(id);
        branch.setIsActive(false);
        branchRepository.deleteById(id);
    }
}
