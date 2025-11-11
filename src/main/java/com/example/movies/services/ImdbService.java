package com.example.movies.services;


import com.example.movies.models.ImdbResponseModel;
import java.util.Map;


public interface ImdbService {
    ImdbResponseModel callImdbApi(String path, Map<String, String> queryParams);
}