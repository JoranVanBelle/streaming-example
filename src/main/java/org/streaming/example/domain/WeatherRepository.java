package org.streaming.example.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<KiteWeatherEntity, String> {}
