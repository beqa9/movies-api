package com.example.movies.models;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ApiLogModel(
        Long id,
        String endpoint,
        String httpMethod,
        String requestParams,
        String responseBody,
        Integer statusCode,
        Integer executionTimeMs,
        OffsetDateTime createdAt
) {}