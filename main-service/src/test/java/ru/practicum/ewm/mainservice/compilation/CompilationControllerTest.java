package ru.practicum.ewm.mainservice.compilation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.mainservice.compilation.service.CompilationService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class CompilationControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private CompilationService compilationService;

    @Test
    void getCompilationReturns200() throws Exception {
        when(compilationService.find(anyLong())).thenReturn(null);
        mvc.perform(get("/compilations/{id}", 3))
                .andExpect(status().isOk());
    }

    @Test
    void getAllPinnedReturns200() throws Exception {
        when(compilationService.findAll(eq(true), anyInt(), anyInt()))
                .thenReturn(List.of());

        mvc.perform(get("/compilations")
                        .param("pinned", "true")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }
}
