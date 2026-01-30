package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.entity.Branch;

import java.util.List;

public interface BranchService {
    Branch create(Branch branch);

    List<Branch> getAll();

    Branch getById(Long id);

    Branch update(Long id, Branch branch);

    void delete(Long id);
}
