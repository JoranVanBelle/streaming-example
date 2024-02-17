package org.streaming.example.domain;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.streaming.example.RawDataMeasured;

public interface AvroSerdesFactory {
    SpecificAvroSerde<RawDataMeasured> rawDataMeasuredSerde();
    <T extends SpecificRecord> Deserializer<T> specificAvroValueDeserializer();
    KafkaAvroSerializer avroSerializer();
    KafkaAvroDeserializer avroDeserializer();
}
