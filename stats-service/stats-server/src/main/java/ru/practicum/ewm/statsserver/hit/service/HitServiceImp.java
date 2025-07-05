package ru.practicum.ewm.statsserver.hit.service;

import lombok.AllArgsConstructor;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.statsdto.HitDto;
import ru.practicum.ewm.statsdto.StatsDto;
import ru.practicum.ewm.statsserver.advice.exception.BadDateException;
import ru.practicum.ewm.statsserver.advice.exception.BadIpException;
import ru.practicum.ewm.statsserver.hit.mapper.HitMapper;
import ru.practicum.ewm.statsserver.hit.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class HitServiceImp implements HitService {
    private final HitRepository hitRepository;

    @Override
    public HitDto create(HitDto hitDto) {
        if (!InetAddressValidator.getInstance().isValid(hitDto.getIp())) {
            throw new BadIpException("Неверный формат IP-адреса " + hitDto.getIp());
        }
        if (hitDto.getCreated() == null) {
            hitDto.setCreated(LocalDateTime.now());
        }
        return HitMapper.toHitDto(hitRepository.save(HitMapper.toHit(hitDto)));
    }

    @Override
    public Collection<StatsDto> findAll(LocalDateTime start, LocalDateTime end,
                                        List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new BadDateException("Start не может быть позже end");
        }

        return unique
                ? hitRepository.getStatsUnique(start, end, uris)
                : hitRepository.getStats(start, end, uris);
    }
}
