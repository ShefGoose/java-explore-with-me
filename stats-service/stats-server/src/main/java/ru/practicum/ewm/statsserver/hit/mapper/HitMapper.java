package ru.practicum.ewm.statsserver.hit.mapper;

import ru.practicum.ewm.statsdto.HitDto;
import ru.practicum.ewm.statsserver.hit.model.Hit;

public class HitMapper {
    public static HitDto toHitDto(Hit hit) {
        return new HitDto(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getCreated()
        );
    }

    public static Hit toHit(HitDto hitDto) {
        return Hit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .created(hitDto.getCreated())
                .build();
    }
}
