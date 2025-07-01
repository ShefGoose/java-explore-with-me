package ru.practicum.ewm.statsclient;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.statsdto.HitDto;
import ru.practicum.ewm.statsdto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor
public class StatsClientRestTemplate implements StatsClient {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate rest;
    private final String baseUrl;

    @Override
    public HitDto hit(HitDto hitDto) {
        String url = baseUrl + "/hit";

        ResponseEntity<HitDto> response =
        rest.postForEntity(url, hitDto, HitDto.class);

        return response.getBody();
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start,
                                   LocalDateTime end,
                                   List<String> uris,
                                   boolean unique) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/stats")
                .queryParam("start", DATE_FORMAT.format(start))
                .queryParam("end", DATE_FORMAT.format(end))
                .queryParam("unique", unique);


        if (uris != null && !uris.isEmpty()) {
            uris.forEach(u -> builder.queryParam("uris", u));
        }

        ResponseEntity<StatsDto[]> resp = rest.exchange(
                builder.encode().build().toUri(),
                HttpMethod.GET,
                null,
                StatsDto[].class);


        StatsDto[] body = resp.getBody();

        if (body == null || body.length == 0) {
            return List.of();
        }

        return List.of(body);
    }
}
