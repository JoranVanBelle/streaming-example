package org.streaming.example.infrastructure.processor;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.example.KiteableWindDirectionDetected;
import org.streaming.example.RawDataMeasured;
import org.streaming.example.UnkiteableWindDirectionDetected;

import java.time.Clock;
import java.time.Instant;

public class WindDirectionProcessor implements Processor<String, RawDataMeasured, String, SpecificRecord> {

    private final Logger logger = LoggerFactory.getLogger(WindDirectionProcessor.class.getSimpleName());
    public static final String WIND_DIRECTION_PROCESSOR_STATE_STORE_NAME = "WindDirectionProcessorStateStore";
    public static final String NAME = WindDirectionProcessor.class.getSimpleName();
    private static final String WIND_DIRECTION_SENSOR_ID = "WRS";
    private ProcessorContext<String, SpecificRecord> context;
    private KeyValueStore<String, RawDataMeasured> keyValueStore;
    private final Clock clock;
    private final double fromTresholdWindDirection;

    private final double untilTresholdWindDirection;

    public WindDirectionProcessor(Clock clock, double fromTresholdWindDirection, double untilTresholdWindDirection) {
        this.clock = clock;
        this.fromTresholdWindDirection = fromTresholdWindDirection;
        this.untilTresholdWindDirection = untilTresholdWindDirection;
    }

    @Override
    public void init(ProcessorContext<String, SpecificRecord> context) {
        this.context = context;
        keyValueStore = context.getStateStore(WIND_DIRECTION_PROCESSOR_STATE_STORE_NAME);
    }

    @Override
    public void process(Record<String, RawDataMeasured> event) {

        if(isWindDirectionEvent(event)) {
            logger.info("WindDirection event found: %s".formatted(event.value()));

            var newValue = Double.valueOf(event.value().getValue());
            var newWindDirectionValue = Double.valueOf(event.value().getValue());

            var storedEvent = keyValueStore.get(event.key());

            if (storedEvent != null) {
                var oldWindDirectionValue = Double.parseDouble(storedEvent.getValue());

                if (oldWindDirectionValue == newWindDirectionValue) {
                    return;
                }

                if (windDirectionIsKiteable(oldWindDirectionValue)) {
                    // kiteable - old
                    if (windDirectionIsKiteable(newValue)) {
                        return;
                    } else {
                        keyValueStore.put(event.key(), event.value());
                        var notKiteableWind = mapToNotKiteableWindDirectionDetected(event.value());
                        context.forward(new Record<>(notKiteableWind.getSensorId(), notKiteableWind, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
                    }
                } else {
                    // not kiteable - old
                    if (windDirectionIsKiteable(newValue)) {
                        keyValueStore.put(event.key(), event.value());
                        var kiteableWind = mapToKiteableWindDirectionDetected(event.value());
                        context.forward(new Record<>(kiteableWind.getSensorId(), kiteableWind, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
                    } else {
                        return;
                    }
                }
            } else {
                keyValueStore.put(event.key(), event.value());
                if (windDirectionIsKiteable(newValue)) {
                    var kiteableWind = mapToKiteableWindDirectionDetected(event.value());
                    context.forward(new Record<>(kiteableWind.getSensorId(), kiteableWind, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
                } else {
                    var notKiteableWind = mapToNotKiteableWindDirectionDetected(event.value());
                    context.forward(new Record<>(notKiteableWind.getSensorId(), notKiteableWind, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
                }
            }
        }
    }

    private boolean isWindDirectionEvent(Record<String, RawDataMeasured> event) {
        return event.key().contains(WIND_DIRECTION_SENSOR_ID);
    }

    private boolean windDirectionIsKiteable(Double value) {
        if(value < 0 || value > 360) {
            return false;
        }
        return !(value <= fromTresholdWindDirection && value >= untilTresholdWindDirection);
    }

    private KiteableWindDirectionDetected mapToKiteableWindDirectionDetected(RawDataMeasured rawDataMeasured) {
        return KiteableWindDirectionDetected.newBuilder()
                .setSensorId(rawDataMeasured.getSensorId())
                .setLocation(rawDataMeasured.getLocation())
                .setValue(rawDataMeasured.getValue())
                .setUnit(rawDataMeasured.getUnit())
                .setDescription(rawDataMeasured.getDescription())
                .build();
    }

    private UnkiteableWindDirectionDetected mapToNotKiteableWindDirectionDetected(RawDataMeasured rawDataMeasured) {
        return UnkiteableWindDirectionDetected.newBuilder()
                .setSensorId(rawDataMeasured.getSensorId())
                .setLocation(rawDataMeasured.getLocation())
                .setValue(rawDataMeasured.getValue())
                .setUnit(rawDataMeasured.getUnit())
                .setDescription(rawDataMeasured.getDescription())
                .build();
    }
}
