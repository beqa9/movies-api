package com.example.movies.controllers;

import com.example.movies.models.ImdbResponseModel;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.util.AntPathMatcher;
import com.example.movies.services.ImdbService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/imdb")
public class ImdbController {

    private final ImdbService imdbService;

    @GetMapping("/**")
    public ImdbResponseModel proxy(HttpServletRequest request) {

        String fullPath = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE
        );

        String pattern = (String) request.getAttribute(
                HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE
        );

        String dynamicPath = new AntPathMatcher().extractPathWithinPattern(pattern, fullPath);

        Map<String, String> queryParams = Collections.list(request.getParameterNames())
                .stream()
                .collect(Collectors.toMap(
                        name -> name,
                        request::getParameter
                ));

        return imdbService.callImdbApi(dynamicPath, queryParams);
    }
}