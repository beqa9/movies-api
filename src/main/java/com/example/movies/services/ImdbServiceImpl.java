package com.example.movies.services;

import com.example.movies.entities.ApiLog;
import com.example.movies.models.ImdbResponseModel;
import com.example.movies.repositories.ApiLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final String BASE_URL = "https://imdb236.p.rapidapi.com/";

    @Value("${rapidapi.key}")
    private String apiKey;

    @Override
    public ImdbResponseModel callImdbApi(String dynamicPath, Map<String, String> queryParams) {

        long start = System.currentTimeMillis();
        String url = buildUrl(dynamicPath, queryParams);

        int status = 500;
        String responseJson = null;

        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-RapidAPI-Key", apiKey)
                    .header("X-RapidAPI-Host", "imdb236.p.rapidapi.com")
                    .header("accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            status = response.statusCode();
            responseJson = response.body();

            return ImdbResponseModel.builder()
                    .json(responseJson)
                    .statusCode(status)
                    .build();

        } catch (Exception e) {
            responseJson = "{\"error\":\"" + e.getMessage() + "\"}";
            throw new RuntimeException("IMDb API error: " + e.getMessage());
        } finally {
            try {
                apiLogRepository.save(
                        ApiLog.builder()
                                .endpoint(url)
                                .httpMethod("GET")
                                .statusCode(status)
                                .executionTimeMs((int) (System.currentTimeMillis() - start))
                                .requestParams(queryParams != null
                                        ? objectMapper.writeValueAsString(queryParams)
                                        : "{}")
                                .responseBody(responseJson)
                                .createdAt(OffsetDateTime.now())
                                .build()
                );
            } catch (Exception loggingError) {
                loggingError.printStackTrace();
            }
        }
    }

    private String buildUrl(String dynamicPath, Map<String, String> queryParams) {
        StringBuilder url = new StringBuilder(BASE_URL).append(dynamicPath);

        if (queryParams != null && !queryParams.isEmpty()) {
            String qp = queryParams.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));

            url.append("?").append(qp);
        }
        return url.toString();
    }
}