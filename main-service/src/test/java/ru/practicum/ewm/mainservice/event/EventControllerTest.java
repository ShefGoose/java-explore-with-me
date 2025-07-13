package ru.practicum.ewm.mainservice.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.mainservice.event.service.EventService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private EventService eventService;

    @Test
    void adminPatchReturns200() throws Exception {
        when(eventService.updateAdmin(anyLong(), any()))
                .thenReturn(null);

        mvc.perform(patch("/admin/events/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void privateGetEvent_returns200() throws Exception {
        when(eventService.findByInitiator(anyLong(), anyLong()))
                .thenReturn(null);

        mvc.perform(get("/users/{userId}/events/{eventId}", 10, 5))
                .andExpect(status().isOk());
    }
}
