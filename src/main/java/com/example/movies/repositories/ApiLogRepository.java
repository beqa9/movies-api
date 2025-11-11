package com.example.movies.repositories;

import com.example.movies.entities.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {



}
