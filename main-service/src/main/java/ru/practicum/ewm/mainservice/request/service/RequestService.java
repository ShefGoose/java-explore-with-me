package ru.practicum.ewm.mainservice.request.service;

import ru.practicum.ewm.mainservice.request.dto.ParticipationRequestDto;

import java.util.Collection;

public interface RequestService {
    ParticipationRequestDto create(Long userId, Long eventId);

    Collection<ParticipationRequestDto> findAll(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
