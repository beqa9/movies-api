package com.example.movies.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> buildError(
            Exception ex,
            HttpServletRequest req,
            HttpStatus status
    ) {
        log.error("{} at {}: {}", status, req.getRequestURI(), ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("path", req.getRequestURI());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());

        return ResponseEntity.status(status).body(body);
    }
    // 429 Too Many Requests
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<Object> handleRateLimit(RateLimitException ex, HttpServletRequest req) {
        return buildError(ex, req, HttpStatus.TOO_MANY_REQUESTS);
    }

    // 502 Bad Gateway - IMDb upstream error
    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<Object> handleExternal(ExternalApiException ex, HttpServletRequest req) {
        return buildError(ex, req, HttpStatus.BAD_GATEWAY);
    }

    // 503 Service Unavailable (network issues)
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Object> handleUnavailable(ServiceUnavailableException ex, HttpServletRequest req) {
        return buildError(ex, req, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // 400
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        return buildError(ex, req, HttpStatus.BAD_REQUEST);
    }

    // 404
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return buildError(ex, req, HttpStatus.NOT_FOUND);
    }

    // 502 - upstream API failed
    @ExceptionHandler(ImdbApiException.class)
    public ResponseEntity<Object> handleImdbApi(ImdbApiException ex, HttpServletRequest req) {
        return buildError(ex, req, HttpStatus.BAD_GATEWAY);
    }

    // fallback for unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneral(Exception ex, HttpServletRequest req) {
        return buildError(ex, req, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}