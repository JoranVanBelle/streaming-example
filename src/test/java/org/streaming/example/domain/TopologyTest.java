package org.streaming.example.domain;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.streaming.example.adapter.kafka.KafkaTopicsProperties;
import org.streaming.example.infrastructure.MockAvroSerdesConfiguration;
import org.streaming.example.infrastructure.TopologyConfiguration;
import org.streaming.example.infrastructure.TopologyTestDriverConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@ExtendWith({SpringExtension.class})
@EnableConfigurationProperties({KafkaProperties.class, KafkaTopicsProperties.class})
@Import({MockAvroSerdesConfiguration.class, TopologyConfiguration.class, TopologyTestDriverConfiguration.class})
@ImportAutoConfiguration
@OverrideAutoConfiguration(enabled = false)
@ActiveProfiles("test")
public @interface TopologyTest {
}
