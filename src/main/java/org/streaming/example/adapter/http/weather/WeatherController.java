package org.streaming.example.adapter.http.weather;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.streaming.example.application.weather.FindUseCase;
import org.streaming.example.domain.http.ResponseEntityStatus;

@RestController
public class WeatherController {

    private final FindUseCase findUseCase;

    public WeatherController(FindUseCase findUseCase) {
        this.findUseCase = findUseCase;
    }

    @GetMapping("/weather")
    public ResponseEntity<WeatherReponse> findAll() {
        var result = findUseCase.findAll();
        return ResponseEntity.ok(new WeatherReponse(ResponseEntityStatus.success, result, result.size()));
    }
}
