package org.streaming.example.application.weather;

import org.springframework.stereotype.Component;
import org.streaming.example.domain.WeatherEntity;
import org.streaming.example.domain.WeatherRepository;
import org.streaming.example.domain.http.UseCaseResult;

import java.util.List;

import static org.streaming.example.domain.http.ResponseEntityStatus.success;

@Component
public class FindUseCase {

    private final WeatherRepository weatherRepository;

    public FindUseCase(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

//    public UseCaseResult<List<WeatherEntity>> findAll() {
        public List<WeatherEntity> findAll() {
        var data = weatherRepository.findAll();
//        return new UseCaseResult<>(success, data);
        return data;
    }
}
