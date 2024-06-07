package org.streaming.example.infrastructure.processor;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.streaming.example.adapter.events.WindSpeedDetected;

import java.time.Clock;
import java.time.Instant;

public class WindSpeedStateStorePopulatorProcessor implements Processor<String, WindSpeedDetected, String, WindSpeedDetected> {

    public static final String REKEYED_WIND_SPEED_STATE_STORE_NAME = "WindSpeedStateStorePopulatorStateStore";
    public static final String NAME = WindSpeedStateStorePopulatorProcessor.class.getSimpleName();
    private final Clock clock;

    private ProcessorContext<String, WindSpeedDetected> context;
    private KeyValueStore<String, WindSpeedDetected> keyValueStore;

    public WindSpeedStateStorePopulatorProcessor(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void init(ProcessorContext<String, WindSpeedDetected> context) {
        this.context = context;
        keyValueStore = context.getStateStore(REKEYED_WIND_SPEED_STATE_STORE_NAME);
    }

    @Override
    public void process(Record<String, WindSpeedDetected> record) {
        keyValueStore.put(record.key(), record.value());

        context.forward(new Record<>(record.key(), null, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
    }
}
