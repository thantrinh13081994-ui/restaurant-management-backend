package com.thantruongnhan.doanketthucmon.repository;

import com.thantruongnhan.doanketthucmon.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("""
                SELECT DISTINCT p FROM Promotion p
                LEFT JOIN FETCH p.products
                LEFT JOIN FETCH p.branches
            """)
    List<Promotion> findAllWithProducts();

    @Query("""
                SELECT p FROM Promotion p
                LEFT JOIN FETCH p.products
                LEFT JOIN FETCH p.branches
                WHERE p.id = :id
            """)
    Optional<Promotion> findByIdWithProducts(@Param("id") Long id);

}
