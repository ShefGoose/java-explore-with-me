package ru.practicum.ewm.mainservice.subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.advice.Pagination;
import ru.practicum.ewm.mainservice.advice.enums.EventSort;
import ru.practicum.ewm.mainservice.advice.exception.AccessDeniedException;
import ru.practicum.ewm.mainservice.advice.exception.EntityNotFoundException;
import ru.practicum.ewm.mainservice.advice.exception.SubscribeConflictException;
import ru.practicum.ewm.mainservice.event.dto.EventShortDto;
import ru.practicum.ewm.mainservice.event.mapper.EventMapper;
import ru.practicum.ewm.mainservice.event.model.Event;
import ru.practicum.ewm.mainservice.event.repository.EventRepository;
import ru.practicum.ewm.mainservice.stats.StatsService;
import ru.practicum.ewm.mainservice.subscription.model.Subscription;
import ru.practicum.ewm.mainservice.subscription.model.SubscriptionId;
import ru.practicum.ewm.mainservice.subscription.repository.SubscriptionRepository;
import ru.practicum.ewm.mainservice.user.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImp implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final StatsService statsService;

    @Override
    public void subscribe(Long userId, Long targetId) {
        if (userId.equals(targetId)) {
            throw new SubscribeConflictException("Нельзя подписаться на самого себя");
        }

        if (!userRepository.existsById(targetId)) {
            throw new EntityNotFoundException("TargetUser with id=" + targetId + " was not found");
        }

        if (subscriptionRepository.existsById(new SubscriptionId(userId, targetId))) {
            throw new SubscribeConflictException("Подписка уже оформлена");
        }

        subscriptionRepository.save(Subscription.builder()
                .subscriberId(userId)
                .targetId(targetId)
                .build());
    }

    @Override
    public void unsubscribe(Long userId, Long targetId) {
        if (userId.equals(targetId)) {
            throw new SubscribeConflictException("Нельзя отписаться от самого себя");
        }

        subscriptionRepository.deleteById(new SubscriptionId(userId, targetId));
    }

    @Override
    public void removeSubscriber(Long userId, Long subscriberId) {
        if (userId.equals(subscriberId)) {
            throw new SubscribeConflictException("Нельзя удалить из подписчиков самого себя");
        }

        subscriptionRepository.deleteById(new SubscriptionId(subscriberId, userId));
    }

    @Override
    public Collection<EventShortDto> findEventsFromTarget(Long userId, Long targetId) {
        if (userId.equals(targetId)) {
            throw new SubscribeConflictException("Нельзя запрашивать события от самого себя");
        }

        if (!subscriptionRepository.existsById(new SubscriptionId(userId, targetId))) {
            throw new AccessDeniedException("Вы не подписаны на пользователя с id= " + targetId);
        }

        Collection<Event> events = eventRepository.findAllPublishedByInitiatorId(targetId);

        Map<String, Long> views =
                events.isEmpty()
                        ? Collections.emptyMap()
                        : statsService.buildViewsMapForPublished(events);

        return events.stream()
                .map(e -> EventMapper.toEventShortDto(e,
                        views.getOrDefault("/events/" + e.getId(), 0L)))
                .toList();
    }

    @Override
    public Collection<EventShortDto> findAll(Long userId, EventSort sort, Integer from, Integer size) {

        Collection<Long> targetIds = subscriptionRepository.findAllTargetIdBySubscriberId(userId);

        if (targetIds.isEmpty()) {
            return List.of();
        }

        Sort sortEvents;
        if (sort == EventSort.EVENT_DATE) {
            sortEvents = Sort.by("eventDate").ascending();
        } else {
            sortEvents = Sort.unsorted();
        }

        PageRequest pageRequest = Pagination.makePageRequest(from, size);
        Collection<Event> events = eventRepository.findAllPublishedByInitiatorIdIn(targetIds,
                Objects.requireNonNullElseGet(pageRequest,
                        () -> PageRequest.of(0, Integer.MAX_VALUE, sortEvents))).getContent();

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

        return shortEvents;
    }
}
