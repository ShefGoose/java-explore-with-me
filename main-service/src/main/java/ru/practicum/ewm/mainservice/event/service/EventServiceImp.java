package ru.practicum.ewm.mainservice.event.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.advice.Pagination;
import ru.practicum.ewm.mainservice.advice.enums.EventSort;
import ru.practicum.ewm.mainservice.advice.enums.EventState;
import ru.practicum.ewm.mainservice.advice.enums.RequestStatus;
import ru.practicum.ewm.mainservice.advice.enums.UpdateRequestStatus;
import ru.practicum.ewm.mainservice.advice.exception.EntityNotFoundException;
import ru.practicum.ewm.mainservice.advice.exception.EventConflictException;
import ru.practicum.ewm.mainservice.category.model.Category;
import ru.practicum.ewm.mainservice.category.repository.CategoryRepository;
import ru.practicum.ewm.mainservice.event.dto.*;
import ru.practicum.ewm.mainservice.event.mapper.EventPatchMapper;
import ru.practicum.ewm.mainservice.event.mapper.EventMapper;
import ru.practicum.ewm.mainservice.event.mapper.EventRequestMapper;
import ru.practicum.ewm.mainservice.event.model.Event;
import ru.practicum.ewm.mainservice.event.repository.EventRepository;
import ru.practicum.ewm.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.mainservice.request.dto.RequestMapper;
import ru.practicum.ewm.mainservice.request.model.Request;
import ru.practicum.ewm.mainservice.request.repository.RequestRepository;
import ru.practicum.ewm.mainservice.stats.StatsService;
import ru.practicum.ewm.mainservice.user.model.User;
import ru.practicum.ewm.mainservice.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@AllArgsConstructor
public class EventServiceImp implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventPatchMapper eventPatchMapper;
    private final StatsService statsService;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " was not found"));
        Category category = categoryRepository.findById(newEventDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" +
                        newEventDto.getCategoryId() + " was not found"));

        Event event = EventMapper.toEvent(newEventDto, initiator, category);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto findByInitiator(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" +
                        eventId + " was not found"));

        long views = 0L;
        if (event.getPublishedOn() != null) {
            views = statsService.getViewsEvent(event);
        }

        return EventMapper.toEventFullDto(event, views);
    }

    @Override
    public Collection<EventShortDto> findAllByInitiator(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = Pagination.makePageRequest(from, size);
        Page<Event> eventsPage;
        Pageable pageable = Objects.requireNonNullElseGet(pageRequest,
                () -> PageRequest.of(0, Integer.MAX_VALUE));
        eventsPage = eventRepository.findAllByInitiatorId(userId, pageable);
        List<Event> events = eventsPage.getContent();

        Map<String, Long> views =
                events.isEmpty()
                        ? Collections.emptyMap()
                        : statsService.buildViewsMapForPublished(events);

        return events.stream()
                .map(e -> EventMapper.toEventShortDto(
                        e,
                        views.getOrDefault("/events/" + e.getId(), 0L)
                ))
                .toList();
    }

    @Override
    public EventFullDto updateInitiator(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" +
                        eventId + " was not found"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new EventConflictException("Only pending or canceled events can be changed");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventConflictException("дата и время на которые намечено событие не может быть раньше," +
                    " чем через два часа от текущего момента");
        }

        if (updateEventUserRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category with id=" +
                            updateEventUserRequest.getCategoryId() + " was not found"));
            event.setCategory(category);
        }

        eventPatchMapper.updateEventFromDto(updateEventUserRequest, event);

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            }
        }

        eventRepository.save(event);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public Collection<ParticipationRequestDto> findRequests(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" +
                        eventId + " was not found"));

        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId,
                                                         EventRequestStatusUpdateRequest updateRequest) {

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" +
                        eventId + " was not found"));

        if (event.getRequestModeration().equals(Boolean.FALSE) || event.getParticipantLimit() == 0) {
            throw new EventConflictException("Подтверждение заявок не требуется");
        }

        if (event.getParticipantLimit().equals(event.getConfirmedRequests()) &&
                updateRequest.getStatus() == UpdateRequestStatus.CONFIRMED) {
            throw new EventConflictException("Достигнут лимит по заявкам на данное событие");
        }

        Collection<Request> requestsUpdate = requestRepository.findAllByIdInAndStatus(updateRequest.getRequestIds(),
                RequestStatus.PENDING);

        if (requestsUpdate.size() != updateRequest.getRequestIds().size()) {
            throw new EventConflictException("статус можно изменить только у заявок, находящихся в состоянии ожидания");
        }

        long freeSlots = event.getParticipantLimit() - event.getConfirmedRequests();

        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        if (updateRequest.getStatus() == UpdateRequestStatus.CONFIRMED) {
            for (Request r : requestsUpdate) {
                if (freeSlots > 0) {
                    r.setStatus(RequestStatus.CONFIRMED);
                    confirmed.add(r);
                    freeSlots--;
                } else {
                    r.setStatus(RequestStatus.REJECTED);
                    rejected.add(r);
                }
            }

            if (freeSlots == 0) {
                int n = requestRepository.rejectAllPendingByEventId(eventId);
            }

            event.setConfirmedRequests(event.getConfirmedRequests() + confirmed.size());
        } else {
            requestsUpdate.forEach(r -> r.setStatus(RequestStatus.REJECTED));
            rejected.addAll(requestsUpdate);
        }

        requestRepository.saveAll(requestsUpdate);
        eventRepository.save(event);

        return EventRequestMapper.toUpdateResult(confirmed, rejected);
    }


    @Override
    public EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" +
                        eventId + " was not found"));

        if (updateEventAdminRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category with id=" +
                            updateEventAdminRequest.getCategoryId() + " was not found"));
            event.setCategory(category);
        }

        eventPatchMapper.updateEventFromDto(updateEventAdminRequest, event);

        if (updateEventAdminRequest.getStateAction() != null) {
            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT -> publish(event);
                case REJECT_EVENT -> reject(event);
            }
        }

        eventRepository.save(event);

        long views = 0L;
        if (event.getState() == EventState.PUBLISHED && event.getPublishedOn() != null) {
            views = statsService.getViewsEvent(event);
        }

        return EventMapper.toEventFullDto(event, views);
    }

    @Override
    public Collection<EventFullDto> findAllByAdmin(List<Long> users, List<String> states,
                                                   List<Long> categories, String rangeStart,
                                                   String rangeEnd, Integer from, Integer size) {
        LocalDateTime start = rangeStart == null ? null
                : LocalDateTime.parse(rangeStart, FMT);
        LocalDateTime end = rangeEnd == null ? null
                : LocalDateTime.parse(rangeEnd, FMT);

        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidationException("rangeStart должен быть раньше rangeEnd");
        }

        List<EventState> stateEnums;
        if (states != null && !states.isEmpty()) {
            stateEnums = states.stream()
                    .map(EventState::valueOf)
                    .toList();
        } else {
            stateEnums = null;
        }

        Specification<Event> spec = Specification.where(null);

        if (users != null && !users.isEmpty()) {
            spec = spec.and((r, q, cb) -> r.get("initiator").get("id").in(users));
        }

        if (stateEnums != null && !stateEnums.isEmpty()) {
            spec = spec.and((r, q, cb) -> r.get("state").in(stateEnums));
        }

        if (categories != null && !categories.isEmpty()) {
            spec = spec.and((r, q, cb) -> r.get("category").get("id").in(categories));
        }

        if (start != null) {
            spec = spec.and((r, q, cb) -> cb.greaterThanOrEqualTo(r.get("eventDate"), start));
        }

        if (end != null) {
            spec = spec.and((r, q, cb) -> cb.lessThanOrEqualTo(r.get("eventDate"), end));
        }

        PageRequest pageRequest = Pagination.makePageRequest(from, size);
        Page<Event> eventsPage;
        Pageable pageable = Objects.requireNonNullElseGet(pageRequest,
                () -> PageRequest.of(0, Integer.MAX_VALUE));
        eventsPage = eventRepository.findAll(spec, pageable);
        List<Event> events = eventsPage.getContent();

        Map<String, Long> views =
                events.isEmpty()
                        ? Collections.emptyMap()
                        : statsService.buildViewsMapForPublished(events);

        return events.stream()
                .map(e -> EventMapper.toEventFullDto(
                        e,
                        views.getOrDefault("/events/" + e.getId(), 0L)
                ))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto findByPublicUser(Long eventId, String ip, String uri) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" +
                        eventId + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new EntityNotFoundException("Event with id=" +
                    eventId + " was not found");
        }

        long views = statsService.getViewsEventByUniqueIp(event);

        statsService.addHit(uri, ip);

        return EventMapper.toEventFullDto(event, views);
    }

    @Override
    public Collection<EventShortDto> findAllByPublicUser(String text, List<Long> categories, Boolean paid,
                                                         String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                         EventSort sort, Integer from, Integer size, String ip,
                                                         String uri) {
        LocalDateTime start = rangeStart == null
                ? LocalDateTime.now()
                : LocalDateTime.parse(rangeStart, FMT);
        LocalDateTime end = rangeEnd == null
                ? null
                : LocalDateTime.parse(rangeEnd, FMT);

        if (end != null && start.isAfter(end)) {
            throw new ValidationException("rangeStart должен быть раньше rangeEnd");
        }

        Specification<Event> spec = Specification
                .<Event>where((r, q, cb) -> cb.equal(r.get("state"), EventState.PUBLISHED))

                .and((r, q, cb) -> end == null
                        ? cb.greaterThanOrEqualTo(r.get("eventDate"), start)
                        : cb.between(r.get("eventDate"), start, end));

        if (text != null && !text.isBlank()) {
            String pattern = "%" + text.toLowerCase() + "%";
            spec = spec.and((r, q, cb) -> cb.or(
                    cb.like(cb.lower(r.get("annotation")), pattern),
                    cb.like(cb.lower(r.get("description")), pattern)));
        }

        if (categories != null && !categories.isEmpty()) {
            spec = spec.and((r, q, cb) -> r.get("category").get("id").in(categories));
        }

        if (paid != null) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("paid"), paid));
        }

        if (Boolean.TRUE.equals(onlyAvailable)) {
            spec = spec.and((r, q, cb) -> cb.or(
                    cb.equal(r.get("participantLimit"), 0),
                    cb.lessThan(r.get("confirmedRequests"), r.get("participantLimit"))));
        }

        Sort sortEvents;
        if (sort == EventSort.EVENT_DATE) {
            sortEvents = Sort.by("eventDate").ascending();
        } else {
            sortEvents = Sort.unsorted();
        }

        PageRequest pageRequest = Pagination.makePageRequest(from, size);
        Page<Event> eventsPage;
        Pageable pageable = Objects.requireNonNullElseGet(pageRequest,
                () -> PageRequest.of(0, Integer.MAX_VALUE, sortEvents));
        eventsPage = eventRepository.findAll(spec, pageable);
        List<Event> events = eventsPage.getContent();

        Map<String, Long> views =
                events.isEmpty()
                        ? Collections.emptyMap()
                        : statsService.buildViewsMapForPublished(events);

        List<EventShortDto> shortEvents = events.stream()
                .map(e -> EventMapper.toEventShortDto(e,
                        views.getOrDefault("/events/" + e.getId(), 0L)))
                .toList();

        if (sort == EventSort.VIEWS) {
            shortEvents = shortEvents.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                    .toList();
        }

        statsService.addHit(uri, ip);

        return shortEvents;
    }

    private void publish(Event event) {
        if (event.getState() != EventState.PENDING) {
            throw new EventConflictException("Событие не в состоянии ожидания к публикации");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new EventConflictException("Дата начала события должна быть не ранее чем за час от даты публикации");
        }

        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
    }

    private void reject(Event event) {
        if (event.getState() == EventState.PUBLISHED) {
            throw new EventConflictException("Опубликованое событие не может быть отклонено");
        }
        event.setState(EventState.CANCELED);
    }
}
