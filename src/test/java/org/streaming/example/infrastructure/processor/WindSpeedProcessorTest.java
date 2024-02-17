package org.streaming.example.infrastructure.processor;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.streaming.example.adapter.kafka.KafkaTopicsProperties;
import org.streaming.example.adapter.kafka.WeatherPublisher;
import org.streaming.example.domain.AvroSerdesFactory;
import org.streaming.example.domain.TopologyTest;
import org.streaming.example.mothers.RawWaveHeightMeasured;
import org.streaming.example.mothers.RawWindSpeedMeasured;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TopologyTest
class WindSpeedProcessorTest {

    @MockBean
    WeatherPublisher weatherPublisher;
    @Autowired
    TopologyTestDriver topologyTestDriver;
    @Autowired
    KafkaTopicsProperties kafkaTopicsProperties;
    @Autowired
    private AvroSerdesFactory avroSerdesFactory;
    TestInputTopic<String, Object> rawDataMeasuredTopic;
    TestOutputTopic<String, SpecificRecord> windTopic;

    @BeforeEach
    void setUp() {
        rawDataMeasuredTopic = topologyTestDriver.createInputTopic(
                kafkaTopicsProperties.getRawDataMeasured(),
                new StringSerializer(),
                avroSerdesFactory.avroSerializer());

        windTopic = topologyTestDriver.createOutputTopic(
                kafkaTopicsProperties.getWindDetected(),
                new StringDeserializer(),
                avroSerdesFactory.specificAvroValueDeserializer());
    }

    @Test
    void givenNotKiteableWind_whenNotKiteableWindDetected_oneEventShouldBeFound() {
        // given
        var key = UUID.randomUUID().toString();
        var notKiteableWind = RawWindSpeedMeasured.newEvent().withNotKiteableWind().withSensorId("NP-%s-WVC".formatted(key)).withSensorId("NP-%s-WVC".formatted(key)).build();
        var notKiteableWind2 = RawWindSpeedMeasured.newEvent().withNotKiteableWind().withSensorId("NP-%s-WVC".formatted(key)).withSensorId("NP-%s-WVC".formatted(key)).build();

        // when
        rawDataMeasuredTopic.pipeInput(notKiteableWind.getSensorId(), notKiteableWind);
        rawDataMeasuredTopic.pipeInput(notKiteableWind2.getSensorId(), notKiteableWind2);

        // then
        var result = windTopic.readKeyValuesToList();

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void givenKiteableWind_whenKiteableWindDetected_oneEventShouldBeFound() {
        // given
        var key = UUID.randomUUID().toString();
        var kiteableWind = RawWindSpeedMeasured.newEvent()
                .withKiteableWind()
                .withSensorId("NP-%s-WVC".formatted(key))
                .build();
        var kiteableWind2 = RawWindSpeedMeasured.newEvent()
                .withKiteableWind()
                .withSensorId("NP-%s-WVC".formatted(key))
                .build();

        // when
        rawDataMeasuredTopic.pipeInput(kiteableWind.getSensorId(), kiteableWind);
        rawDataMeasuredTopic.pipeInput(kiteableWind2.getSensorId(), kiteableWind2);

        // then
        var result = windTopic.readKeyValuesToList();

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void givenKiteableWind_whenNotKiteableWindDetected_oneEventShouldBeFound() {
        // given
        var key = UUID.randomUUID().toString();
        var kiteableWind = RawWindSpeedMeasured.newEvent()
                .withKiteableWind()
                .withSensorId("NP-%s-WVC".formatted(key))
                .build();
        var notKiteableWind = RawWindSpeedMeasured.newEvent()
                .withNotKiteableWind()
                .withSensorId("NP-%s-WVC".formatted(key))
                .build();

        // when
        rawDataMeasuredTopic.pipeInput(kiteableWind.getSensorId(), kiteableWind);
        rawDataMeasuredTopic.pipeInput(notKiteableWind.getSensorId(), notKiteableWind);

        // then
        var result = windTopic.readKeyValuesToList();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void givenNotKiteableWind_whenKiteableWindDetected_oneEventShouldBeFound() {
        // given
        var key = UUID.randomUUID().toString();
        var notKiteableWind = RawWindSpeedMeasured.newEvent()
                .withNotKiteableWind()
                .withSensorId("NP-%s-WVC".formatted(key))
                .build();
        var kiteableWind = RawWindSpeedMeasured.newEvent()
                .withKiteableWind()
                .withSensorId("NP-%s-WVC".formatted(key))
                .build();

        // when
        rawDataMeasuredTopic.pipeInput(notKiteableWind.getSensorId(), notKiteableWind);
        rawDataMeasuredTopic.pipeInput(kiteableWind.getSensorId(), kiteableWind);

        // then
        var result = windTopic.readKeyValuesToList();

        assertThat(result.size()).isEqualTo(2);
    }
}