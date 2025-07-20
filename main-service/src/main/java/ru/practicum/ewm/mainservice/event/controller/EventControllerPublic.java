package ru.practicum.ewm.mainservice.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.event.dto.EventFullDto;
import ru.practicum.ewm.mainservice.event.dto.EventShortDto;
import ru.practicum.ewm.mainservice.event.dto.PublicEventFilter;
import ru.practicum.ewm.mainservice.event.service.EventService;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/events")
@Validated
public class EventControllerPublic {
    private final EventService eventService;

    @GetMapping("/{id}")
    public EventFullDto find(@PathVariable Long id,
                             HttpServletRequest request) {


        return eventService.findByPublicUser(id, request.getRemoteAddr(), request.getRequestURI());
    }

    @GetMapping
    public Collection<EventShortDto> findAll(@ModelAttribute @Valid PublicEventFilter filter,
                                             HttpServletRequest request
    ) {

        return eventService.findAllByPublicUser(filter, request.getRemoteAddr(), request.getRequestURI());
    }
}
