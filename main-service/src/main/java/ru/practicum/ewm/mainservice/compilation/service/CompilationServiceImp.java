package ru.practicum.ewm.mainservice.compilation.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.advice.Pagination;
import ru.practicum.ewm.mainservice.advice.enums.EventState;
import ru.practicum.ewm.mainservice.advice.exception.EntityNotFoundException;
import ru.practicum.ewm.mainservice.compilation.dto.CompilationDto;
import ru.practicum.ewm.mainservice.compilation.dto.CompilationMapper;
import ru.practicum.ewm.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.mainservice.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.mainservice.compilation.model.Compilation;
import ru.practicum.ewm.mainservice.compilation.repository.CompilationRepository;
import ru.practicum.ewm.mainservice.event.dto.EventShortDto;
import ru.practicum.ewm.mainservice.event.mapper.EventMapper;
import ru.practicum.ewm.mainservice.event.model.Event;
import ru.practicum.ewm.mainservice.event.repository.EventRepository;
import ru.practicum.ewm.mainservice.stats.StatsService;

import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class CompilationServiceImp implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final StatsService statsService;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Set<Long> eventIds = Optional.ofNullable(newCompilationDto.getEvents())
                .orElse(Set.of());

        Collection<Event> events = eventRepository.findAllByIdIn(eventIds);

        if (events.size() != eventIds.size()) {
            throw new EntityNotFoundException("Одно или несколько событий не найдены");
        }

        Compilation compilation = compilationRepository.save(CompilationMapper
                .toCompilation(newCompilationDto, events));

        Map<String, Long> views =
                events.isEmpty()
                        ? Collections.emptyMap()
                        : statsService.buildViewsMapForPublished(events);

        List<EventShortDto> eventShortDtos = events.stream()
                .map(e -> EventMapper.toEventShortDto(
                        e,
                        views.getOrDefault("/events/" + e.getId(), 0L)
                ))
                .toList();

        return CompilationMapper.toCompilationDto(compilation, eventShortDtos);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest updateComp) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation with id=" + compId + " was not found"));

        if (updateComp.getTitle() != null) {
            compilation.setTitle(updateComp.getTitle());
        }

        if (updateComp.getPinned() != null) {
            compilation.setPinned(updateComp.getPinned());
        }

        if (updateComp.getEvents() != null) {
            if (updateComp.getEvents().isEmpty()) {
                compilation.getEvents().clear();
            } else {
                Collection<Event> newEvents = eventRepository.findAllByIdIn(updateComp.getEvents());

                if (newEvents.size() != updateComp.getEvents().size()) {
                    throw new EntityNotFoundException("Одно или несколько событий не найдены");
                }
                compilation.setEvents(new HashSet<>(newEvents));
            }
        }

        compilationRepository.save(compilation);

        Map<String, Long> views =
                compilation.getEvents().isEmpty()
                        ? Collections.emptyMap()
                        : statsService.buildViewsMapForPublished(compilation.getEvents());

        List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                .map(e -> EventMapper.toEventShortDto(
                        e,
                        views.getOrDefault("/events/" + e.getId(), 0L)
                ))
                .toList();

        return CompilationMapper.toCompilationDto(compilation, eventShortDtos);
    }

    @Override
    public void delete(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new EntityNotFoundException("Compilation with id=" + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto find(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation with id=" + compId + " was not found"));

        Collection<Event> events = eventRepository.findAllByIdInAndState(compilation.getEvents().stream()
                        .map(Event::getId)
                        .toList(),
                EventState.PUBLISHED);

        Map<String, Long> views =
                events.isEmpty()
                        ? Collections.emptyMap()
                        : statsService.buildViewsMapForPublished(events);

        List<EventShortDto> eventShortDtos = events.stream()
                .map(e -> EventMapper.toEventShortDto(
                        e,
                        views.getOrDefault("/events/" + e.getId(), 0L)
                ))
                .toList();

        return CompilationMapper.toCompilationDto(compilation, eventShortDtos);
    }

    @Override
    public Collection<CompilationDto> findAll(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = Pagination.makePageRequest(from, size);
        Page<Compilation> compilationsPage;
        Pageable pageable = Objects.requireNonNullElseGet(pageRequest,
                () -> PageRequest.of(0, Integer.MAX_VALUE));

        compilationsPage = (pinned == null)
                ? compilationRepository.findAll(pageable)
                : compilationRepository.findAllByPinned(pinned, pageable);

        List<Compilation> compilations = compilationsPage.getContent();

        Set<Event> publishedEvents = compilations.stream()
                .flatMap(c -> c.getEvents().stream())
                .filter(e -> e.getState() == EventState.PUBLISHED)
                .collect(Collectors.toSet());

        Map<String, Long> views =
                publishedEvents.isEmpty()
                        ? Collections.emptyMap()
                        : statsService.buildViewsMapForPublished(publishedEvents);

        return compilations.stream()
                .map(c -> {
                    List<EventShortDto> eventShortDtos = c.getEvents().stream()
                            .filter(e -> e.getState() == EventState.PUBLISHED)
                            .map(e -> EventMapper.toEventShortDto(e,
                                    views.getOrDefault("/events/" + e.getId(), 0L)))
                            .toList();
                    return CompilationMapper.toCompilationDto(c, eventShortDtos);
                })
                .toList();
    }
}
