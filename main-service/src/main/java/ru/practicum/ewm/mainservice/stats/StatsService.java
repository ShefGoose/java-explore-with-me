package ru.practicum.ewm.mainservice.stats;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.advice.enums.EventState;
import ru.practicum.ewm.mainservice.event.model.Event;
import ru.practicum.ewm.statsclient.StatsClient;
import ru.practicum.ewm.statsdto.HitDto;
import ru.practicum.ewm.statsdto.StatsDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatsService {
    private final StatsClient statsClient;
    private static final String APP_NAME = "ewm-main-service";

    public void addHit(String uri, String ip) {
        HitDto newHitDto = new HitDto();
        newHitDto.setIp(ip);
        newHitDto.setUri(uri);
        newHitDto.setApp(APP_NAME);
        statsClient.hit(newHitDto);
    }

    public Map<String, Long> buildViewsMapForPublished(Collection<Event> events) {
        List<Event> published = events.stream()
                .filter(e -> e.getState() == EventState.PUBLISHED)
                .toList();

        if (published.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> uris = published.stream()
                .map(e -> "/events/" + e.getId())
                .toList();

        return statsClient.getStats(
                        published.stream()
                                .map(Event::getPublishedOn)
                                .min(LocalDateTime::compareTo)
                                .orElse(LocalDateTime.now().plusSeconds(1)),
                        LocalDateTime.now(),
                        uris,
                        true
                )
                .stream()
                .collect(Collectors.toMap(
                        StatsDto::getUri,
                        StatsDto::getHits
                ));
    }

    public long getViewsEvent(Event event) {
        return statsClient.getStats(
                        event.getPublishedOn(),
                        LocalDateTime.now().plusSeconds(1),
                        List.of("/events/" + event.getId()),
                        false
                ).stream()
                .findFirst()
                .map(StatsDto::getHits)
                .orElse(0L);
    }

    public long getViewsEventByUniqueIp(Event event) {
        return statsClient.getStats(
                        event.getPublishedOn(),
                        LocalDateTime.now().plusSeconds(1),
                        List.of("/events/" + event.getId()),
                        true
                ).stream()
                .findFirst()
                .map(StatsDto::getHits)
                .orElse(0L);
    }
}
