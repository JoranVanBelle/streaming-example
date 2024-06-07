package org.streaming.example.infrastructure.processor;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.streaming.example.adapter.events.WindDirectionDetected;

import java.time.Clock;
import java.time.Instant;

public class WindDirectionStateStorePopulatorProcessor implements Processor<String, WindDirectionDetected, String, WindDirectionDetected> {

    public static final String REKEYED_WIND_DIRECTION_STATE_STORE_NAME = "WindDirectionStateStorePopulatorStateStore";
    public static final String NAME = WindDirectionStateStorePopulatorProcessor.class.getSimpleName();
    private final Clock clock;

    private ProcessorContext<String, WindDirectionDetected> context;
    private KeyValueStore<String, WindDirectionDetected> keyValueStore;

    public WindDirectionStateStorePopulatorProcessor(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void init(ProcessorContext<String, WindDirectionDetected> context) {
        this.context = context;
        keyValueStore = context.getStateStore(REKEYED_WIND_DIRECTION_STATE_STORE_NAME);
    }

    @Override
    public void process(Record<String, WindDirectionDetected> record) {
        keyValueStore.put(record.key(), record.value());

        context.forward(new Record<>(record.key(), null, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
    }
}
