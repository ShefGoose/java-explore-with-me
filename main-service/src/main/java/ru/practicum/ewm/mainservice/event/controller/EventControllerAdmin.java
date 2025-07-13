package ru.practicum.ewm.mainservice.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.event.dto.EventFullDto;
import ru.practicum.ewm.mainservice.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.mainservice.event.service.EventService;

import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/events")
@Validated
public class EventControllerAdmin {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long eventId,
                               @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateAdmin(eventId, updateEventAdminRequest);
    }

    @GetMapping
    @Validated
    public Collection<EventFullDto> findAll(@RequestParam(name = "users", required = false) List<Long> users,
                                            @RequestParam(name = "states", required = false) List<String> states,
                                            @RequestParam(name = "categories", required = false)
                                                List<Long> categories,
                                            @RequestParam(name = "rangeStart", required = false)
                                                String rangeStart,
                                            @RequestParam(name = "rangeEnd", required = false)
                                                String rangeEnd,
                                            @PositiveOrZero @RequestParam(name = "from", required = false,
                                                    defaultValue = "0") Integer from,
                                            @Positive @RequestParam(name = "size", required = false,
                                                    defaultValue = "10") Integer size) {
        return eventService.findAllByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
