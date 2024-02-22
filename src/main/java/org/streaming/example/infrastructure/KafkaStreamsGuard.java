package org.streaming.example.infrastructure;

import org.apache.kafka.streams.KafkaStreams;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.kafka.StreamsBuilderFactoryBeanCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import static org.apache.kafka.streams.KafkaStreams.State.ERROR;

/**
 * A custom state listener to shutdown the application when a fatal error occurs
 */
@Configuration
public class KafkaStreamsGuard implements KafkaStreams.StateListener {

    private final ApplicationContext ctx;

    public KafkaStreamsGuard(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Bean
    StreamsBuilderFactoryBeanCustomizer kafkaAppKillerRegistration(ApplicationContext ctx) {
        return (streamsBuilderFactoryBean) -> streamsBuilderFactoryBean
                .setStateListener(new KafkaStreamsGuard(ctx));
    }

    @Override
    public void onChange(KafkaStreams.State newState, KafkaStreams.State oldState) {
        if(newState == ERROR) {
            SpringApplication.exit(ctx);
            System.exit(1);
        }
    }
}
