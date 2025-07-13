package ru.practicum.ewm.mainservice.event;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.mainservice.advice.enums.EventState;
import ru.practicum.ewm.mainservice.category.model.Category;
import ru.practicum.ewm.mainservice.category.repository.CategoryRepository;
import ru.practicum.ewm.mainservice.event.model.Event;
import ru.practicum.ewm.mainservice.event.model.Location;
import ru.practicum.ewm.mainservice.event.repository.EventRepository;
import ru.practicum.ewm.mainservice.user.model.User;
import ru.practicum.ewm.mainservice.user.repository.UserRepository;
import ru.practicum.ewm.statsclient.StatsClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class PublicEventTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @MockBean
    private StatsClient statsClient;

    private Long eventId;

    @BeforeEach
    void setUp() {
        Location loc = Location.builder()
                .lat(0.0)
                .lon(0.0)
                .build();

        User user = userRepository.save(User.builder()
                .name("user")
                .email("user@test.com")
                .build());

        Category cat = categoryRepository.save(Category.builder()
                .name("cat")
                .build());

        Event e = Event.builder()
                .title("test event")
                .annotation("test")
                .description("desc")
                .location(loc)
                .eventDate(LocalDateTime.now().plusDays(30))
                .createdOn(LocalDateTime.now())
                .publishedOn(LocalDateTime.now())
                .state(EventState.PUBLISHED)
                .initiator(user)
                .category(cat)
                .build();
        eventId = eventRepository.save(e).getId();

        Mockito.when(statsClient.getStats(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.anyList(),
                        Mockito.anyBoolean()))
                .thenReturn(Collections.emptyList());
        Mockito.when(statsClient.hit(Mockito.any()))
                .thenReturn(null);
    }

    @Test
    void getExistsEvent200() throws Exception {
        mvc.perform(get("/events/{id}", eventId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}
