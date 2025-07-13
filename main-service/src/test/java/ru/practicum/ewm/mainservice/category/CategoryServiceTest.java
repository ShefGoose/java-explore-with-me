package ru.practicum.ewm.mainservice.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.mainservice.advice.exception.DuplicationNameCatException;
import ru.practicum.ewm.mainservice.category.dto.CategoryDto;
import ru.practicum.ewm.mainservice.category.model.Category;
import ru.practicum.ewm.mainservice.category.repository.CategoryRepository;
import ru.practicum.ewm.mainservice.category.service.CategoryServiceImp;
import ru.practicum.ewm.mainservice.event.repository.EventRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private CategoryServiceImp categoryService;

    @Test
    void createDuplicateCategoryException() {

        when(categoryRepository.existsByNameIgnoreCase("Music")).thenReturn(true);

        CategoryDto dto = new CategoryDto(null, "Music");

        assertThrows(DuplicationNameCatException.class,
                () -> categoryService.create(dto));
    }

    @Test
    void updateDuplicateNameThrowsConflict() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category(1L, "Old")));
        when(categoryRepository.existsByNameIgnoreCase("New")).thenReturn(true);

        assertThrows(DuplicationNameCatException.class,
                () -> categoryService.update(new CategoryDto(null, "New"), 1L));
    }

    @Test
    void findAllNullFromSizeReturnsAllCategories() {

        Category c1 = new Category(1L, "Music");
        Category c2 = new Category(2L, "Film");

        when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(c1, c2)));

        Collection<CategoryDto> out = categoryService.findAll(null, null);

        assertEquals(2, out.size());
        assertTrue(out.stream().anyMatch(d -> d.getName().equals("Music")));
        assertTrue(out.stream().anyMatch(d -> d.getName().equals("Film")));
    }
}
