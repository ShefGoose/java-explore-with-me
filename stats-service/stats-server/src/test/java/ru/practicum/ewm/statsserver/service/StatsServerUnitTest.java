package ru.practicum.ewm.statsserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.statsdto.HitDto;
import ru.practicum.ewm.statsserver.advice.exception.BadDateException;
import ru.practicum.ewm.statsserver.advice.exception.BadIpException;
import ru.practicum.ewm.statsserver.hit.mapper.HitMapper;
import ru.practicum.ewm.statsserver.hit.model.Hit;
import ru.practicum.ewm.statsserver.hit.repository.HitRepository;
import ru.practicum.ewm.statsserver.hit.service.HitServiceImp;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatsServerUnitTest {
    @Mock
    private HitRepository hitRepository;
    private HitServiceImp hitService;
    private HitDto hitDto;
    private Hit hit;

    @BeforeEach
    void setUp() {
        hitService = new HitServiceImp(hitRepository);
        hitDto = new HitDto(null, "app", "/uri", "1.1.1.1",
                LocalDateTime.now().withNano(0));

        hit = HitMapper.toHit(hitDto);
        hit.setId(1L);
    }

    @Test
    void shouldCreateHit() {
        when(hitRepository.save(any())).thenReturn(hit);
        HitDto result = hitService.create(hitDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getApp()).isEqualTo("app");
        verify(hitRepository).save(any(Hit.class));
    }

    @Test
    void shouldCallUniqueRepoMethod() {
        hitService.findAll(LocalDateTime.MIN, LocalDateTime.MAX, null, true);
        verify(hitRepository).getStatsUnique(any(), any(), any());
        verify(hitRepository, never()).getStats(any(), any(), any());
    }

    @Test
    void testFindAllDateException() {
        assertThrows(BadDateException.class, () -> hitService.findAll(LocalDateTime.now().plusDays(1),
                LocalDateTime.now(), null, false));
    }

    @Test
    void testCreateIpException() {
        hitDto.setIp("999.999.1.1");
        assertThrows(BadIpException.class, () -> hitService.create(hitDto));
        verify(hitRepository, never()).save(any());
    }

    @Test
    void shouldSetCurrentTimeIfHitDtoHasNullDate() {
        hitDto.setCreated(null);
        when(hitRepository.save(any())).thenReturn(hit);
        HitDto result = hitService.create(hitDto);

        assertThat(result.getCreated())
                .isNotNull();
    }
}
