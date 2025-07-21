package ru.practicum.ewm.mainservice.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.mainservice.request.service.RequestService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RequestControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private RequestService requestService;

    @Test
    void getUserRequestsReturns200() throws Exception {
        when(requestService.findAll(10L)).thenReturn(List.of());

        mvc.perform(get("/users/{id}/requests", 10))
                .andExpect(status().isOk());
    }
}
