package org.streaming.example.infrastructure.processor;

import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.streaming.example.adapter.events.WaveDetected;

import java.time.Clock;
import java.time.Instant;

public class WaveStateStorePopulatorProcessor implements Processor<String, WaveDetected, String, WaveDetected> {

    public static final String REKEYED_WAVE_STATE_STORE_NAME = "WaveStateStorePopulatorStateStore";
    public static final String NAME = WaveStateStorePopulatorProcessor.class.getSimpleName();
    private final Clock clock;

    private ProcessorContext<String, WaveDetected> context;
    private KeyValueStore<String, WaveDetected> keyValueStore;

    public WaveStateStorePopulatorProcessor(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void init(ProcessorContext<String, WaveDetected> context) {
        this.context = context;
        keyValueStore = context.getStateStore(REKEYED_WAVE_STATE_STORE_NAME);
    }

    @Override
    public void process(Record<String, WaveDetected> record) {
        keyValueStore.put(record.key(), record.value());

        context.forward(new Record<>(record.key(), null, Instant.now(clock).toEpochMilli(), new RecordHeaders()));
    }
}
