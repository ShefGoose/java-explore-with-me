package ru.practicum.ewm.mainservice.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.mainservice.category.dto.CategoryDto;
import ru.practicum.ewm.mainservice.category.service.CategoryService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void postCategoryReturns201() throws Exception {
        when(categoryService.create(any()))
                .thenReturn(new CategoryDto(1L, "Name"));

        String body = "{\"name\":\"Name\"}";


        mvc.perform(post("/admin/categories")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void getCategoryByIdReturns200() throws Exception {
        when(categoryService.find(anyLong()))
                .thenReturn(new CategoryDto(99L, "Test"));

        mvc.perform(get("/categories/{id}", 99))
                .andExpect(status().isOk());
    }

    @Test
    void getAllCategoriesReturns200() throws Exception {
        when(categoryService.findAll(0, 10))
                .thenReturn(List.of());
        mvc.perform(get("/categories"))
                .andExpect(status().isOk());
    }
}
