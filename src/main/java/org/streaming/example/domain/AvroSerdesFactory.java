package org.streaming.example.domain;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.streaming.example.adapter.events.RawDataMeasured;
import org.streaming.example.adapter.events.WaveDetected;
import org.streaming.example.adapter.events.WindDirectionDetected;
import org.streaming.example.adapter.events.WindSpeedDetected;

public interface AvroSerdesFactory {
    SpecificAvroSerde<RawDataMeasured> rawDataMeasuredSerde();
    SpecificAvroSerde<WindSpeedDetected> windSpeedDetectedSerde();
    SpecificAvroSerde<WaveDetected> waveDetectedSerde();
    SpecificAvroSerde<WindDirectionDetected> windDirectionDetectedSerde();
    SpecificAvroSerde<?> specificSerde();
    <T extends SpecificRecord>Serializer<T> specificAvroValueSerializer();
    <T extends SpecificRecord> Deserializer<T> specificAvroValueDeserializer();
    KafkaAvroSerializer avroSerializer();
    KafkaAvroDeserializer avroDeserializer();
}
