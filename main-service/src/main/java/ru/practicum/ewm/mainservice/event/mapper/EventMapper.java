package ru.practicum.ewm.mainservice.event.mapper;

import ru.practicum.ewm.mainservice.advice.enums.EventState;
import ru.practicum.ewm.mainservice.category.dto.CategoryMapper;
import ru.practicum.ewm.mainservice.category.model.Category;
import ru.practicum.ewm.mainservice.event.dto.EventFullDto;
import ru.practicum.ewm.mainservice.event.dto.EventShortDto;
import ru.practicum.ewm.mainservice.event.dto.NewEventDto;
import ru.practicum.ewm.mainservice.event.model.Event;
import ru.practicum.ewm.mainservice.user.dto.UserMapper;
import ru.practicum.ewm.mainservice.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

public class EventMapper {
    public static EventFullDto toEventFullDto(Event event, long views) {
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getDescription(),
                event.getEventDate(),
                LocationMapper.toLocationDto(event.getLocation()),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getPaid(),
                event.getRequestModeration(),
                event.getParticipantLimit(),
                UserMapper.toUserShortDto(event.getInitiator()),
                views,
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getPublishedOn(),
                event.getState()
        );
    }

    public static EventFullDto toEventFullDto(Event event) {
        return toEventFullDto(event, 0L);
    }

    public static EventShortDto toEventShortDto(Event event, long views) {
        return new EventShortDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getEventDate(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getPaid(),
                UserMapper.toUserShortDto(event.getInitiator()),
                views,
                event.getConfirmedRequests()
        );
    }

    public static Event toEvent(NewEventDto newEventDto, User initiator, Category category) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(LocationMapper.toLocation(newEventDto.getLocation()))
                .createdOn(LocalDateTime.now())
                .state(EventState.PENDING)
                .confirmedRequests(0L)

                .category(category)
                .initiator(initiator)

                .requestModeration(newEventDto.getRequestModeration() == null
                || newEventDto.getRequestModeration())
                .participantLimit(Optional.ofNullable(newEventDto.getParticipantLimit()).orElse(0L))
                .paid(Boolean.TRUE.equals(newEventDto.getPaid()))

                .build();
    }
}
