package org.streaming.example.application.weather;

import org.springframework.stereotype.Component;
import org.streaming.example.domain.WeatherRepository;
import org.streaming.example.domain.KiteWeatherEntity;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class FindUseCase {

    private final WeatherRepository weatherRepository;

    public FindUseCase(org.streaming.example.domain.WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

        public List<KiteWeatherEntity> findAll() {
        return weatherRepository.findAll();
    }
}
