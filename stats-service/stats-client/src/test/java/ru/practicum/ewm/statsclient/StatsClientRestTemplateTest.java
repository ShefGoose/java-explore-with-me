package ru.practicum.ewm.statsclient;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.statsdto.HitDto;
import ru.practicum.ewm.statsdto.StatsDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.time.LocalDateTime;
import java.util.List;

public class StatsClientRestTemplateTest {

    private StatsClientRestTemplate client;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        client = new StatsClientRestTemplate(restTemplate, "http://stats");
    }

    @Test
    void shouldReturnBody() {
        HitDto hitDto = new HitDto(null, "app", "/events", "1.1.1.1",
                LocalDateTime.now().withNano(0));

        server.expect(requestTo("http://stats/hit"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(json(hitDto), MediaType.APPLICATION_JSON));
        HitDto saved = client.hit(hitDto);
        assertThat(saved).usingRecursiveComparison().isEqualTo(hitDto);
        server.verify();
    }

    @Test
    void shouldGetStatsWithoutUris() {
        LocalDateTime start = LocalDateTime.of(2025, 7, 1, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2025, 7, 2, 0, 0);

        StatsDto[] answer = {
                new StatsDto("app", "/events",    3L),
                new StatsDto("app", "/events/5",  1L)
        };

        server.expect(requestTo(
                        "http://stats/stats?start=2025-07-01%2000:00:00" +
                                "&end=2025-07-02%2000:00:00&unique=false"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json(answer), MediaType.APPLICATION_JSON));

        List<StatsDto> result = client.getStats(start, end, null, false);

        assertThat(result).containsExactly(answer);
        server.verify();
    }

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static String json(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("Не удается сериализовать в JSON", e);
        }
    }
}
