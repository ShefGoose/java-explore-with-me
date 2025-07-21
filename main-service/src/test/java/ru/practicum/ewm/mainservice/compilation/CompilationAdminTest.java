package ru.practicum.ewm.mainservice.compilation;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.statsclient.StatsClient;


import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class CompilationAdminTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private StatsClient statsClient;

    @Test
    void createAndDeleteCompilation() throws Exception {
        Mockito.when(statsClient.getStats(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.anyList(),
                        Mockito.anyBoolean()))
                .thenReturn(Collections.emptyList());
        Mockito.when(statsClient.hit(Mockito.any())).thenReturn(null);

        String body = "{\"title\":\"compilation\",\"pinned\":false,\"events\":[]}";

        String json = mvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Number id = JsonPath.read(json, "$.id");

        mvc.perform(delete("/admin/compilations/{id}", id))
                .andExpect(status().isNoContent());
    }
}
