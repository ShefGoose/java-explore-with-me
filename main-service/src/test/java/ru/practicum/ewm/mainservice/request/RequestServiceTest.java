package ru.practicum.ewm.mainservice.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.mainservice.advice.enums.EventState;
import ru.practicum.ewm.mainservice.advice.enums.RequestStatus;
import ru.practicum.ewm.mainservice.advice.exception.EventConflictException;
import ru.practicum.ewm.mainservice.event.model.Event;
import ru.practicum.ewm.mainservice.event.repository.EventRepository;
import ru.practicum.ewm.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.mainservice.request.model.Request;
import ru.practicum.ewm.mainservice.request.repository.RequestRepository;
import ru.practicum.ewm.mainservice.request.service.RequestServiceImp;
import ru.practicum.ewm.mainservice.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private RequestServiceImp requestService;

    @Test
    void createDuplicateRequestsConflict() {
        long userId = 10L;
        long eventId = 20L;

        Event event = Event.builder()
                .id(eventId)
                .initiator(User.builder().id(99L).build())   // другой инициатор
                .state(EventState.PUBLISHED)
                .participantLimit(0L)
                .confirmedRequests(0L)
                .requestModeration(true)
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(requestRepository.existsByEventIdAndRequesterId(eventId, userId))
                .thenReturn(true);   // ← уже есть запрос

        assertThrows(EventConflictException.class,
                () -> requestService.create(userId, eventId));
    }

    @Test
    void cancelRequestAlreadyCanceledConflict() {

        long userId = 11L;
        long requestId = 100L;

        Request canceled = Request.builder()
                .id(requestId)
                .requester(User.builder().id(userId).build())
                .status(RequestStatus.CANCELED)
                .created(LocalDateTime.now())
                .build();

        when(requestRepository.findByIdAndRequesterId(requestId, userId))
                .thenReturn(Optional.of(canceled));

        assertThrows(EventConflictException.class,
                () -> requestService.cancelRequest(userId, requestId));
    }

    @Test
    void createAutoConfirmedWhenNoModeration() {
        long userId = 10;
        long eventId = 20;

        Event event = Event.builder()
                .id(eventId)
                .initiator(User.builder().id(99L).build())
                .state(EventState.PUBLISHED)
                .participantLimit(0L)
                .confirmedRequests(0L)
                .requestModeration(false)
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(requestRepository.existsByEventIdAndRequesterId(eventId, userId)).thenReturn(false);
        when(requestRepository.save(any())).thenAnswer(a -> a.getArgument(0));

        ParticipationRequestDto dto = requestService.create(userId, eventId);

        assertEquals(RequestStatus.CONFIRMED, dto.getStatus());
    }
}
