package ru.practicum.ewm.mainservice.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.advice.Pagination;
import ru.practicum.ewm.mainservice.advice.exception.DuplicationNameCatException;
import ru.practicum.ewm.mainservice.advice.exception.EntityNotFoundException;
import ru.practicum.ewm.mainservice.advice.exception.EventConflictException;
import ru.practicum.ewm.mainservice.category.dto.CategoryDto;
import ru.practicum.ewm.mainservice.category.dto.CategoryMapper;
import ru.practicum.ewm.mainservice.category.model.Category;
import ru.practicum.ewm.mainservice.category.repository.CategoryRepository;
import ru.practicum.ewm.mainservice.event.repository.EventRepository;

import java.util.Collection;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        if (categoryRepository.existsByNameIgnoreCase(categoryDto.getName())) {
            throw new DuplicationNameCatException("Категория с таким именем уже существует");
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto, Long catId) {
        Category categoryUpdate = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" + catId + " was not found"));

        if (!categoryUpdate.getName().equalsIgnoreCase(categoryDto.getName())) {
            if (categoryRepository.existsByNameIgnoreCase(categoryDto.getName())) {
                throw new DuplicationNameCatException("Категория с таким именем уже существует");
            }
            categoryUpdate.setName(categoryDto.getName());
        }

        return CategoryMapper.toCategoryDto(categoryRepository.save(categoryUpdate));
    }

    @Override
    public void delete(Long catId) {
        if (eventRepository.existsByCategoryId(catId)) {
            throw new EventConflictException("The category is not empty");
        }

        if (!categoryRepository.existsById(catId)) {
            throw new EntityNotFoundException("Category with id=" + catId + " was not found");
        }

        categoryRepository.deleteById(catId);
    }

    @Override
    public Collection<CategoryDto> findAll(Integer from, Integer size) {
        PageRequest pageRequest = Pagination.makePageRequest(from, size);
        Page<Category> categories;
        categories = categoryRepository.findAll(Objects.requireNonNullElseGet(pageRequest,
                () -> PageRequest.of(0, Integer.MAX_VALUE)));

        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto find(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" + catId + " was not found"));

        return CategoryMapper.toCategoryDto(category);
    }
}
