package com.devx.auth.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devx.auth.domain.Category;
import com.devx.auth.domain.ServiceOrder;
import com.devx.auth.dto.ServiceOrderRequest;
import com.devx.auth.dto.ServiceOrderResponse;
import com.devx.auth.dto.ServiceRequest;
import com.devx.auth.dto.ServiceResponse;
import com.devx.auth.dto.ServiceStatsResponse;
import com.devx.auth.enums.OrderStatus;
import com.devx.auth.enums.ServiceType;
import com.devx.auth.exception.BusinessException;
import com.devx.auth.repository.ServiceOrderRepository;
import com.devx.auth.repository.ServiceRepository;
import com.devx.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    // =========================
    // SERVICES CRUD
    // =========================
    public ServiceResponse createService(ServiceRequest request) {
        Category category = categoryService.findEntityById(request.categoryId());

        com.devx.auth.domain.Service service = com.devx.auth.domain.Service.builder()
                .name(request.name())
                .description(request.description())
                .category(category)
                .price(request.price())
                .durationMinutes(request.durationMinutes())
                .type(parseType(request.type()))
                .active(true)
                .build();

        return mapService(serviceRepository.save(service));
    }

    public ServiceResponse updateService(Long id, ServiceRequest request) {
        com.devx.auth.domain.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Serviço não encontrado", 404));

        Category category = categoryService.findEntityById(request.categoryId());

        service.setName(request.name());
        service.setDescription(request.description());
        service.setCategory(category);
        service.setPrice(request.price());
        service.setDurationMinutes(request.durationMinutes());
        service.setType(parseType(request.type()));

        return mapService(serviceRepository.save(service));
    }

    public void deleteService(Long id) {
        com.devx.auth.domain.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Serviço não encontrado", 404));
        service.setActive(false);
        serviceRepository.save(service);
    }

    public ServiceResponse toggleService(Long id) {
        com.devx.auth.domain.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Serviço não encontrado", 404));
        service.setActive(!Boolean.TRUE.equals(service.getActive()));
        return mapService(serviceRepository.save(service));
    }

    public Page<ServiceResponse> searchServices(Long categoryId, String name, String type, Pageable pageable) {
        ServiceType serviceType = null;
        if (type != null && !type.isBlank()) {
            try { serviceType = ServiceType.valueOf(type.toUpperCase()); } catch (Exception ignored) {}
        }

        if (categoryId != null && name != null && !name.isBlank()) {
            return serviceRepository.findByNameContainingIgnoreCaseAndCategoryId(name, categoryId, pageable).map(this::mapService);
        }
        if (categoryId != null && serviceType != null) {
            return serviceRepository.findByCategoryIdAndType(categoryId, serviceType, pageable).map(this::mapService);
        }
        if (categoryId != null) {
            return serviceRepository.findByCategoryId(categoryId, pageable).map(this::mapService);
        }
        if (name != null && !name.isBlank()) {
            return serviceRepository.findByNameContainingIgnoreCase(name, pageable).map(this::mapService);
        }
        return serviceRepository.findAll(pageable).map(this::mapService);
    }

    public ServiceResponse getService(Long id) {
        return mapService(serviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Serviço não encontrado", 404)));
    }

    // =========================
    // STATS
    // =========================
    public ServiceStatsResponse getStats() {
        return new ServiceStatsResponse(
                serviceRepository.count(),
                serviceRepository.countByActiveTrue(),
                0, // será preenchido via CategoryService
                serviceOrderRepository.count(),
                serviceOrderRepository.countByStatus(OrderStatus.PENDING),
                serviceRepository.sumTotalRevenue()
        );
    }

    // =========================
    // ORDERS
    // =========================
    public ServiceOrderResponse createOrder(ServiceOrderRequest request) {
        com.devx.auth.domain.Service service = serviceRepository.findById(request.serviceId())
                .orElseThrow(() -> new BusinessException("Serviço não encontrado", 404));

        com.devx.auth.domain.User client = userRepository.findById(request.clientId())
                .orElseThrow(() -> new BusinessException("Cliente não encontrado", 404));

        ServiceOrder order = ServiceOrder.builder()
                .service(service)
                .client(client)
                .scheduledAt(request.scheduledAt())
                .notes(request.notes())
                .status(OrderStatus.PENDING)
                .build();

        return mapOrder(serviceOrderRepository.save(order));
    }

    public ServiceOrderResponse updateOrderStatus(Long id, String status) {
        ServiceOrder order = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Ordem não encontrada", 404));
        try {
            order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        } catch (Exception e) {
            throw new BusinessException("Status inválido", 400);
        }
        return mapOrder(serviceOrderRepository.save(order));
    }

    public Page<ServiceOrderResponse> searchOrders(Pageable pageable) {
        return serviceOrderRepository.findAll(pageable).map(this::mapOrder);
    }

    public Page<ServiceOrderResponse> getOrdersByClient(Long clientId, Pageable pageable) {
        return serviceOrderRepository.findByClientId(clientId, pageable).map(this::mapOrder);
    }

    // =========================
    // MAPPERS
    // =========================
    private ServiceResponse mapService(com.devx.auth.domain.Service s) {
        return new ServiceResponse(
                s.getId(), s.getName(), s.getDescription(),
                s.getCategory().getId(),
                s.getCategory().getName(),
                s.getCategory().getColor(),
                s.getPrice(), s.getDurationMinutes(),
                s.getType().name(), s.getActive(), s.getCreatedAt()
        );
    }

    private ServiceOrderResponse mapOrder(ServiceOrder o) {
        return new ServiceOrderResponse(
                o.getId(),
                o.getService().getName(),
                o.getService().getCategory().getName(),
                o.getClient().getName(),
                o.getClient().getEmail(),
                o.getStatus().name(),
                o.getScheduledAt(),
                o.getNotes(),
                o.getCreatedAt()
        );
    }

    private ServiceType parseType(String type) {
        try {
            return ServiceType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new BusinessException("Tipo inválido. Use SERVICE ou PRODUCT", 400);
        }
    }
}