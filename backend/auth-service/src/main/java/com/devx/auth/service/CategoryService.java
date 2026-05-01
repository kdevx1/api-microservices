package com.devx.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devx.auth.dto.CategoryRequest;
import com.devx.auth.dto.CategoryResponse;
import com.devx.auth.exception.BusinessException;
import com.devx.auth.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> findAll() {
        return categoryRepository.findByActiveTrueOrderByNameAsc()
                .stream().map(this::map).toList();
    }

    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessException("Categoria já existe", 409);
        }

        com.devx.auth.domain.Category category = com.devx.auth.domain.Category.builder()
                .name(request.name())
                .description(request.description())
                .color(request.color())
                .active(true)
                .build();

        return map(categoryRepository.save(category));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        com.devx.auth.domain.Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoria não encontrada", 404));

        category.setName(request.name());
        category.setDescription(request.description());
        category.setColor(request.color());

        return map(categoryRepository.save(category));
    }

    public void delete(Long id) {
        com.devx.auth.domain.Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoria não encontrada", 404));

        long count = categoryRepository.countActiveServicesByCategoryId(id);
        if (count > 0) {
            throw new BusinessException("Categoria possui serviços ativos. Remova os serviços antes.", 400);
        }

        category.setActive(false);
        categoryRepository.save(category);
    }

    public com.devx.auth.domain.Category findEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoria não encontrada", 404));
    }

    private CategoryResponse map(com.devx.auth.domain.Category c) {
        long count = categoryRepository.countActiveServicesByCategoryId(c.getId());
        return new CategoryResponse(
                c.getId(), c.getName(), c.getDescription(),
                c.getColor(), c.getActive(), count, c.getCreatedAt()
        );
    }
}