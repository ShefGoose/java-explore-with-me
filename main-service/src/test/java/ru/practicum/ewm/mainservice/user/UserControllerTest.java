package ru.practicum.ewm.mainservice.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.mainservice.advice.exception.EventConflictException;
import ru.practicum.ewm.mainservice.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;

    @Test
    void userCreateReturns201() throws Exception {
        when(userService.create(any())).thenReturn(null);

        mvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"nas\",\"email\":\"asda@gmail.com\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void createDuplicateEmailReturns409() throws Exception {
        when(userService.create(any()))
                .thenThrow(new EventConflictException("email занят"));

        mvc.perform(post("/admin/users")
                        .contentType("application/json")
                        .content("{\"name\":\"Bob\",\"email\":\"bobies@gmail.com\"}"))
                .andExpect(status().isConflict());
    }
}
