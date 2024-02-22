package org.streaming.example.domain;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.VARCHAR;

@Repository
@Profile("!test")
@EnableKafkaStreams
public class PostgresWeatherRepository implements WeatherRepository {

    private final JdbcClient jdbcClient;

    public PostgresWeatherRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<WeatherEntity> findAll() {
        return jdbcClient.sql("""
                         SELECT *
                         FROM "KiteWeather";""")
                .query(). listOfRows().stream().map(this::toWeatherEntity).toList();
    }

    private WeatherEntity toWeatherEntity(Map<String, Object> weatherEntityMap) {

        return WeatherEntity.newWeatherEntity()
                .withLocation(weatherEntityMap.get("location").toString())
                .withTimestamp(((Timestamp) weatherEntityMap.get("timestamp")).toLocalDateTime())
                .withWindSpeed(Double.parseDouble(weatherEntityMap.get("windSpeed").toString()))
                .withWindSpeedUnit(weatherEntityMap.get("windSpeedUnit").toString())
                .withWaveHeight(Double.parseDouble(weatherEntityMap.get("waveHeight").toString()))
                .withWaveHeightUnit(weatherEntityMap.get("waveHeightUnit").toString())
                .withWindDirection(Double.parseDouble(weatherEntityMap.get("windDirection").toString()))
                .withWindDirectionUnit(weatherEntityMap.get("windDirectionUnit").toString())
                .withStatus(weatherEntityMap.get("status").toString())
                .build();
    }

    @Override
    public void save(WeatherEntity weather) {
        var params = new MapSqlParameterSource();
        params.addValue("location", weather.location(), VARCHAR);
        params.addValue("timestamp", weather.timestamp(), TIMESTAMP);
        params.addValue("windSpeed", weather.windSpeed(), VARCHAR);
        params.addValue("windSpeedUnit", weather.windSpeedUnit(), VARCHAR);
        params.addValue("waveHeight", weather.waveHeight(), VARCHAR);
        params.addValue("waveHeightUnit", weather.waveHeightUnit(), VARCHAR);
        params.addValue("windDirection", weather.windDirection(), VARCHAR);
        params.addValue("windDirectionUnit", weather.windDirectionUnit(), VARCHAR);
        params.addValue("status", weather.status(), VARCHAR);
        jdbcClient.sql("""
                         INSERT INTO "KiteWeather"("location", "timestamp", "windSpeed", "windSpeedUnit", "waveHeight", "waveHeightUnit", "windDirection", "windDirectionUnit", "status")
                         VALUES(:location, :timestamp, :windSpeed, :windSpeedUnit, :waveHeight, :waveHeightUnit, :windDirection, :windDirectionUnit, :status)
                        ON CONFLICT ("location")
                        DO UPDATE  SET "location" = :location, "timestamp" = :timestamp, "windSpeed" = :windSpeed, "windSpeedUnit" = :windSpeedUnit, "waveHeight" = :waveHeight, "waveHeightUnit" = :waveHeightUnit, "windDirection" = :windDirection, "windDirectionUnit" = :windDirectionUnit, "status" = :status;""")
                .paramSource(params).update();

    }
}
