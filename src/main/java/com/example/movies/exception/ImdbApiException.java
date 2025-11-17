package com.example.movies.exception;

public class ImdbApiException extends RuntimeException {
    public ImdbApiException(String message) {
        super(message);
    }
}