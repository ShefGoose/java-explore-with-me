package ru.practicum.ewm.mainservice.event.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.event.dto.AdminEventFilter;
import ru.practicum.ewm.mainservice.event.dto.EventFullDto;
import ru.practicum.ewm.mainservice.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.mainservice.event.service.EventService;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/events")
public class EventControllerAdmin {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long eventId,
                               @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateAdmin(eventId, updateEventAdminRequest);
    }

    @GetMapping
    public Collection<EventFullDto> findAll(@ModelAttribute @Valid AdminEventFilter filter) {
        return eventService.findAllByAdmin(filter);
    }
}
