package org.streaming.example.adapter.http.weather;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.streaming.example.application.weather.FindUseCase;
import org.streaming.example.domain.WeatherEntity;
import org.streaming.example.domain.http.ResponseEntityStatus;

import java.util.List;

@RestController
public class WeatherController {

    private final FindUseCase findUseCase;

    public WeatherController(FindUseCase findUseCase) {
        this.findUseCase = findUseCase;
    }

    @GetMapping("/weather")
    public ResponseEntity<WeatherReponse> findAll() {
        var result = findUseCase.findAll();
        return ResponseEntity.ok(new WeatherReponse(ResponseEntityStatus.success, result));
    }

    private ResponseEntity<?> responseEntityOf(WeatherReponse result) {
        return switch (result.status()) {
            case success -> ResponseEntity.ok(result);
            case fail -> ResponseEntity.unprocessableEntity().body(result);
            case error -> ResponseEntity.internalServerError().body(result);
        };
    }
}
