package ru.practicum.ewm.mainservice.event;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.mainservice.advice.enums.EventSort;
import ru.practicum.ewm.mainservice.advice.enums.EventState;
import ru.practicum.ewm.mainservice.category.model.Category;
import ru.practicum.ewm.mainservice.category.repository.CategoryRepository;
import ru.practicum.ewm.mainservice.event.dto.EventShortDto;
import ru.practicum.ewm.mainservice.event.mapper.EventPatchMapper;
import ru.practicum.ewm.mainservice.event.model.Event;
import ru.practicum.ewm.mainservice.event.model.Location;
import ru.practicum.ewm.mainservice.event.repository.EventRepository;
import ru.practicum.ewm.mainservice.event.service.EventServiceImp;
import ru.practicum.ewm.mainservice.request.repository.RequestRepository;
import ru.practicum.ewm.mainservice.stats.StatsService;
import ru.practicum.ewm.mainservice.user.model.User;
import ru.practicum.ewm.mainservice.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private EventPatchMapper eventPatchMapper;
    @Mock
    private StatsService statsService;

    @InjectMocks
    EventServiceImp eventService;

    @Test
    void findAllByPublicUserStartAfterEndThrowsValidationException() {

        String rangeStart = "2025-12-31 00:00:00";
        String rangeEnd = "2025-01-01 00:00:00";

        assertThrows(
                ValidationException.class,
                () -> eventService.findAllByPublicUser(
                        null,
                        List.of(),
                        null,
                        rangeStart,
                        rangeEnd,
                        null,
                        EventSort.EVENT_DATE,
                        0, 10,
                        "127.0.0.1", "/events"));
    }

    @Test
    void findAllOnlyAvailableReturnFreeEvents() {
        Category category = Category.builder().id(10L).name("test").build();
        User user = User.builder().id(20L).name("testN").email("test@gmail.com").build();
        Location loc = Location.builder().lat(0.0).lon(0.0).build();

        Event free = Event.builder()
                .id(2L)
                .title("free")
                .annotation("a2")
                .description("d2")
                .participantLimit(5L)
                .confirmedRequests(0L)
                .state(EventState.PUBLISHED)
                .eventDate(LocalDateTime.now().plusDays(20))
                .category(category)
                .initiator(user)
                .location(loc)
                .paid(false)
                .build();

        when(eventRepository.findAll(Mockito.<Specification<Event>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(free)));

        when(statsService.buildViewsMapForPublished(anyCollection()))
                .thenReturn(Collections.emptyMap());

        Collection<EventShortDto> result = eventService.findAllByPublicUser(
                null,
                null, null,
                null, null,
                true,
                EventSort.EVENT_DATE,
                0, 10,
                "127.0.0.1", "/events");

        assertEquals(1, result.size());
        assertEquals(2L, result.iterator().next().getId());

        verify(statsService).addHit("/events", "127.0.0.1");
    }

}
