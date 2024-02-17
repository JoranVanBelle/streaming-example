package org.streaming.example.domain.kafka;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.state.StoreBuilder;

import java.util.List;

import static org.apache.kafka.streams.state.Stores.keyValueStoreBuilder;
import static org.apache.kafka.streams.state.Stores.persistentKeyValueStore;

public class StateStoreDefinition {
    private final String name;
    private final String[] processors;
    private final Serde<?> valueSerdes;

    public StateStoreDefinition(String name, String[] processors, Serde<?> valueSerdes) {
        this.name = name;
        this.processors = processors;
        this.valueSerdes = valueSerdes;
    }

    public String name() {
        return name;
    }

    public String[] processors() {
        return processors;
    }

    public Serde<?> valueSerdes() {
        return valueSerdes;
    }

    public StoreBuilder<?> storeBuilder() {
        return keyValueStoreBuilder(
                persistentKeyValueStore(name),
                Serdes.String(),
                valueSerdes);
    }

    public static StateStoreDefinitionBuilder newStateStoreDefinition() {
        return new StateStoreDefinitionBuilder();
    }

    public static class StateStoreDefinitionBuilder {
        private String name;
        private String[] processors;
        private Serde<?> valueSerdes;

        public StateStoreDefinitionBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public StateStoreDefinitionBuilder withProcessors(List<String> processors) {
            this.processors = processors.toArray(String[]::new);
            return this;
        }

        public StateStoreDefinitionBuilder withProcessors(String... processors) {
            this.processors = processors;
            return this;
        }

        public StateStoreDefinitionBuilder withValueSerdes(Serde<?> valueSerdes) {
            this.valueSerdes = valueSerdes;
            return this;
        }

        public StateStoreDefinition build() {
            return new StateStoreDefinition(name, processors, valueSerdes);
        }
    }
}
