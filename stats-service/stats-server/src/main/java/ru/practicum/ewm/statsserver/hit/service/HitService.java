package ru.practicum.ewm.statsserver.hit.service;

import ru.practicum.ewm.statsdto.HitDto;
import ru.practicum.ewm.statsdto.StatsDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface HitService {
    HitDto create(HitDto hitDto);

    Collection<StatsDto> findAll(LocalDateTime start, LocalDateTime end,
                                 List<String> uris, boolean unique);
}
