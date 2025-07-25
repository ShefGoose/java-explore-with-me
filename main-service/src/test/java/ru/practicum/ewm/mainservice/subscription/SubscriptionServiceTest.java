package ru.practicum.ewm.mainservice.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.mainservice.advice.enums.EventState;
import ru.practicum.ewm.mainservice.advice.exception.AccessDeniedException;
import ru.practicum.ewm.mainservice.advice.exception.EntityNotFoundException;
import ru.practicum.ewm.mainservice.advice.exception.SubscribeConflictException;
import ru.practicum.ewm.mainservice.category.model.Category;
import ru.practicum.ewm.mainservice.event.dto.EventShortDto;
import ru.practicum.ewm.mainservice.event.model.Event;
import ru.practicum.ewm.mainservice.event.model.Location;
import ru.practicum.ewm.mainservice.event.repository.EventRepository;
import ru.practicum.ewm.mainservice.stats.StatsService;
import ru.practicum.ewm.mainservice.subscription.model.Subscription;
import ru.practicum.ewm.mainservice.subscription.model.SubscriptionId;
import ru.practicum.ewm.mainservice.subscription.repository.SubscriptionRepository;
import ru.practicum.ewm.mainservice.subscription.service.SubscriptionServiceImp;
import ru.practicum.ewm.mainservice.user.model.User;
import ru.practicum.ewm.mainservice.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private StatsService statsService;

    @InjectMocks
    private SubscriptionServiceImp subscriptionService;

    @Test
    void subscribeShouldSaveSubscriptionWhenValid() {
        Long userId = 1L, targetId = 2L;

        when(userRepository.existsById(targetId)).thenReturn(true);
        when(subscriptionRepository.existsById(new SubscriptionId(userId, targetId))).thenReturn(false);

        subscriptionService.subscribe(userId, targetId);

        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void subscribeShouldThrowWhenSelfSubscribe() {
        Long userId = 1L;

        SubscribeConflictException ex = assertThrows(SubscribeConflictException.class,
                () -> subscriptionService.subscribe(userId, userId));

        assertEquals("Нельзя подписаться на самого себя", ex.getMessage());
    }

    @Test
    void subscribeShouldThrowWhenTargetNotFound() {
        Long userId = 1L, targetId = 2L;

        when(userRepository.existsById(targetId)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> subscriptionService.subscribe(userId, targetId));

        assertTrue(ex.getMessage().contains("TargetUser with id="));
    }

    @Test
    void subscribeShouldThrowWhenAlreadySubscribed() {
        Long userId = 1L, targetId = 2L;

        when(userRepository.existsById(targetId)).thenReturn(true);
        when(subscriptionRepository.existsById(new SubscriptionId(userId, targetId))).thenReturn(true);

        SubscribeConflictException ex = assertThrows(SubscribeConflictException.class,
                () -> subscriptionService.subscribe(userId, targetId));

        assertEquals("Подписка уже оформлена", ex.getMessage());
    }

    @Test
    void unsubscribeShouldDeleteWhenValid() {
        Long userId = 1L, targetId = 2L;

        subscriptionService.unsubscribe(userId, targetId);

        verify(subscriptionRepository).deleteById(new SubscriptionId(userId, targetId));
    }

    @Test
    void unsubscribeShouldThrowWhenSelfUnsubscribe() {
        Long userId = 1L;

        SubscribeConflictException ex = assertThrows(SubscribeConflictException.class,
                () -> subscriptionService.unsubscribe(userId, userId));

        assertEquals("Нельзя отписаться от самого себя", ex.getMessage());
    }

    @Test
    void removeSubscriberShouldDeleteWhenSubscribed() {
        Long userId = 2L, subscriberId = 1L;

        subscriptionService.removeSubscriber(userId, subscriberId);

        verify(subscriptionRepository).deleteById(new SubscriptionId(subscriberId, userId));
    }

    @Test
    void removeSubscriberShouldThrowWhenSelfRemove() {
        Long userId = 1L;

        SubscribeConflictException ex = assertThrows(SubscribeConflictException.class,
                () -> subscriptionService.removeSubscriber(userId, userId));

        assertEquals("Нельзя удалить из подписчиков самого себя", ex.getMessage());
    }

    @Test
    void findEventsFromTargetShouldReturnEvents() {
        Long userId = 1L;

        Category category = Category.builder().id(10L).name("test").build();
        User target = User.builder().id(20L).name("testN").email("test@gmail.com").build();
        Location loc = Location.builder().lat(0.0).lon(0.0).build();

        Event event = Event.builder()
                .id(2L)
                .title("free")
                .annotation("a2")
                .description("d2")
                .participantLimit(5L)
                .confirmedRequests(0L)
                .state(EventState.PUBLISHED)
                .eventDate(LocalDateTime.now().plusDays(20))
                .category(category)
                .initiator(target)
                .location(loc)
                .paid(false)
                .build();

        when(subscriptionRepository.existsById(new SubscriptionId(userId, target.getId()))).thenReturn(true);
        when(eventRepository.findAllPublishedByInitiatorId(target.getId())).thenReturn(List.of(event));
        when(statsService.buildViewsMapForPublished(any())).thenReturn(Collections.emptyMap());

        Collection<EventShortDto> result = subscriptionService.findEventsFromTarget(userId, target.getId());

        assertEquals(1, result.size());
        assertEquals(2L, result.iterator().next().getId());
    }

    @Test
    void findEventsFromTargetShouldThrowWhenSelfRequest() {
        Long userId = 1L;

        SubscribeConflictException ex = assertThrows(SubscribeConflictException.class,
                () -> subscriptionService.findEventsFromTarget(userId, userId));

        assertEquals("Нельзя запрашивать события от самого себя", ex.getMessage());
    }

    @Test
    void findAllShouldReturnEmptyWhenNoTargets() {
        when(subscriptionRepository.findAllBySubscriberId(1L)).thenReturn(List.of());

        Collection<EventShortDto> result = subscriptionService.findAll(1L, null, 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void findEventsFromTargetShouldThrowWhenNotSubscribed() {
        Long userId = 1L, targetId = 2L;

        when(subscriptionRepository.existsById(new SubscriptionId(userId, targetId))).thenReturn(false);

        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> subscriptionService.findEventsFromTarget(userId, targetId));

        assertTrue(ex.getMessage().contains("не подписаны"));
    }
}
