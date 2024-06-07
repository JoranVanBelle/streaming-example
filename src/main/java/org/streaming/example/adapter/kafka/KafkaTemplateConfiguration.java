package org.streaming.example.adapter.kafka;

import org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.streaming.example.FeedbackGiven;
import org.streaming.example.RawDataMeasured;

@Configuration
public class KafkaTemplateConfiguration {

    private final KafkaConnectionDetails details;
    private final KafkaProperties kafkaProperties;

    public KafkaTemplateConfiguration(KafkaConnectionDetails details, KafkaProperties kafkaProperties) {
        this.details = details;
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public KafkaTemplate<String, FeedbackGiven> feedbackGivenKafkaTemplate(ProducerFactory<String, FeedbackGiven> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTemplate<String, RawDataMeasured> rawDataMeasuredKafkaTemplate(ProducerFactory<String, RawDataMeasured> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, FeedbackGiven> feedbackProducerFactory() {
        kafkaProperties.setBootstrapServers(details.getBootstrapServers());
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties(null));
    }

    @Bean
    public ProducerFactory<String, RawDataMeasured> rawDataMeasuredProducerFactory() {
        kafkaProperties.setBootstrapServers(details.getBootstrapServers());
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties(null));
    }

}
