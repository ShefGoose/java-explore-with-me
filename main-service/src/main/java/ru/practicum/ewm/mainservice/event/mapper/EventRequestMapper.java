package ru.practicum.ewm.mainservice.event.mapper;

import ru.practicum.ewm.mainservice.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.mainservice.request.dto.RequestMapper;
import ru.practicum.ewm.mainservice.request.model.Request;

import java.util.List;

public class EventRequestMapper {
    public static EventRequestStatusUpdateResult toUpdateResult(List<Request> confirmed,
                                                                List<Request> rejected) {
        return new EventRequestStatusUpdateResult(confirmed.stream()
                .map(RequestMapper::toRequestDto)
                .toList(),
                rejected.stream()
                        .map(RequestMapper::toRequestDto)
                        .toList());
    }
}
