package ru.practicum.ewm.statsserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.statsdto.HitDto;

import ru.practicum.ewm.statsdto.StatsDto;
import ru.practicum.ewm.statsserver.advice.exception.BadIpException;
import ru.practicum.ewm.statsserver.hit.controller.HitController;
import ru.practicum.ewm.statsserver.hit.service.HitServiceImp;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HitController.class)
public class StatsServerControllerTest {
    @MockBean
    HitServiceImp hitService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    void testPostHitSuccess() throws Exception {
        HitDto req  = new HitDto(null, "app", "/events",
                "1.1.1.1", LocalDateTime.now().withNano(0));
        HitDto resp = new HitDto(1L,   "app", "/events",
                "1.1.1.1", req.getCreated());

        when(hitService.create(req)).thenReturn(resp);

        mvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.uri").value("/events"));
    }

    @Test
    void testPostHitBadIp() throws Exception {
        HitDto bad = new HitDto(null, "app", "/e",
                "999.999.1.1", LocalDateTime.now());

        when(hitService.create(any(HitDto.class)))
                .thenThrow(new BadIpException("Неверный формат IP"));

        mvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetStatsWithUrisAndUnique() throws Exception {
        List<String> uris = List.of("/events", "/events/5");

        List<StatsDto> list = List.of(
                new StatsDto("app", "/events",   2L),
                new StatsDto("app", "/events/5", 1L));

        when(hitService.findAll(any(), any(), eq(uris), eq(true)))
                .thenReturn(list);

        mvc.perform(get("/stats")
                        .param("start","2025-07-01 00:00:00")
                        .param("end",  "2025-07-02 00:00:00")
                        .param("uris","/events")
                        .param("uris","/events/5")      // мульти-параметр
                        .param("unique","true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].hits").value(2));

        verify(hitService).findAll(any(), any(), eq(uris), eq(true));
    }
}
