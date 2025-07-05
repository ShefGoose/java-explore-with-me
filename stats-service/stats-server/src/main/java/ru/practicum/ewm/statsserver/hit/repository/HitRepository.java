package ru.practicum.ewm.statsserver.hit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.statsdto.StatsDto;
import ru.practicum.ewm.statsserver.hit.model.Hit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {
    @Query("""
            SELECT new ru.practicum.ewm.statsdto.StatsDto(h.app, h.uri, COUNT(h)) as stats
            FROM Hit h
            WHERE h.created BETWEEN :start AND :end
            AND (:uris IS NULL OR h.uri IN :uris)
            GROUP BY h.app, h.uri
            ORDER BY COUNT(h) DESC
            """)
    Collection<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("""
            SELECT new ru.practicum.ewm.statsdto.StatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) as stats
                        FROM Hit h
                        WHERE h.created BETWEEN :start AND :end
                        AND (:uris IS NULL OR h.uri IN :uris)
                        GROUP BY h.app, h.uri
                        ORDER BY COUNT(DISTINCT h.ip) DESC
            """)
    Collection<StatsDto> getStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}
