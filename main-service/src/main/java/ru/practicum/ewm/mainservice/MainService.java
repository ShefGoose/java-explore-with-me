package ru.practicum.ewm.mainservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.statsclient.StatsClient;
import ru.practicum.ewm.statsclient.StatsClientRestTemplate;
import ru.practicum.ewm.statsclient.advice.StatsClientErrorHandler;

@SpringBootApplication
public class MainService {
    public static void main(String[] args) {
        SpringApplication.run(MainService.class, args);
    }

    @Bean
    RestTemplate statsRestTemplate(RestTemplateBuilder builder, StatsClientErrorHandler errorHandler) {
        return builder
                .errorHandler(errorHandler)
                .build();
    }

    @Bean
    StatsClientErrorHandler statsClientErrorHandler() {
        return new StatsClientErrorHandler();
    }

    @Bean
    StatsClient statsClient(RestTemplate statsRestTemplate,
                            @Value("${stats-server.url}") String baseUrl) {
        return new StatsClientRestTemplate(statsRestTemplate, baseUrl);

    }
}


