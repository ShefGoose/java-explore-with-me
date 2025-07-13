package ru.practicum.ewm.mainservice.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.advice.enums.EventSort;
import ru.practicum.ewm.mainservice.event.dto.EventFullDto;
import ru.practicum.ewm.mainservice.event.dto.EventShortDto;
import ru.practicum.ewm.mainservice.event.service.EventService;

import java.util.Collection;
import java.util.List;

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
    @Validated
    public Collection<EventShortDto> findAll(@RequestParam(name = "text", required = false) String text,
                                             @RequestParam(name = "categories", required = false) List<Long> categories,
                                             @RequestParam(name = "paid", required = false) Boolean paid,
                                             @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                             @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                             @RequestParam(name = "onlyAvailable", required = false)
                                             Boolean onlyAvailable,
                                             @RequestParam(name = "sort", required = false) EventSort sort,
                                             @PositiveOrZero @RequestParam(name = "from", required = false,
                                                     defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", required = false,
                                                     defaultValue = "10") Integer size,
                                             HttpServletRequest request
    ) {

        return eventService.findAllByPublicUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, request.getRemoteAddr(), request.getRequestURI());
    }
}
