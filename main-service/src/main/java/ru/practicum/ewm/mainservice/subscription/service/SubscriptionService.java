package ru.practicum.ewm.mainservice.subscription.service;

import ru.practicum.ewm.mainservice.advice.enums.EventSort;
import ru.practicum.ewm.mainservice.event.dto.EventShortDto;

import java.util.Collection;

public interface SubscriptionService {
    void subscribe(Long userId, Long targetId);

    void unsubscribe(Long userId, Long targetId);

    void removeSubscriber(Long userId, Long subscriberId);

    Collection<EventShortDto> findEventsFromTarget(Long userId, Long targetId);

    Collection<EventShortDto> findAll(Long userId, EventSort sort, Integer from, Integer size);
}
