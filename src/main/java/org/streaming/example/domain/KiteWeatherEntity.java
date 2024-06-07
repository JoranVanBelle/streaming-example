package org.streaming.example.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "kite_weather")
public class KiteWeatherEntity {

    @Id
    private String location;

    @NonNull
    private LocalDateTime timestamp;

    @NonNull
    private Double windSpeed;

    @NonNull
    private String windSpeedUnit;

    @NonNull
    private Double waveHeight;

    @NonNull
    private String waveHeightUnit;

    @NonNull
    private Double windDirection;

    @NonNull
    private String windDirectionUnit;

    @NonNull
    private String status;

    public KiteWeatherEntity() {}

    public KiteWeatherEntity(String location,
                             @NonNull LocalDateTime timestamp,
                             @NonNull Double windSpeed,
                             @NonNull String windSpeedUnit,
                             @NonNull Double waveHeight,
                             @NonNull String waveHeightUnit,
                             @NonNull Double windDirection,
                             @NonNull String windDirectionUnit,
                             @NonNull String status) {
        this.location = location;
        this.timestamp = timestamp;
        this.windSpeed = windSpeed;
        this.windSpeedUnit = windSpeedUnit;
        this.waveHeight = waveHeight;
        this.waveHeightUnit = waveHeightUnit;
        this.windDirection = windDirection;
        this.windDirectionUnit = windDirectionUnit;
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    @NonNull
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @NonNull
    public Double getWindSpeed() {
        return windSpeed;
    }

    @NonNull
    public String getWindSpeedUnit() {
        return windSpeedUnit;
    }

    @NonNull
    public Double getWaveHeight() {
        return waveHeight;
    }

    @NonNull
    public String getWaveHeightUnit() {
        return waveHeightUnit;
    }

    @NonNull
    public Double getWindDirection() {
        return windDirection;
    }

    @NonNull
    public String getWindDirectionUnit() {
        return windDirectionUnit;
    }

    @NonNull
    public String getStatus() {
        return status;
    }
}
