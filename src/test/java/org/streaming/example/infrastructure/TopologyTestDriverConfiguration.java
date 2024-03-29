package org.streaming.example.infrastructure;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.KafkaStreamsInfrastructureCustomizer;

import java.util.Properties;

@Profile("test")
@Configuration
public class TopologyTestDriverConfiguration {

    private final KafkaProperties kafkaProperties;

    public TopologyTestDriverConfiguration(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * To create a topologyTestDriver so that the topology can be tested
     * @param infrastructureCustomizerProvider
     * @return a bean of a TopologyTestDriver
     */
    @Bean
    @DependsOn("streamsInfrastructureCustomizer")
    TopologyTestDriver defaultTopologyTestDriver(
            ObjectProvider<KafkaStreamsInfrastructureCustomizer> infrastructureCustomizerProvider) {
        var streamsProperties = kafkaProperties.buildStreamsProperties(null);
        var streamsConfig = new StreamsConfig(streamsProperties);
        var topologyConfig = new TopologyConfig(streamsConfig);
        var streamsBuilder = new StreamsBuilder(topologyConfig);
        var iterator = infrastructureCustomizerProvider.iterator();
        while (iterator.hasNext()) {
            iterator.next().configureBuilder(streamsBuilder);
        }
        var topology = streamsBuilder.build();
        iterator = infrastructureCustomizerProvider.iterator();
        while (iterator.hasNext()) {
            iterator.next().configureTopology(topology);
        }
        var properties = new Properties();
        properties.putAll(streamsProperties);
        return new TopologyTestDriver(topology, properties);
    }
}
