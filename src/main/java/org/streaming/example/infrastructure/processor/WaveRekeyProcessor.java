package org.streaming.example.infrastructure.processor;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.example.adapter.events.WaveDetected;
import org.streaming.example.domain.meetnetvlaamsebanken.LocationKeyMapping;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;

public class WaveRekeyProcessor implements Processor<String, WaveDetected, String, WaveDetected> {

    private final Logger logger = LoggerFactory.getLogger(WaveRekeyProcessor.class.getSimpleName());

    public static final String NAME = WaveRekeyProcessor.class.getSimpleName();
    private final LocationKeyMapping locationKeyMapping;
    private final Clock clock;
    private ProcessorContext<String, WaveDetected> context;

    public WaveRekeyProcessor(Clock clock, LocationKeyMapping locationKeyMapping) {
        this.locationKeyMapping = locationKeyMapping;
        this.clock = clock;
    }

    @Override
    public void init(ProcessorContext<String, WaveDetected> context) {
        this.context = context;
    }

    @Override
    public void process(Record<String, WaveDetected> record) {
        logger.info("rekeying key: " + record.key());
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
