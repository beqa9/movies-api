package com.example.movies.services;

import com.example.movies.entities.ApiLog;
import com.example.movies.exception.*;
import com.example.movies.models.ImdbResponseModel;
import com.example.movies.repositories.ApiLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImdbServiceImpl implements ImdbService {

    private final ApiLogRepository apiLogRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${rapidapi.key}")
    private String apiKey;

    private static final String BASE_URL = "https://imdb236.p.rapidapi.com/";

    @Override
    public ImdbResponseModel callImdbApi(String dynamicPath, Map<String, String> queryParams) {

        long start = System.currentTimeMillis();
        String url = buildUrl(dynamicPath, queryParams);

        int status = 500;
        String responseBody = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-RapidAPI-Key", apiKey)
                    .header("X-RapidAPI-Host", "imdb236.p.rapidapi.com")
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            status = response.statusCode();
            responseBody = response.body();

            // Handle API-specific error codes
            if (status == 429) {
                throw new RateLimitException("RapidAPI rate limit exceeded");
            }
            if (status == 404) {
                throw new NotFoundException("IMDb resource not found");
            }
            if (status >= 500) {
                throw new ExternalApiException("IMDb server error: " + status);
            }
            if (status >= 400) {
                throw new BadRequestException("Invalid request to IMDb: " + status);
            }

            return ImdbResponseModel.builder()
                    .json(responseBody)
                    .statusCode(status)
                    .build();

        } catch (IOException | InterruptedException ex) {
            responseBody = ex.getMessage();
            throw new ServiceUnavailableException("IMDb API unavailable", ex);
        } finally {
            logApiCall(url, queryParams, status, responseBody, start);
        }
    }

    private void logApiCall(String url, Map<String, String> params, int status,
                            String response, long startTime) {
        try {
            apiLogRepository.save(
                    ApiLog.builder()
                            .endpoint(url)
                            .httpMethod("GET")
                            .statusCode(status)
                            .executionTimeMs((int) (System.currentTimeMillis() - startTime))
                            .requestParams(
                                    params != null ? objectMapper.writeValueAsString(params) : "{}"
                            )
                            .responseBody(response)
                            .createdAt(OffsetDateTime.now())
                            .build()
            );
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private String buildUrl(String dynamicPath, Map<String, String> queryParams) {
        StringBuilder sb = new StringBuilder(BASE_URL).append(dynamicPath);

        if (queryParams != null && !queryParams.isEmpty()) {
            String qp = queryParams.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));
            sb.append("?").append(qp);
        }

        return sb.toString();
    }
}