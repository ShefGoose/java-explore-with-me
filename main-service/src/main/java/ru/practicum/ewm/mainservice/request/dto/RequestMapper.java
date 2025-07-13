package ru.practicum.ewm.mainservice.request.dto;

import ru.practicum.ewm.mainservice.request.model.Request;

public class RequestMapper {
    public static ParticipationRequestDto toRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus(),
                request.getCreated());
    }
}
