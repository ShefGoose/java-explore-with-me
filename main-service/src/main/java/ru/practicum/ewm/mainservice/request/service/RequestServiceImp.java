package ru.practicum.ewm.mainservice.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.advice.enums.EventState;
import ru.practicum.ewm.mainservice.advice.enums.RequestStatus;
import ru.practicum.ewm.mainservice.advice.exception.EntityNotFoundException;
import ru.practicum.ewm.mainservice.advice.exception.EventConflictException;
import ru.practicum.ewm.mainservice.event.model.Event;
import ru.practicum.ewm.mainservice.event.repository.EventRepository;
import ru.practicum.ewm.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.mainservice.request.dto.RequestMapper;
import ru.practicum.ewm.mainservice.request.model.Request;
import ru.practicum.ewm.mainservice.request.repository.RequestRepository;
import ru.practicum.ewm.mainservice.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@AllArgsConstructor
public class RequestServiceImp implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" +
                        eventId + " was not found"));

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new EventConflictException("Нельзя добавить повторный запрос на участие в том же событии");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new EventConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EventConflictException("Нельзя участвовать в неопубликованном событии");
        }

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new EventConflictException("У события достигнут лимит запросов на участие");
        }

        Request request = new Request();
        request.setEvent(event);
        request.setRequester(User.builder()
                .id(userId)
                .build());
        request.setCreated(LocalDateTime.now());

        if (event.getRequestModeration() == Boolean.FALSE || event.getParticipantLimit() == 0) {
            if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
                throw new EventConflictException("У события достигнут лимит запросов на участие");
            }

            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public Collection<ParticipationRequestDto> findAll(Long userId) {

        Collection<Request> requests = requestRepository.findAllByRequesterId(userId);

        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Request with id=" + requestId + " was not found"));

        if (request.getStatus().equals(RequestStatus.PENDING) ||
                request.getStatus().equals(RequestStatus.CONFIRMED)) {
            request.setStatus(RequestStatus.CANCELED);
        } else {
            throw new EventConflictException("Запрос на участие в событии уже отменен");
        }

        return RequestMapper.toRequestDto(requestRepository.save(request));
    }
}
