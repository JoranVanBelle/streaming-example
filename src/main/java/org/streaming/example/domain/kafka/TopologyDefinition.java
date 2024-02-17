package org.streaming.example.domain.kafka;

import java.util.List;

public interface TopologyDefinition {
    List<StateStoreDefinition> stateStores();

    List<SourceDefinition> sources();

    List<SinkDefinition> sinks();

    List<ProcessorDefinition<?, ?, ?, ?>> processors();

}
