package ru.practicum.ewm.mainservice.event.service;

import ru.practicum.ewm.mainservice.advice.enums.EventSort;
import ru.practicum.ewm.mainservice.event.dto.*;
import ru.practicum.ewm.mainservice.request.dto.ParticipationRequestDto;

import java.util.Collection;
import java.util.List;

public interface EventService {
    EventFullDto create(Long userId, NewEventDto newEventDto);

    EventFullDto findByInitiator(Long userId, Long eventId);

    Collection<EventShortDto> findAllByInitiator(Long userId, Integer from, Integer size);

    EventFullDto updateInitiator(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    Collection<ParticipationRequestDto> findRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId,
                                                  EventRequestStatusUpdateRequest updateRequest);

    EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    Collection<EventFullDto> findAllByAdmin(List<Long> users, List<String> states,
                                            List<Long> categories, String rangeStart,
                                            String rangeEnd, Integer from, Integer size);

    EventFullDto findByPublicUser(Long eventId, String ip, String uri);

    Collection<EventShortDto> findAllByPublicUser(String text, List<Long> categories, Boolean paid,
                                                  String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                  EventSort sort, Integer from, Integer size, String ip, String uri);
}
