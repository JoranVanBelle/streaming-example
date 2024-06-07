package org.streaming.example.infrastructure.processor;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.Record;
import org.streaming.example.adapter.events.KiteableWeatherDetected;
import org.streaming.example.adapter.events.UnkiteableWeatherDetected;
import org.streaming.example.adapter.events.WeatherDetected;
import org.streaming.example.domain.KiteWeatherEntity;
import org.streaming.example.domain.WeatherRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class WeatherDatabaseWriterProcessor implements Processor<String, WeatherDetected, Void, Void> {

    public static final String NAME = WeatherDatabaseWriterProcessor.class.getSimpleName();
    private final WeatherRepository weatherRepository;

    private KiteableWeatherDetected kiteableWeatherDetected;
    private UnkiteableWeatherDetected noKiteableWeatherDetected;

    public WeatherDatabaseWriterProcessor(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @Override
    public void process(Record<String, WeatherDetected> record) {

        parseSpecificRecord(record.value());

        weatherRepository.save(
                new KiteWeatherEntity(kiteableWeatherDetected != null ? kiteableWeatherDetected.getLocation() : noKiteableWeatherDetected.getLocation(),
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(record.timestamp()), TimeZone.getDefault().toZoneId()),
                        kiteableWeatherDetected != null ? Double.parseDouble(kiteableWeatherDetected.getWindSpeed()) : Double.parseDouble(noKiteableWeatherDetected.getWindSpeed()),
                        kiteableWeatherDetected != null ? kiteableWeatherDetected.getWindSpeedUnit() : noKiteableWeatherDetected.getWindSpeedUnit(),
                        kiteableWeatherDetected != null ? Double.parseDouble(kiteableWeatherDetected.getWaveHeight()) : Double.parseDouble(noKiteableWeatherDetected.getWaveHeight()),
                        kiteableWeatherDetected != null ? kiteableWeatherDetected.getWaveHeightUnit() : noKiteableWeatherDetected.getWaveHeightUnit(),
                        kiteableWeatherDetected != null ? Double.parseDouble(kiteableWeatherDetected.getWindDirection()) : Double.parseDouble(noKiteableWeatherDetected.getWindDirection()),
                        kiteableWeatherDetected != null ? kiteableWeatherDetected.getWindDirectionUnit() : noKiteableWeatherDetected.getWindDirectionUnit(),
                        kiteableWeatherDetected != null ? kiteableWeatherDetected.getSchema().getName() : noKiteableWeatherDetected.getSchema().getName())
        );
    }

    private void parseSpecificRecord(WeatherDetected record) {
        if (record.getState() instanceof KiteableWeatherDetected) {
            kiteableWeatherDetected = (KiteableWeatherDetected) record.getState();
            noKiteableWeatherDetected = null;
        } else if (record.getState() instanceof UnkiteableWeatherDetected) {
            noKiteableWeatherDetected = (UnkiteableWeatherDetected) record.getState();
            kiteableWeatherDetected = null;
        } else {
            throw new IllegalArgumentException("%s could not be casted to a weather event".formatted(record));
        }
    }

}
