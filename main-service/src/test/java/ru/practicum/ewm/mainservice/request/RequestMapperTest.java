package ru.practicum.ewm.mainservice.request;

import org.junit.jupiter.api.Test;
import ru.practicum.ewm.mainservice.advice.enums.RequestStatus;
import ru.practicum.ewm.mainservice.event.model.Event;
import ru.practicum.ewm.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.mainservice.request.dto.RequestMapper;
import ru.practicum.ewm.mainservice.request.model.Request;
import ru.practicum.ewm.mainservice.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMapperTest {
    @Test
    void mapperBuildsDto_correctly() {
        User u = User.builder().id(5L).name("name").email("eas@gmail.com").build();
        Event e = Event.builder().id(6L).build();

        Request r = Request.builder()
                .id(7L)
                .created(LocalDateTime.now())
                .status(RequestStatus.PENDING)
                .event(e)
                .requester(u)
                .build();

        ParticipationRequestDto dto = RequestMapper.toRequestDto(r);

        assertEquals(5L, dto.getRequester());
        assertEquals(6L, dto.getEvent());
    }
}
