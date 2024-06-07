package org.streaming.example.infrastructure;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.streaming.example.adapter.kafka.KafkaTopicsProperties;

/**
 * Creates the topics for the integration test
 */
@Profile("it-test")
@Configuration
public class TopicConfiguration {

    private final KafkaProperties kafkaProperties;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    public TopicConfiguration(KafkaProperties kafkaProperties, KafkaTopicsProperties kafkaTopicsProperties) {
        this.kafkaProperties = kafkaProperties;
        this.kafkaTopicsProperties = kafkaTopicsProperties;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(kafkaProperties.buildAdminProperties((SslBundles) null));
    }

    @Bean
    public KafkaAdmin.NewTopics topics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(kafkaTopicsProperties.getRawDataMeasured()).build(),
                TopicBuilder.name(kafkaTopicsProperties.getWindDetected()).build(),
                TopicBuilder.name(kafkaTopicsProperties.getWaveDetected()).build(),
                TopicBuilder.name(kafkaTopicsProperties.getWindDirectionDetected()).build(),
                TopicBuilder.name(kafkaTopicsProperties.getRekeyedWindDetected()).build(),
                TopicBuilder.name(kafkaTopicsProperties.getRekeyedWaveDetected()).build(),
                TopicBuilder.name(kafkaTopicsProperties.getRekeyedWindDirectionDetected()).build(),
                TopicBuilder.name(kafkaTopicsProperties.getKiteWeatherDetected()).build(),
                TopicBuilder.name(kafkaTopicsProperties.getFeedbackGiven()).build()
        );
    }

}
