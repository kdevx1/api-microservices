package com.devx.auth.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.devx.auth.domain.Service;
import com.devx.auth.enums.ServiceType;

public interface ServiceRepository extends
        JpaRepository<Service, Long>,
        JpaSpecificationExecutor<Service> {

    Page<Service> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Service> findByCategoryIdAndType(Long categoryId, ServiceType type, Pageable pageable);
    Page<Service> findByNameContainingIgnoreCaseAndCategoryId(String name, Long categoryId, Pageable pageable);
    Page<Service> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Service> findByActiveTrue(Pageable pageable);
    Page<Service> findByType(ServiceType type, Pageable pageable);
    
    long countByActiveTrue();
    long countByType(ServiceType type);
    long countByCategoryId(Long categoryId);

   @Query("SELECT COALESCE(SUM(so.service.price), 0) FROM ServiceOrder so WHERE so.status = 'DONE'")
    double sumTotalRevenue();
}