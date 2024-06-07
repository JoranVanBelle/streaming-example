package org.streaming.example.infrastructure.processor;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.streaming.example.adapter.events.WindDirectionDetected;
import org.streaming.example.domain.meetnetvlaamsebanken.LocationKeyMapping;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;

public class WindDirectionRekeyProcessor implements Processor<String, WindDirectionDetected, String, WindDirectionDetected> {

    public static final String NAME = WindDirectionRekeyProcessor.class.getSimpleName();
    private final LocationKeyMapping locationKeyMapping;
    private final Clock clock;
    private ProcessorContext<String, WindDirectionDetected> context;

    public WindDirectionRekeyProcessor(Clock clock, LocationKeyMapping locationKeyMapping) {
        this.locationKeyMapping = locationKeyMapping;
        this.clock = clock;
    }

    @Override
    public void init(ProcessorContext<String, WindDirectionDetected> context) {
        this.context = context;
    }

    @Override
    public void process(Record<String, WindDirectionDetected> record) {
        final String newKey = getNewKey(record.key());
        context.forward(new Record<>(newKey, record.value(), Instant.now(clock).toEpochMilli(), new RecordHeaders()));
    }

    private String getNewKey(String key) {
        return locationKeyMapping.getMapping().entrySet().stream()
                .filter(e -> key.contains(e.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new IllegalArgumentException("Key %s is not know in the mapping".formatted(key)));
    }
}
