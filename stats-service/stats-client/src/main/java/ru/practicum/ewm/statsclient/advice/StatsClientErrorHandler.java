package ru.practicum.ewm.statsclient.advice;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientResponseException;
import ru.practicum.ewm.statsclient.advice.exception.StatsServiceException;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class StatsClientErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public boolean hasError(@NonNull ClientHttpResponse response) throws IOException {
        return super.hasError(response);
    }

    @Override
    public void handleError(@NonNull URI url,
                            @NonNull HttpMethod method,
                            @NonNull ClientHttpResponse response) throws IOException {

        try {
            super.handleError(url, method, response);
        } catch (RestClientResponseException ex) {
            throw new StatsServiceException(
                    "stats-service ответил " + method + " " + url +
                            " - " + ex.getStatusCode() + "; body: " +
                            ex.getResponseBodyAsString(StandardCharsets.UTF_8), ex);
        }
    }
}
