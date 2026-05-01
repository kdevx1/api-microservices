package com.devx.auth.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devx.auth.domain.ServiceOrder;
import com.devx.auth.enums.OrderStatus;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    Page<ServiceOrder> findByClientId(Long clientId, Pageable pageable);
    Page<ServiceOrder> findByStatus(OrderStatus status, Pageable pageable);
    Page<ServiceOrder> findByServiceId(Long serviceId, Pageable pageable);
    long countByStatus(OrderStatus status);
}