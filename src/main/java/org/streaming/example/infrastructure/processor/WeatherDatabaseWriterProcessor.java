package org.streaming.example.infrastructure.processor;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.Record;
import org.streaming.example.KiteableWeatherDetected;
import org.streaming.example.NoKiteableWeatherDetected;
import org.streaming.example.UnkiteableWaveDetected;
import org.streaming.example.domain.WeatherEntity;
import org.streaming.example.domain.WeatherRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class WeatherDatabaseWriterProcessor implements Processor<String, SpecificRecord, Void, Void> {

    public static final String NAME = WeatherDatabaseWriterProcessor.class.getSimpleName();
    private final WeatherRepository weatherRepository;

    private KiteableWeatherDetected kiteableWeatherDetected;
    private NoKiteableWeatherDetected noKiteableWeatherDetected;

    public WeatherDatabaseWriterProcessor(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @Override
    public void process(Record<String, SpecificRecord> record) {

        parseSpecificRecord(record.value());

        weatherRepository.save(
                WeatherEntity.newWeatherEntity()
                        .withLocation(kiteableWeatherDetected != null ? kiteableWeatherDetected.getLocation() : noKiteableWeatherDetected.getLocation())
                        .withTimestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(record.timestamp()),
                                        TimeZone.getDefault().toZoneId()))
                        .withWaveHeight(kiteableWeatherDetected != null ? Double.parseDouble(kiteableWeatherDetected.getWaveHeight()) : Double.parseDouble(noKiteableWeatherDetected.getWaveHeight()))
                        .withWaveHeightUnit(kiteableWeatherDetected != null ? kiteableWeatherDetected.getWaveHeightUnit() : noKiteableWeatherDetected.getWaveHeightUnit())
                        .withWindSpeed(kiteableWeatherDetected != null ? Double.parseDouble(kiteableWeatherDetected.getWindSpeed()) : Double.parseDouble(noKiteableWeatherDetected.getWindSpeed()))
                        .withWindSpeedUnit(kiteableWeatherDetected != null ? kiteableWeatherDetected.getWindSpeedUnit() : noKiteableWeatherDetected.getWindSpeedUnit())
                        .withWindDirection(kiteableWeatherDetected != null ? Double.parseDouble(kiteableWeatherDetected.getWindDirection()) : Double.parseDouble(noKiteableWeatherDetected.getWindDirection()))
                        .withWindDirectionUnit(kiteableWeatherDetected != null ? kiteableWeatherDetected.getWindDirectionUnit() : noKiteableWeatherDetected.getWindDirectionUnit())
                        .withStatus(kiteableWeatherDetected != null ? kiteableWeatherDetected.getSchema().getName() : noKiteableWeatherDetected.getSchema().getName())
                        .build());
    }

    private void parseSpecificRecord(SpecificRecord record) {
        if (record.getSchema().equals(KiteableWeatherDetected.getClassSchema())) {
            kiteableWeatherDetected = (KiteableWeatherDetected) record;
            noKiteableWeatherDetected = null;
        } else if (record.getSchema().equals(NoKiteableWeatherDetected.getClassSchema())) {
            noKiteableWeatherDetected = (NoKiteableWeatherDetected) record;
            kiteableWeatherDetected = null;
        } else {
            throw new IllegalArgumentException("%s could not be casted to a weather event".formatted(record));
        }
    }

}
