package ru.practicum.ewm.mainservice.event.service;

import ru.practicum.ewm.mainservice.event.dto.*;
import ru.practicum.ewm.mainservice.request.dto.ParticipationRequestDto;

import java.util.Collection;

public interface EventService {
    EventFullDto create(Long userId, NewEventDto newEventDto);

    EventFullDto findByInitiator(Long userId, Long eventId);

    Collection<EventShortDto> findAllByInitiator(Long userId, Integer from, Integer size);

    EventFullDto updateInitiator(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    Collection<ParticipationRequestDto> findRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId,
                                                  EventRequestStatusUpdateRequest updateRequest);

    EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    Collection<EventFullDto> findAllByAdmin(AdminEventFilter filter);

    EventFullDto findByPublicUser(Long eventId, String ip, String uri);

    Collection<EventShortDto> findAllByPublicUser(PublicEventFilter filter, String ip, String uri);
}
