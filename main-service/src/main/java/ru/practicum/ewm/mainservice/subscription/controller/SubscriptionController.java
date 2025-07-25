package ru.practicum.ewm.mainservice.subscription.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.advice.enums.EventSort;
import ru.practicum.ewm.mainservice.event.dto.EventShortDto;
import ru.practicum.ewm.mainservice.subscription.service.SubscriptionService;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/subscriptions")
@Validated
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/{targetId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable Long userId,
                       @PathVariable Long targetId) {
        subscriptionService.subscribe(userId, targetId);
    }

    @DeleteMapping("/{targetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId,
                       @PathVariable Long targetId) {
        subscriptionService.unsubscribe(userId, targetId);
    }

    @DeleteMapping("/{subscriberId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSubscriber(@PathVariable Long userId,
                       @PathVariable Long subscriberId) {
        subscriptionService.removeSubscriber(userId, subscriberId);
    }

    @GetMapping("/{targetId}")
    public Collection<EventShortDto> getByTarget(@PathVariable Long userId, @PathVariable Long targetId) {
        return subscriptionService.findEventsFromTarget(userId, targetId);
    }

    @GetMapping
    public Collection<EventShortDto> findAll(@PathVariable Long userId,
                                             @RequestParam(name = "sort", required = false) EventSort sort,
                                             @PositiveOrZero @RequestParam(name = "from", required = false,
                                                     defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", required = false,
                                                     defaultValue = "10") Integer size) {
        return subscriptionService.findAll(userId, sort, from, size);
    }
}
