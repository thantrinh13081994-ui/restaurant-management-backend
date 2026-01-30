package com.thantruongnhan.doanketthucmon.repository;

import com.thantruongnhan.doanketthucmon.entity.BranchIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BranchIngredientRepository
        extends JpaRepository<BranchIngredient, Long> {

    List<BranchIngredient> findByBranchId(Long branchId);

    Optional<BranchIngredient> findByBranchIdAndIngredientId(Long branchId, Long ingredientId);
}
