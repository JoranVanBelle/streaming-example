package org.streaming.example.infrastructure.processor;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.example.KiteableWaveDetected;
import org.streaming.example.KiteableWeatherDetected;
import org.streaming.example.KiteableWindDetected;
import org.streaming.example.KiteableWindDirectionDetected;
import org.streaming.example.NoKiteableWeatherDetected;
import org.streaming.example.UnkiteableWaveDetected;
import org.streaming.example.UnkiteableWindDetected;
import org.streaming.example.UnkiteableWindDirectionDetected;

import java.time.Clock;
import java.time.Instant;

import static org.streaming.example.infrastructure.processor.WaveStateStorePopulator.REKEYED_WAVE_STATE_STORE_NAME;
import static org.streaming.example.infrastructure.processor.WindDirectionStateStorePopulator.REKEYED_WIND_DIRECTION_STATE_STORE_NAME;
import static org.streaming.example.infrastructure.processor.WindSpeedStateStorePopulator.REKEYED_WIND_SPEED_STATE_STORE_NAME;

public class JoinProcessor implements Processor<String, SpecificRecord, String, SpecificRecord> {
    private final Logger logger = LoggerFactory.getLogger(JoinProcessor.class.getSimpleName());
    public static final String NAME = JoinProcessor.class.getSimpleName();
    private final Clock clock;
    private ProcessorContext<String, SpecificRecord> context;
    private KeyValueStore<String, SpecificRecord> waveKeyValueStore;
    private KeyValueStore<String, SpecificRecord> windSpeedKeyValueStore;
    private KeyValueStore<String, SpecificRecord> windDirectionKeyValueStore;
    private KiteableWaveDetected kiteableWaveDetected;
    private UnkiteableWaveDetected unkiteableWaveDetected;
    private KiteableWindDetected kiteableWindDetected;
    private UnkiteableWindDetected unkiteableWindDetected;
    private KiteableWindDirectionDetected kiteableWindDirectionDetected;
    private UnkiteableWindDirectionDetected unkiteableWindDirectionDetected;

    public JoinProcessor(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void init(ProcessorContext<String, SpecificRecord> context) {
        this.context = context;
        waveKeyValueStore = context.getStateStore(REKEYED_WAVE_STATE_STORE_NAME);
        windSpeedKeyValueStore = context.getStateStore(REKEYED_WIND_SPEED_STATE_STORE_NAME);
        windDirectionKeyValueStore = context.getStateStore(REKEYED_WIND_DIRECTION_STATE_STORE_NAME);
    }

    @Override
    public void process(Record<String, SpecificRecord> record) {
        var key = record.key();
        if (keyProcessedByAllParents(key)) {
            getWaveEvent(key);
            getWindSpeedEvent(key);
            getWindDirectionEvent(key);

            if (kiteableWaveDetected != null && kiteableWindDetected != null && kiteableWindDirectionDetected != null) {
                var kiteableWeather = new KiteableWeatherDetected(
                        kiteableWaveDetected.getLocation(),
                        kiteableWaveDetected.getLocation(),
                        kiteableWaveDetected.getValue(),
                        kiteableWaveDetected.getUnit(),
                        kiteableWindDetected.getValue(),
                        kiteableWindDetected.getUnit(),
                        kiteableWindDirectionDetected.getValue(),
                        kiteableWindDirectionDetected.getUnit()
                );
                logger.info("Kiteable weather detected: %s".formatted(kiteableWeather));
                waveKeyValueStore.delete(key);
                windSpeedKeyValueStore.delete(key);
                windDirectionKeyValueStore.delete(key);
                context.forward(new Record<>(kiteableWeather.getDataId(), kiteableWeather, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
            } else {
                var unkiteableWeather = new NoKiteableWeatherDetected(
                        kiteableWaveDetected != null ? kiteableWaveDetected.getLocation() : unkiteableWaveDetected.getLocation(),
                        kiteableWaveDetected != null ? kiteableWaveDetected.getLocation() : unkiteableWaveDetected.getLocation(),
                        kiteableWaveDetected != null ? kiteableWaveDetected.getValue() : unkiteableWaveDetected.getValue(),
                        kiteableWaveDetected != null ? kiteableWaveDetected.getUnit() : unkiteableWaveDetected.getUnit(),
                        kiteableWindDetected != null ? kiteableWindDetected.getValue() : unkiteableWindDetected.getValue(),
                        kiteableWindDetected != null ? kiteableWindDetected.getUnit() : unkiteableWindDetected.getUnit(),
                        kiteableWindDirectionDetected != null ? kiteableWindDirectionDetected.getValue() : unkiteableWindDirectionDetected.getValue(),
                        kiteableWindDirectionDetected != null ? kiteableWindDirectionDetected.getUnit() : unkiteableWindDirectionDetected.getUnit()
                        );
                logger.info("Unkiteable weather detected: %s".formatted(unkiteableWeather));
                waveKeyValueStore.delete(key);
                windSpeedKeyValueStore.delete(key);
                windDirectionKeyValueStore.delete(key);
                context.forward(new Record<>(unkiteableWeather.getDataId(), unkiteableWeather, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
            }

        }
    }

    private boolean keyProcessedByAllParents(String key) {
        return waveKeyValueStore.get(key) != null && windSpeedKeyValueStore.get(key) != null && windDirectionKeyValueStore.get(key) != null;
    }

    private void getWaveEvent(String key) {
        var specificWave = waveKeyValueStore.get(key);
        if (specificWave.getSchema().equals(KiteableWaveDetected.getClassSchema())) {
            kiteableWaveDetected = (KiteableWaveDetected) specificWave;
            unkiteableWaveDetected = null;
        } else if (specificWave.getSchema().equals(UnkiteableWaveDetected.getClassSchema())) {
            unkiteableWaveDetected = (UnkiteableWaveDetected) specificWave;
            kiteableWaveDetected = null;
        } else {
            throw new IllegalArgumentException("%s could not be casted to a wave event".formatted(specificWave));
        }
    }

    private void getWindSpeedEvent(String key) {
        var specificWindSpeed = windSpeedKeyValueStore.get(key);
        if (specificWindSpeed.getSchema().equals(KiteableWindDetected.getClassSchema())) {
            kiteableWindDetected = (KiteableWindDetected) specificWindSpeed;
            unkiteableWindDetected = null;
        } else if (specificWindSpeed.getSchema().equals(UnkiteableWindDetected.getClassSchema())) {
            unkiteableWindDetected = (UnkiteableWindDetected) specificWindSpeed;
            kiteableWindDetected = null;
        } else {
            throw new IllegalArgumentException("%s could not be casted to a wind speed event".formatted(specificWindSpeed));
        }
    }

    private void getWindDirectionEvent(String key) {
        var specificWindDirection = windDirectionKeyValueStore.get(key);
        if (specificWindDirection.getSchema().equals(KiteableWindDirectionDetected.getClassSchema())) {
            kiteableWindDirectionDetected = (KiteableWindDirectionDetected) specificWindDirection;
            unkiteableWindDirectionDetected = null;
        } else if (specificWindDirection.getSchema().equals(UnkiteableWindDirectionDetected.getClassSchema())) {
            unkiteableWindDirectionDetected = (UnkiteableWindDirectionDetected) specificWindDirection;
            kiteableWindDirectionDetected = null;
        } else {
            throw new IllegalArgumentException("%s could not be casted to a wind direction event".formatted(specificWindDirection));
        }
    }
}
