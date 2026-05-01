package com.devx.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.devx.auth.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByActiveTrueOrderByNameAsc();
    Optional<Category> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT COUNT(s) FROM Service s WHERE s.category.id = :categoryId AND s.active = true")
    long countActiveServicesByCategoryId(Long categoryId);
}