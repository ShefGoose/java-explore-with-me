package ru.practicum.ewm.statsclient;

import ru.practicum.ewm.statsdto.HitDto;
import ru.practicum.ewm.statsdto.StatsDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsClient {
    HitDto hit(HitDto dto);

    Collection<StatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                  List<String> uris, boolean unique);
}
