package org.streaming.example.adapter.http.weather;

import org.streaming.example.domain.KiteWeatherEntity;
import org.streaming.example.domain.http.ResponseEntityStatus;

import java.util.List;

public record WeatherReponse(
        ResponseEntityStatus status,
        List<KiteWeatherEntity> weather,
        Integer size
) {
}
