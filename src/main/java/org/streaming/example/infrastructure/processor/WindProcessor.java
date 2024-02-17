package org.streaming.example.infrastructure.processor;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.example.KiteableWindDetected;
import org.streaming.example.RawDataMeasured;
import org.streaming.example.UnkiteableWindDetected;

import java.time.Clock;
import java.time.Instant;

import static java.lang.Math.round;

public class WindProcessor implements Processor<String, RawDataMeasured, String, SpecificRecord> {

    private final Logger logger = LoggerFactory.getLogger(WindProcessor.class.getSimpleName());
    public static final String WIND_PROCESSOR_STATE_STORE_NAME = "WindProcessorStateStore";
    public static final String NAME = WindProcessor.class.getSimpleName();
    private static final String WIND_SPEED_SENSOR_ID = "WVC";
    private ProcessorContext<String, SpecificRecord> context;
    private KeyValueStore<String, RawDataMeasured> keyValueStore;
    private final Clock clock;

    private final double tresholdWindSpeed;

    public WindProcessor(Clock clock, double tresholdWindSpeed) {
        this.clock = clock;
        this.tresholdWindSpeed = tresholdWindSpeed;
    }

    @Override
    public void init(ProcessorContext<String, SpecificRecord> context) {
        this.context = context;
        keyValueStore = context.getStateStore(WIND_PROCESSOR_STATE_STORE_NAME);
    }

    @Override
    public void process(Record<String, RawDataMeasured> event) {

        if (isWindSpeedSensor(event)) {
            logger.info("Wind event found: %s".formatted(event.value()));
            var newWindSpeed = Double.valueOf(event.value().getValue());

            var storedEvent = keyValueStore.get(event.key());

            if (storedEvent != null) {
                var oldWindSpeed = Double.parseDouble(storedEvent.getValue());

                if (oldWindSpeed == newWindSpeed) {
                    return;
                }

                if (oldWindSpeed > tresholdWindSpeed) {
                    if (newWindSpeed > tresholdWindSpeed) {
                        return;
                    } else {
                        keyValueStore.put(event.key(), event.value());
                        var notKiteableWind = mapToNotKiteableWindDetected(event.value());
                        context.forward(new Record<>(notKiteableWind.getSensorId(), notKiteableWind, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
                    }
                } else {
                    if (newWindSpeed > tresholdWindSpeed) {
                        keyValueStore.put(event.key(), event.value());
                        var kiteableWind = mapToKiteableWindDetected(event.value());
                        context.forward(new Record<>(kiteableWind.getSensorId(), kiteableWind, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
                    } else {
                        return;
                    }
                }
            } else {
                keyValueStore.put(event.key(), event.value());
                if (newWindSpeed > tresholdWindSpeed) {
                    var kiteableWind = mapToKiteableWindDetected(event.value());
                    context.forward(new Record<>(kiteableWind.getSensorId(), kiteableWind, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
                } else {
                    var notKiteableWind = mapToNotKiteableWindDetected(event.value());
                    context.forward(new Record<>(notKiteableWind.getSensorId(), notKiteableWind, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
                }
            }
        }

    }

    private boolean isWindSpeedSensor(Record<String, RawDataMeasured> event) {
        return event.key().contains(WIND_SPEED_SENSOR_ID);
    }

    private KiteableWindDetected mapToKiteableWindDetected(RawDataMeasured rawDataMeasured) {
        return KiteableWindDetected.newBuilder()
                .setSensorId(rawDataMeasured.getSensorId())
                .setLocation(rawDataMeasured.getLocation())
                .setValue(rawDataMeasured.getValue())
                .setUnit(rawDataMeasured.getUnit())
                .setDescription(rawDataMeasured.getDescription())
                .build();
    }

    private UnkiteableWindDetected mapToNotKiteableWindDetected(RawDataMeasured rawDataMeasured) {
        return UnkiteableWindDetected.newBuilder()
                .setSensorId(rawDataMeasured.getSensorId())
                .setLocation(rawDataMeasured.getLocation())
                .setValue(rawDataMeasured.getValue())
                .setUnit(rawDataMeasured.getUnit())
                .setDescription(rawDataMeasured.getDescription())
                .build();
    }
}
