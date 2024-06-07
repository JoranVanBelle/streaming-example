package org.streaming.example.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * The weatherObject in the database that is also returned via the controller
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather implements Comparable<Weather> {

    private String location;
    private LocalDateTime timestamp;
    private Double windSpeed;
    private String windSpeedUnit;
    private Double waveHeight;
    private String waveHeightUnit;
    private Double windDirection;
    private String windDirectionUnit;
    private String status;

    public Weather(
            String location,
            LocalDateTime timestamp,
            Double windSpeed,
            String windSpeedUnit,
            Double waveHeight,
            String waveHeightUnit,
            Double windDirection,
            String windDirectionUnit,
            String status) {
        this.status = status;
        this.location = location;
        this.timestamp = timestamp;
        this.windSpeed = windSpeed;
        this.windSpeedUnit = windSpeedUnit;
        this.waveHeight = waveHeight;
        this.waveHeightUnit = waveHeightUnit;
        this.windDirection = windDirection;
        this.windDirectionUnit = windDirectionUnit;
    }

    public String location() {
        return location;
    }

    public LocalDateTime timestamp() {
        return timestamp;
    }

    public Double windSpeed() {
        return windSpeed;
    }

    public String windSpeedUnit() {
        return windSpeedUnit;
    }

    public Double waveHeight() {
        return waveHeight;
    }

    public String waveHeightUnit() {
        return waveHeightUnit;
    }

    public Double windDirection() {
        return windDirection;
    }

    public String windDirectionUnit() {
        return windDirectionUnit;
    }

    public String status() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindSpeedUnit() {
        return windSpeedUnit;
    }

    public void setWindSpeedUnit(String windSpeedUnit) {
        this.windSpeedUnit = windSpeedUnit;
    }

    public Double getWaveHeight() {
        return waveHeight;
    }

    public void setWaveHeight(Double waveHeight) {
        this.waveHeight = waveHeight;
    }

    public String getWaveHeightUnit() {
        return waveHeightUnit;
    }

    public void setWaveHeightUnit(String waveHeightUnit) {
        this.waveHeightUnit = waveHeightUnit;
    }

    public Double getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(Double windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindDirectionUnit() {
        return windDirectionUnit;
    }

    public void setWindDirectionUnit(String windDirectionUnit) {
        this.windDirectionUnit = windDirectionUnit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weather that = (Weather) o;
        return Objects.equals(location, that.location) && Objects.equals(timestamp, that.timestamp) && Objects.equals(windSpeed, that.windSpeed) && Objects.equals(windSpeedUnit, that.windSpeedUnit) && Objects.equals(waveHeight, that.waveHeight) && Objects.equals(waveHeightUnit, that.waveHeightUnit) && Objects.equals(windDirection, that.windDirection) && Objects.equals(windDirectionUnit, that.windDirectionUnit) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, timestamp, windSpeed, windSpeedUnit, waveHeight, waveHeightUnit, windDirection, windDirectionUnit, status);
    }

    @Override
    public int compareTo(Weather old) {
        return old.location().compareTo(location());
    }

    public static WeatherEntityBuilder newWeatherEntity() {
        return new WeatherEntityBuilder();
    }

    public static class WeatherEntityBuilder {
        private String location;
        private LocalDateTime timestamp = LocalDateTime.now(ZoneId.of("Europe/Brussels"))
                .truncatedTo(ChronoUnit.SECONDS);
        private Double windSpeed;
        private String windSpeedUnit;
        private Double waveHeight;
        private String waveHeightUnit;
        private Double windDirection;
        private String windDirectionUnit;
        private String status;

        public WeatherEntityBuilder withLocation(String location) {
            this.location = location;
            return this;
        }

        public WeatherEntityBuilder withTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public WeatherEntityBuilder withWindSpeed(Double windSpeed) {
            this.windSpeed = windSpeed;
            return this;
        }

        public WeatherEntityBuilder withWindSpeedUnit(String windSpeedUnit) {
            this.windSpeedUnit = windSpeedUnit;
            return this;
        }

        public WeatherEntityBuilder withWaveHeight(Double waveHeight) {
            this.waveHeight = waveHeight;
            return this;
        }

        public WeatherEntityBuilder withWaveHeightUnit(String waveHeightUnit) {
            this.waveHeightUnit = waveHeightUnit;
            return this;
        }

        public WeatherEntityBuilder withWindDirection(Double windDirection) {
            this.windDirection = windDirection;
            return this;
        }

        public WeatherEntityBuilder withWindDirectionUnit(String windDirectionUnit) {
            this.windDirectionUnit = windDirectionUnit;
            return this;
        }

        public WeatherEntityBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Weather build() {
            return new Weather(
                    location,
                    timestamp,
                    windSpeed,
                    windSpeedUnit,
                    waveHeight,
                    waveHeightUnit,
                    windDirection,
                    windDirectionUnit,
                    status
            );
        }

    }
}
