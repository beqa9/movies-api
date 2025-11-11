package com.example.movies.mappers;

import com.example.movies.entities.ApiLog;
import com.example.movies.models.ApiLogModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApiLogMapper {

    ApiLogModel toModel(ApiLog log);

    List<ApiLogModel> toModelList(List<ApiLog> logs);
}