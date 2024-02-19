package org.streaming.example.infrastructure;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.testutil.MockSchemaRegistry;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.streaming.example.RawDataMeasured;
import org.streaming.example.domain.AvroSerdesFactory;

import java.util.HashMap;
import java.util.Map;

@Primary
@Component
@Profile({ "test" })
public class MockAvroSerdesConfiguration implements AvroSerdesFactory {

    private final SchemaRegistryClient client = MockSchemaRegistry.getClientForScope("test-scope");
    private final String schema_registry = "mock://schema-registry";

    @Override
    public SpecificAvroSerde<RawDataMeasured> rawDataMeasuredSerde() {
        final SpecificAvroSerde<RawDataMeasured> rawDataMeasuredSerde = new SpecificAvroSerde<>();
        Map<String, Object> serdeConfig = new HashMap<>();
        serdeConfig.put("schema.registry.url", schema_registry);
        serdeConfig.put("auto.register.schemas", true);
        serdeConfig.put("value.subject.name.strategy", TopicRecordNameStrategy.class);
        serdeConfig.put("specific.avro.reader", true);
        rawDataMeasuredSerde.configure(serdeConfig, false);
        return rawDataMeasuredSerde;
    }

    @Override
    public SpecificAvroSerde<?> specificSerde() {
        final SpecificAvroSerde<?> specificSerde= new SpecificAvroSerde<>();
        Map<String, Object> serdeConfig = new HashMap<>();
        serdeConfig.put("schema.registry.url", schema_registry);
        serdeConfig.put("auto.register.schemas", true);
        serdeConfig.put("value.subject.name.strategy", TopicRecordNameStrategy.class);
        serdeConfig.put("specific.avro.reader", true);
        specificSerde.configure(serdeConfig, false);
        return specificSerde;
    }

    @Override
    public <T extends SpecificRecord> Deserializer<T> specificAvroValueDeserializer() {
        Deserializer<T> deserializer = (new SpecificAvroSerde<T>(this.client)).deserializer();
        deserializer.configure(this.getSpecificDeserializerProperties(), false);
        return deserializer;
    }

    private Map<String, Object> getSpecificDeserializerProperties() {
        var config = new HashMap<>(this.getSerializerProperties());
        config.put("specific.avro.reader", true);
        return config;
    }

    @Override
    public KafkaAvroSerializer avroSerializer() {
        return new KafkaAvroSerializer(this.client, getSerializerProperties());
    }

    private Map<String, Object> getSerializerProperties() {
        return Map.of("schema.registry.url", "mock://schema-url", "auto.register.schemas", true, "value.subject.name.strategy", TopicRecordNameStrategy.class);
    }

    @Override
    public KafkaAvroDeserializer avroDeserializer() {
        return new KafkaAvroDeserializer(this.client);
    }
}
