package ru.practicum.ewm.mainservice.compilation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.mainservice.advice.exception.EntityNotFoundException;
import ru.practicum.ewm.mainservice.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.mainservice.compilation.model.Compilation;
import ru.practicum.ewm.mainservice.compilation.repository.CompilationRepository;
import ru.practicum.ewm.mainservice.compilation.service.CompilationServiceImp;
import ru.practicum.ewm.mainservice.event.model.Event;
import ru.practicum.ewm.mainservice.event.repository.EventRepository;
import ru.practicum.ewm.mainservice.stats.StatsService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompilationServiceTest {
    @Mock
    private CompilationRepository compilationRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private StatsService statsService;
    @InjectMocks
    private CompilationServiceImp compilationService;

    @Test
    void updateWithEmptyEventsShouldClearCompilation() {
        Compilation comp = Compilation.builder()
                .id(1L)
                .title("c1")
                .pinned(false)
                .events(new HashSet<>(Set.of(Event.builder().id(100L).build())))
                .build();

        when(compilationRepository.findById(1L)).thenReturn(Optional.of(comp));
        when(compilationRepository.save(any())).thenReturn(comp);

        UpdateCompilationRequest dto = new UpdateCompilationRequest();
        dto.setEvents(Set.of());

        compilationService.update(1L, dto);

        assertTrue(comp.getEvents().isEmpty());
    }

    @Test
    void updateWithoutPinnedKeepOldValue() {
        Compilation comp = Compilation.builder()
                .id(2L)
                .title("old")
                .pinned(true)
                .events(new HashSet<>())
                .build();

        when(compilationRepository.findById(2L)).thenReturn(Optional.of(comp));
        when(compilationRepository.save(any())).thenReturn(comp);

        UpdateCompilationRequest dto = new UpdateCompilationRequest();
        dto.setTitle("new");

        compilationService.update(2L, dto);

        assertTrue(comp.getPinned());
        assertEquals("new", comp.getTitle());
    }

    @Test
    void updateCompilationWithEventNotFound() {
        Compilation comp = Compilation.builder()
                .id(3L)
                .title("com")
                .pinned(false)
                .events(new HashSet<>())
                .build();

        when(compilationRepository.findById(3L)).thenReturn(Optional.of(comp));
        when(eventRepository.findAllByIdIn(Set.of(55L))).thenReturn(Collections.emptyList());

        UpdateCompilationRequest dto = new UpdateCompilationRequest();
        dto.setEvents(Set.of(55L));

        assertThrows(EntityNotFoundException.class,
                () -> compilationService.update(3L, dto));
    }
}

