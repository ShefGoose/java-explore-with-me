package ru.practicum.ewm.mainservice.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.mainservice.request.service.RequestService;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Validated
public class RequestController {
    private RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable Long userId,
                                          @RequestParam(name = "eventId") Long eventId) {
        return requestService.create(userId, eventId);
    }

    @GetMapping
    public Collection<ParticipationRequestDto> findAll(@PathVariable Long userId) {
        return requestService.findAll(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto update(@PathVariable Long userId,
                                          @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
