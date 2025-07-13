package ru.practicum.ewm.mainservice.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.category.dto.CategoryDto;
import ru.practicum.ewm.mainservice.category.service.CategoryService;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/categories")
@Validated
public class CategoryControllerPublic {
    private final CategoryService categoryService;

    @GetMapping
    @Validated
    public Collection<CategoryDto> findAll(@PositiveOrZero @RequestParam(name = "from", required = false,
                                                       defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", required = false,
                                                   defaultValue = "10") Integer size) {
        return categoryService.findAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto find(@PathVariable Long catId) {
        return categoryService.find(catId);
    }
}
