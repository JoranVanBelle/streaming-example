package org.streaming.example.infrastructure;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.streaming.example.domain.AvroSerdesFactory;

@Configuration
public class AvroSerdesCustomizer {

    @Bean
    @SuppressWarnings("unchecked")
    DefaultKafkaProducerFactoryCustomizer serdesProducerFactoryCustomizer(AvroSerdesFactory serdeFactory) {
        return new DefaultKafkaProducerFactoryCustomizer() {
            @Override
            public void customize(DefaultKafkaProducerFactory producerFactory) {
                producerFactory.setKeySerializerSupplier(StringSerializer::new);
                producerFactory.setValueSerializerSupplier(serdeFactory::avroSerializer);
            }
        };
    }

    @Bean
    @SuppressWarnings("unchecked")
    DefaultKafkaConsumerFactoryCustomizer serdesConsumerFactoryCustomizer(AvroSerdesFactory serdeFactory) {
        return new DefaultKafkaConsumerFactoryCustomizer() {
            @Override
            public void customize(DefaultKafkaConsumerFactory consumerFactory) {
                consumerFactory.setKeyDeserializerSupplier(StringDeserializer::new);
                consumerFactory.setValueDeserializerSupplier(serdeFactory::avroDeserializer);
            }
        };
    }
}
