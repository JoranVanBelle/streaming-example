package org.streaming.example.domain;


import java.util.List;

public interface WeatherRepository {

    List<WeatherEntity> findAll();
    void save(WeatherEntity weather);

}
