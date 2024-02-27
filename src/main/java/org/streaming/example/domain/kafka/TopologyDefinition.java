package org.streaming.example.domain.kafka;

import java.util.List;

/**
 * The topology definition of the application
 */
public interface TopologyDefinition {
    List<StateStoreDefinition> stateStores();

    List<SourceDefinition> sources();

    List<SinkDefinition> sinks();

    List<ProcessorDefinition<?, ?, ?, ?>> processors();

}
