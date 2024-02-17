package org.streaming.example.infrastructure;

import org.springframework.boot.autoconfigure.kafka.StreamsBuilderFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsInfrastructureCustomizer;
import org.streaming.example.domain.kafka.TopologyDefinition;

import java.util.List;

@Configuration
public class TopologyConfiguration {
    @Bean
    StreamsBuilderFactoryBeanCustomizer streamsInfrastructureCustomizer(
            KafkaStreamsInfrastructureCustomizer customizer) {
        return (streamsBuilderFactoryBean) -> streamsBuilderFactoryBean.setInfrastructureCustomizer(customizer);
    }

    @Bean
    public KafkaStreamsInfrastructureCustomizer customizer(List<TopologyDefinition> definitions) {
        return new KafkaTopologyCustomizer(definitions);
    }
}
