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
import org.streaming.example.RawDataMeasured;
import org.streaming.example.UnkiteableWaveDetected;

import java.time.Clock;
import java.time.Instant;

public class WaveProcessor implements Processor<String, RawDataMeasured, String, SpecificRecord> {

    private final Logger logger = LoggerFactory.getLogger(WaveProcessor.class.getSimpleName());
    public static final String WAVE_PROCESSOR_STATE_STORE_NAME = "WaveProcessorStateStore";
    public static final String NAME = WaveProcessor.class.getSimpleName();
    private static final String WAVE_SENSOR_ID = "GH1";
    private ProcessorContext<String, SpecificRecord> context;
    private KeyValueStore<String, RawDataMeasured> keyValueStore;
    private final Clock clock;

    private final double tresholdWaveheight;

    public WaveProcessor(Clock clock, double tresholdWaveheight) {
        this.clock = clock;
        this.tresholdWaveheight = tresholdWaveheight;
    }

    @Override
    public void init(ProcessorContext<String, SpecificRecord> context) {
        this.context = context;
        keyValueStore = context.getStateStore(WAVE_PROCESSOR_STATE_STORE_NAME);
    }

    @Override
    public void process(Record<String, RawDataMeasured> event) {

        if(isWaveHeightEvent(event)) {
            logger.info("Wave event found: %s".formatted(event.value()));
            var newWaveHeight = Double.valueOf(event.value().getValue());

            var storedEvent = keyValueStore.get(event.key());

            if (storedEvent != null) {
                var oldWaveHeight = Double.parseDouble(storedEvent.getValue());

                if (oldWaveHeight == newWaveHeight) {
                    return;
                }

                if (oldWaveHeight > tresholdWaveheight) {
                    // kiteable - old
                    if (newWaveHeight > tresholdWaveheight) {
                        return;
                    } else {
                        keyValueStore.put(event.key(), event.value());
                        var notKiteableWind = mapToNotKiteableWaveDetected(event.value());
                        context.forward(new Record<>(notKiteableWind.getSensorId(), notKiteableWind, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
                    }
                } else {
                    // not kiteable - old
                    if (newWaveHeight > tresholdWaveheight) {
                        keyValueStore.put(event.key(), event.value());
                        var notKiteableWind = mapToKiteableWaveDetected(event.value());
                        context.forward(new Record<>(notKiteableWind.getSensorId(), notKiteableWind, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
                    } else {
                        return;
                    }
                }
            } else {
                keyValueStore.put(event.key(), event.value());
                if (newWaveHeight > tresholdWaveheight) {
                    var kiteableWaveDetected = mapToKiteableWaveDetected(event.value());
                    context.forward(new Record<>(kiteableWaveDetected.getSensorId(), kiteableWaveDetected, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
                } else {
                    var unkiteableWaveDetected = mapToNotKiteableWaveDetected(event.value());
                    context.forward(new Record<>(unkiteableWaveDetected.getSensorId(), unkiteableWaveDetected, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
                }
            }
        }
    }

    private boolean isWaveHeightEvent(Record<String, RawDataMeasured> event) {
        return event.key().contains(WAVE_SENSOR_ID);
    }

    private KiteableWaveDetected mapToKiteableWaveDetected(RawDataMeasured rawDataMeasured) {
        return KiteableWaveDetected.newBuilder()
                .setSensorId(rawDataMeasured.getSensorId())
                .setLocation(rawDataMeasured.getLocation())
                .setValue(rawDataMeasured.getValue())
                .setUnit(rawDataMeasured.getUnit())
                .setDescription(rawDataMeasured.getDescription())
                .build();
    }

    private UnkiteableWaveDetected mapToNotKiteableWaveDetected(RawDataMeasured rawDataMeasured) {
        return UnkiteableWaveDetected.newBuilder()
                .setSensorId(rawDataMeasured.getSensorId())
                .setLocation(rawDataMeasured.getLocation())
                .setValue(rawDataMeasured.getValue())
                .setUnit(rawDataMeasured.getUnit())
                .setDescription(rawDataMeasured.getDescription())
                .build();
    }
}
