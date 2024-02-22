package org.streaming.example.adapter.http.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.streaming.example.domain.WeatherEntity;
import org.streaming.example.domain.http.ResponseEntityStatus;
import org.streaming.example.domain.http.UseCaseResult;

import java.util.List;
import java.util.Objects;

public record WeatherReponse(
        ResponseEntityStatus status,
        List<WeatherEntity> weather
) {
}
