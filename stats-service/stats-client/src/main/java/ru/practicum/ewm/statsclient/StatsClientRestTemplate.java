package ru.practicum.ewm.statsclient;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.statsclient.advice.exception.StatsServiceException;
import ru.practicum.ewm.statsdto.HitDto;
import ru.practicum.ewm.statsdto.StatsDto;

import java.net.URI;
import java.nio.charset.StandardCharsets;
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
        URI uri = URI.create(baseUrl + "/hit");

        try {
            ResponseEntity<HitDto> response =
                    rest.postForEntity(uri, hitDto, HitDto.class);
            return requireBody(response.getBody(),
                    "stats-service вернул пустой ответ для POST " + uri);

        } catch (ResourceAccessException netEx) {
            throw new StatsServiceException(
                    "Не удалось подключиться к stats-service: " + uri, netEx);
        } catch (RestClientException restEx) {
            throw new StatsServiceException(
                    "Ошибка вызова stats-service POST " + uri, restEx);
        }
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

        URI uri = builder.encode(StandardCharsets.UTF_8).build().toUri();

        try {
            ResponseEntity<StatsDto[]> resp = rest.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    StatsDto[].class);

            StatsDto[] body = resp.getBody();

            if (body == null || body.length == 0) {
                return List.of();
            }
            return List.of(body);
        } catch (ResourceAccessException netEx) {
            throw new StatsServiceException(
                    "Не удалось подключиться к stats-service: " + uri, netEx);
        } catch (RestClientException restEx) {
            throw new StatsServiceException(
                    "Ошибка вызова stats-service GET " + uri, restEx);
        }
    }

    private static <T> T requireBody(@Nullable T body, String message) {
        if (body == null) {
            throw new StatsServiceException(message);
        }
        return body;
    }
}
