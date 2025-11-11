package com.example.movies.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImdbResponseModel {
    private Object data;
    private String json;
    private int statusCode;
}
