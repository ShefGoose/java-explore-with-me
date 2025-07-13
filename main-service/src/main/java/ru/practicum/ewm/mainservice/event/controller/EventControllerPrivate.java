package ru.practicum.ewm.mainservice.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.event.dto.*;
import ru.practicum.ewm.mainservice.event.service.EventService;
import ru.practicum.ewm.mainservice.request.dto.ParticipationRequestDto;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
public class EventControllerPrivate {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId,
                               @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.create(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto find(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.findByInitiator(userId, eventId);
    }

    @GetMapping
    @Validated
    public Collection<EventShortDto> findAll(@PathVariable Long userId,
                                             @PositiveOrZero @RequestParam(name = "from", required = false,
                                                     defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", required = false,
                                                     defaultValue = "10") Integer size) {
        return eventService.findAllByInitiator(userId, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long userId,
                                @PathVariable Long eventId,
                                @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateInitiator(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public Collection<ParticipationRequestDto> findRequests(@PathVariable Long userId,
                                                @PathVariable Long eventId) {
        return eventService.findRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(@PathVariable Long userId,
                                                         @PathVariable Long eventId,
                                                         @RequestBody @Valid
                                                             EventRequestStatusUpdateRequest updateRequest) {
        return eventService.updateRequests(userId, eventId, updateRequest);
    }
}
