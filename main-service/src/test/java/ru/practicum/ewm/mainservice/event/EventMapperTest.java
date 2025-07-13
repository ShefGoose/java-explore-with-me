package ru.practicum.ewm.mainservice.event;

import org.junit.jupiter.api.Test;
import ru.practicum.ewm.mainservice.advice.enums.EventState;
import ru.practicum.ewm.mainservice.category.model.Category;
import ru.practicum.ewm.mainservice.event.dto.EventFullDto;
import ru.practicum.ewm.mainservice.event.mapper.EventMapper;
import ru.practicum.ewm.mainservice.event.model.Event;
import ru.practicum.ewm.mainservice.event.model.Location;
import ru.practicum.ewm.mainservice.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventMapperTest {

    @Test
    void toEventFullDtoSuccess() {

        User u = User.builder().id(1L).name("Name").email("dsa@gmail.com").build();
        Category c = Category.builder().id(2L).name("Cat").build();
        Location loc = Location.builder().lat(1.0).lon(2.0).build();

        Event e = Event.builder()
                .id(3L)
                .title("t")
                .annotation("ann")
                .description("desc")
                .state(EventState.PUBLISHED)
                .createdOn(LocalDateTime.now())
                .publishedOn(LocalDateTime.now())
                .eventDate(LocalDateTime.now().plusDays(10))
                .initiator(u)
                .category(c)
                .location(loc)
                .participantLimit(0L)
                .confirmedRequests(0L)
                .paid(false)
                .requestModeration(true)
                .build();

        EventFullDto dto = EventMapper.toEventFullDto(e, 42L);

        assertEquals(3L, dto.getId());
        assertEquals(42L, dto.getViews());
    }
}
