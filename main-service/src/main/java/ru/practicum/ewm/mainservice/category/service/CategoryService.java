package ru.practicum.ewm.mainservice.category.service;

import ru.practicum.ewm.mainservice.category.dto.CategoryDto;

import java.util.Collection;

public interface CategoryService {
    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto, Long catId);

    void delete(Long catId);

    Collection<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto find(Long catId);
}
