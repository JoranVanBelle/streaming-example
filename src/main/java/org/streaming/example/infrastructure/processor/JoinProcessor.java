package org.streaming.example.infrastructure.processor;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.example.adapter.events.KiteableWaveDetected;
import org.streaming.example.adapter.events.KiteableWeatherDetected;
import org.streaming.example.adapter.events.KiteableWindDirectionDetected;
import org.streaming.example.adapter.events.KiteableWindSpeedDetected;
import org.streaming.example.adapter.events.UnkiteableWaveDetected;
import org.streaming.example.adapter.events.UnkiteableWeatherDetected;
import org.streaming.example.adapter.events.UnkiteableWindDirectionDetected;
import org.streaming.example.adapter.events.UnkiteableWindSpeedDetected;
import org.streaming.example.adapter.events.WaveDetected;
import org.streaming.example.adapter.events.WeatherDetected;
import org.streaming.example.adapter.events.WindDirectionDetected;
import org.streaming.example.adapter.events.WindSpeedDetected;

import java.time.Clock;
import java.time.Instant;

import static org.streaming.example.infrastructure.processor.WaveStateStorePopulatorProcessor.REKEYED_WAVE_STATE_STORE_NAME;
import static org.streaming.example.infrastructure.processor.WindDirectionStateStorePopulatorProcessor.REKEYED_WIND_DIRECTION_STATE_STORE_NAME;
import static org.streaming.example.infrastructure.processor.WindSpeedStateStorePopulatorProcessor.REKEYED_WIND_SPEED_STATE_STORE_NAME;

public class JoinProcessor implements Processor<String, SpecificRecord, String, WeatherDetected> {
    private final Logger logger = LoggerFactory.getLogger(JoinProcessor.class.getSimpleName());
    public static final String NAME = JoinProcessor.class.getSimpleName();
    private final Clock clock;
    private ProcessorContext<String, WeatherDetected> context;
    private KeyValueStore<String, WaveDetected> waveKeyValueStore;
    private KeyValueStore<String, WindSpeedDetected> windSpeedKeyValueStore;
    private KeyValueStore<String, WindDirectionDetected> windDirectionKeyValueStore;

    public JoinProcessor(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void init(ProcessorContext<String, WeatherDetected> context) {
        this.context = context;
        waveKeyValueStore = context.getStateStore(REKEYED_WAVE_STATE_STORE_NAME);
        windSpeedKeyValueStore = context.getStateStore(REKEYED_WIND_SPEED_STATE_STORE_NAME);
        windDirectionKeyValueStore = context.getStateStore(REKEYED_WIND_DIRECTION_STATE_STORE_NAME);
    }

    @Override
    public void process(Record<String, SpecificRecord> record) {

        var key = record.key();

        if (keyProcessedByAllParents(key)) {
            var waveDetected = getWaveEvent(key);
            var windSpeedDetected = getWindSpeedEvent(key);
            var windDirectionDetected = getWindDirectionEvent(key);

            if (waveDetected.getState() instanceof KiteableWaveDetected &&
                windSpeedDetected.getState() instanceof KiteableWindSpeedDetected &&
                windDirectionDetected.getState() instanceof KiteableWindDirectionDetected) {
                var kiteableWeather = new KiteableWeatherDetected(
                    ((KiteableWaveDetected) waveDetected.getState()).getLocation(),
                    ((KiteableWaveDetected) waveDetected.getState()).getLocation(),
                    ((KiteableWindSpeedDetected) windSpeedDetected.getState()).getValue(),
                    ((KiteableWindSpeedDetected) windSpeedDetected.getState()).getUnit(),
                    ((KiteableWaveDetected) waveDetected.getState()).getValue(),
                    ((KiteableWaveDetected) waveDetected.getState()).getUnit(),
                    ((KiteableWindDirectionDetected) windDirectionDetected.getState()).getValue(),
                    ((KiteableWindDirectionDetected) windDirectionDetected.getState()).getUnit()
                );
                logger.info("Kiteable weather detected: %s".formatted(kiteableWeather));
                context.forward(new Record<>(kiteableWeather.getDataId(), new WeatherDetected(kiteableWeather), Instant.now(clock).toEpochMilli(), new RecordHeaders()));
            } else {
                var unkiteableWeather = new UnkiteableWeatherDetected(
                    waveDetected.getState() instanceof KiteableWaveDetected ? ((KiteableWaveDetected) waveDetected.getState()).getLocation() : ((UnkiteableWaveDetected) waveDetected.getState()).getLocation(),
                    waveDetected.getState() instanceof KiteableWaveDetected ? ((KiteableWaveDetected) waveDetected.getState()).getLocation() : ((UnkiteableWaveDetected) waveDetected.getState()).getLocation(),
                    windSpeedDetected.getState() instanceof KiteableWindSpeedDetected ? ((KiteableWindSpeedDetected) windSpeedDetected.getState()).getValue() : ((UnkiteableWindSpeedDetected) windSpeedDetected.getState()).getValue(),
                    windSpeedDetected.getState() instanceof KiteableWindSpeedDetected ? ((KiteableWindSpeedDetected) windSpeedDetected.getState()).getUnit() : ((UnkiteableWindSpeedDetected) windSpeedDetected.getState()).getUnit(),
                    waveDetected.getState() instanceof KiteableWaveDetected ? ((KiteableWaveDetected) waveDetected.getState()).getValue() : ((UnkiteableWaveDetected) waveDetected.getState()).getValue(),
                    waveDetected.getState() instanceof KiteableWaveDetected ? ((KiteableWaveDetected) waveDetected.getState()).getUnit() : ((UnkiteableWaveDetected) waveDetected.getState()).getUnit(),
                    windDirectionDetected.getState() instanceof KiteableWindDirectionDetected ? ((KiteableWindDirectionDetected) windDirectionDetected.getState()).getValue() : ((UnkiteableWindDirectionDetected) windDirectionDetected.getState()).getValue(),
                    windDirectionDetected.getState() instanceof KiteableWindDirectionDetected ? ((KiteableWindDirectionDetected) windDirectionDetected.getState()).getUnit() : ((UnkiteableWindDirectionDetected) windDirectionDetected.getState()).getUnit()
                );
                logger.info("Unkiteable weather detected: %s".formatted(unkiteableWeather));
                context.forward(new Record<>(unkiteableWeather.getDataId(), new WeatherDetected(unkiteableWeather), Instant.now(clock).toEpochMilli(), new RecordHeaders()));
            }

        }
    }

    private boolean keyProcessedByAllParents(String key) {
        return waveKeyValueStore.get(key) != null && windSpeedKeyValueStore.get(key) != null && windDirectionKeyValueStore.get(key) != null;
    }

    private WaveDetected getWaveEvent(String key) {
        return waveKeyValueStore.get(key);
    }

    private WindSpeedDetected getWindSpeedEvent(String key) {
        return windSpeedKeyValueStore.get(key);
    }

    private WindDirectionDetected getWindDirectionEvent(String key) {
        return windDirectionKeyValueStore.get(key);
    }
}
