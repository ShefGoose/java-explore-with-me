package ru.practicum.ewm.mainservice.stats;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.statsclient.StatsClient;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class StatsServiceTest {
    @Mock
    private StatsClient statsClient;
    @InjectMocks
    private StatsService statsService;

    @Test
    void emptyEventsShouldReturnsEmptyMap() {
        assertTrue(statsService.buildViewsMapForPublished(Set.of()).isEmpty());
    }
}
