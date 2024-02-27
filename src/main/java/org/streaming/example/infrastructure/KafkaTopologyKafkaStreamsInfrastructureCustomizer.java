package org.streaming.example.infrastructure;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.KafkaStreamsInfrastructureCustomizer;
import org.streaming.example.domain.kafka.TopologyDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This will create the topologies defined as {@link TopologyDefinition}
 */
public class KafkaTopologyKafkaStreamsInfrastructureCustomizer implements KafkaStreamsInfrastructureCustomizer {

    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaTopologyKafkaStreamsInfrastructureCustomizer.class.getSimpleName());

    private final List<TopologyDefinition> topologyDefinitions;

    public KafkaTopologyKafkaStreamsInfrastructureCustomizer(List<TopologyDefinition> topologyDefinitions) {
        this.topologyDefinitions = topologyDefinitions;
    }

    @Override
    public void configureTopology(Topology topology) {
        topologyDefinitions.forEach(def -> {
            def.sources().forEach(s -> topology.addSource(source(s.topic()), new StringDeserializer(), s.valueDeserializer(), s.topic()));
            def.processors().forEach(p -> topology.addProcessor(p.name(), p.supplier(), determineParentOfProcessor(p.parents())));
            def.sinks().forEach(s -> topology.addSink(sink(s.topic()), s.topic(), new StringSerializer(), s.valueSerializer(), s.parents()));
            def.stateStores().forEach(s -> topology.addStateStore(s.storeBuilder(), s.processors()));
            LOGGER.info("Current topology: \n%s".formatted(topology.describe()));
        });
    }

    private String[] determineParentOfProcessor(String[] parents) {
        var parentsList = new ArrayList<String>();

        Arrays.stream(parents).toList().forEach(p -> {
            if(p.toLowerCase().contains("processor")) {
                parentsList.add(p);
            } else {
                parentsList.add("%s-source".formatted(p));
            }
        });

        return parentsList.toArray(String[]::new);
    }

    public static String source(String topic) {
        return "%s-source".formatted(topic);
    }

    public static String[] source(String[] topics) {
        return Arrays.stream(topics).map("%s-source"::formatted).toArray(String[]::new);
    }

    public static String processor(String processor) {
        return "%s-processor".formatted(processor);
    }

    public static String[] processor(String[] processors) {
        return Arrays.stream(processors).map("%s-processor"::formatted).toArray(String[]::new);
    }

    public static String sink(String topic) {
        return "%s-sink".formatted(topic);
    }
}
