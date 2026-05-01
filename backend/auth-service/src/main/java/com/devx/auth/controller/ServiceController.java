package com.devx.auth.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devx.auth.dto.ServiceOrderRequest;
import com.devx.auth.dto.ServiceOrderResponse;
import com.devx.auth.dto.ServiceRequest;
import com.devx.auth.dto.ServiceResponse;
import com.devx.auth.dto.ServiceStatsResponse;
import com.devx.auth.service.ServiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    // =========================
    // STATS
    // =========================
    @GetMapping("/stats")
    public ResponseEntity<ServiceStatsResponse> getStats() {
        return ResponseEntity.ok(serviceService.getStats());
    }

    // =========================
    // SERVICES
    // =========================
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping
    public ResponseEntity<ServiceResponse> create(@Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(serviceService.createService(request));
    }

    @GetMapping
    public ResponseEntity<Page<ServiceResponse>> search(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            Pageable pageable
    ) {
        return ResponseEntity.ok(serviceService.searchServices(categoryId, name, type, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getService(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequest request
    ) {
        return ResponseEntity.ok(serviceService.updateService(id, request));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PatchMapping("/{id}/active")
    public ResponseEntity<ServiceResponse> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.toggleService(id));
    }

    // =========================
    // ORDERS
    // =========================
    @PostMapping("/orders")
    public ResponseEntity<ServiceOrderResponse> createOrder(@Valid @RequestBody ServiceOrderRequest request) {
        return ResponseEntity.ok(serviceService.createOrder(request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/orders")
    public ResponseEntity<Page<ServiceOrderResponse>> getOrders(Pageable pageable) {
        return ResponseEntity.ok(serviceService.searchOrders(pageable));
    }

    @GetMapping("/orders/client/{clientId}")
    public ResponseEntity<Page<ServiceOrderResponse>> getOrdersByClient(
            @PathVariable Long clientId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(serviceService.getOrdersByClient(clientId, pageable));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<ServiceOrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(serviceService.updateOrderStatus(id, status));
    }
}