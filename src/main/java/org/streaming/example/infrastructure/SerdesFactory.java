package org.streaming.example.infrastructure;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.stereotype.Component;
import org.streaming.example.adapter.events.RawDataMeasured;
import org.streaming.example.adapter.events.WaveDetected;
import org.streaming.example.adapter.events.WindDirectionDetected;
import org.streaming.example.adapter.events.WindSpeedDetected;
import org.streaming.example.domain.AvroSerdesFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates serializers and deserializers
 */
@Component
public class SerdesFactory implements AvroSerdesFactory {

    private final String schema_registry;
    private final KafkaProperties kafkaProperties;

    public SerdesFactory(
            @Value("${spring.kafka.properties.schema.registry.url}") String schemaRegistry,
            KafkaProperties kafkaProperties) {
        schema_registry = schemaRegistry;
        this.kafkaProperties = kafkaProperties;
    }

    @Override
    public SpecificAvroSerde<RawDataMeasured> rawDataMeasuredSerde() {
        final SpecificAvroSerde<RawDataMeasured> rawDataMeasuredSerde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put("schema.registry.url", schema_registry);
        rawDataMeasuredSerde.configure(serdeConfig, false);
        return rawDataMeasuredSerde;
    }

    @Override
    public SpecificAvroSerde<WindSpeedDetected> windSpeedDetectedSerde() {
        final SpecificAvroSerde<WindSpeedDetected> rawDataMeasuredSerde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put("schema.registry.url", schema_registry);
        rawDataMeasuredSerde.configure(serdeConfig, false);
        return rawDataMeasuredSerde;
    }

    @Override
    public SpecificAvroSerde<WaveDetected> waveDetectedSerde() {
        final SpecificAvroSerde<WaveDetected> rawDataMeasuredSerde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put("schema.registry.url", schema_registry);
        rawDataMeasuredSerde.configure(serdeConfig, false);
        return rawDataMeasuredSerde;
    }

    @Override
    public SpecificAvroSerde<WindDirectionDetected> windDirectionDetectedSerde() {
        final SpecificAvroSerde<WindDirectionDetected> rawDataMeasuredSerde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put("schema.registry.url", schema_registry);
        rawDataMeasuredSerde.configure(serdeConfig, false);
        return rawDataMeasuredSerde;
    }

    @Override
    public SpecificAvroSerde<?> specificSerde() {
        final SpecificAvroSerde<?> rawDataMeasuredSerde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put("schema.registry.url", schema_registry);
        rawDataMeasuredSerde.configure(serdeConfig, false);
        return rawDataMeasuredSerde;
    }

    @Override
    public <T extends SpecificRecord> Serializer<T> specificAvroValueSerializer() {
        SpecificAvroSerializer<T> serializer = new SpecificAvroSerializer<>();
        serializer.configure(this.withSpecificReaderConfig(this.kafkaProperties.buildProducerProperties((SslBundles)null)), false);
        return serializer;
    }

    @Override
    public <T extends SpecificRecord> Deserializer<T> specificAvroValueDeserializer() {
        SpecificAvroDeserializer<T> deserializer = new SpecificAvroDeserializer<>();
        deserializer.configure(this.withSpecificReaderConfig(this.kafkaProperties.buildConsumerProperties((SslBundles)null)), false);
        return deserializer;
    }

    @Override
    public KafkaAvroSerializer avroSerializer() {
        var kafkaAvroSerializer = new KafkaAvroSerializer();
        kafkaAvroSerializer.configure(kafkaProperties.buildProducerProperties(null), false);
        return kafkaAvroSerializer;
    }

    @Override
    public KafkaAvroDeserializer avroDeserializer() {
        var deserializer = new KafkaAvroDeserializer();
        deserializer.configure(kafkaProperties.buildConsumerProperties(null), false);
        return deserializer;
    }

    private Map<String, Object> withSpecificReaderConfig(Map<String, Object> config) {
        var map = new HashMap<>(config);
        map.put("specific.avro.reader", true);
        return map;
    }
}
