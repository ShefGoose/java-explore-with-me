package ru.practicum.ewm.mainservice.subscription;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.mainservice.subscription.service.SubscriptionService;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SubscriptionControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private SubscriptionService subscriptionService;

    @Test
    void postSubscriptionReturns201() throws Exception {
        mvc.perform(post("/users/1/subscriptions/2"))
                .andExpect(status().isCreated());

        verify(subscriptionService).subscribe(1L, 2L);
    }

    @Test
    void deleteShouldReturns204() throws Exception {
        mvc.perform(delete("/users/1/subscriptions/2"))
                .andExpect(status().isNoContent());

        verify(subscriptionService).unsubscribe(1L, 2L);
    }

    @Test
    void removeSubscriberShouldReturns204() throws Exception {
        mvc.perform(delete("/users/2/subscriptions/1/cancel"))
                .andExpect(status().isNoContent());

        verify(subscriptionService).removeSubscriber(2L, 1L);
    }
}
