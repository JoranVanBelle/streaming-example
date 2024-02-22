package org.streaming.example.infrastructure;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.kafka.StreamsBuilderFactoryBeanCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.streaming.example.adapter.kafka.KafkaTopicsProperties;

import java.util.concurrent.ExecutionException;

import static org.apache.kafka.clients.admin.AdminClient.create;

@Configuration
public class TopologyTopicCreationStreamsBuilderFactoryBeanCustomizer implements StreamsBuilderFactoryBeanCustomizer {

    private final KafkaTopicsProperties kafkaTopicsProperties;
    private final KafkaProperties kafkaProperties;

    public TopologyTopicCreationStreamsBuilderFactoryBeanCustomizer(KafkaTopicsProperties kafkaTopicsProperties, KafkaProperties kafkaProperties) {
        this.kafkaTopicsProperties = kafkaTopicsProperties;
        this.kafkaProperties = kafkaProperties;
    }

    @Override
    public void customize(StreamsBuilderFactoryBean factoryBean) {
        try (var admin = create(kafkaProperties.buildAdminProperties(null))) {
            try {
                admin.createTopics(kafkaTopicsProperties.topics().stream().map(name -> new NewTopic(name, 3, (short) 1)).toList()).all().get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
