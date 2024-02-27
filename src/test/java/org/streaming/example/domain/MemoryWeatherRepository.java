package org.streaming.example.domain;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A mock version of a database
 */
@Primary
@Repository
@Profile({ "test" })
public class MemoryWeatherRepository implements WeatherRepository {

    private Map<String, WeatherEntity> map = new HashMap<>();

    @Override
    public List<WeatherEntity> findAll() {
        return map.values().stream().toList();
    }

    @Override
    public void save(WeatherEntity weather) {
        map.put(weather.location(), weather);
    }
}
