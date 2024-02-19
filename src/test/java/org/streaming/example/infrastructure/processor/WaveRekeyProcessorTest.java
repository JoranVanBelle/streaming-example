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
import org.streaming.example.KiteableWaveDetected;
import org.streaming.example.adapter.kafka.KafkaTopicsProperties;
import org.streaming.example.adapter.kafka.WeatherPublisher;
import org.streaming.example.domain.AvroSerdesFactory;
import org.streaming.example.domain.TopologyTest;
import org.streaming.example.mothers.KiteableWaveDetectedMother;
import org.streaming.example.mothers.RawWaveHeightMeasured;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TopologyTest
class WaveRekeyProcessorTest {

    @MockBean
    WeatherPublisher weatherPublisher;

    @Autowired
    TopologyTestDriver topologyTestDriver;

    @Autowired
    KafkaTopicsProperties kafkaTopicsProperties;

    @Autowired
    private AvroSerdesFactory avroSerdesFactory;
    TestInputTopic<String, Object> waveTopic;
    TestOutputTopic<String, SpecificRecord> waveRekeyTopic;

    @BeforeEach
    void setUp() {
        waveTopic = topologyTestDriver.createInputTopic(
                kafkaTopicsProperties.getWaveDetected(),
                new StringSerializer(),
                avroSerdesFactory.avroSerializer());


        waveRekeyTopic = topologyTestDriver.createOutputTopic(
                kafkaTopicsProperties.getRekeyedWaveDetected(),
                new StringDeserializer(),
                avroSerdesFactory.specificAvroValueDeserializer());
    }

    @Test
    void givenNotKiteableWaves_eventShouldBeRekeyed() {
        //given
        var key = UUID.randomUUID().toString();
        var kiteableWaves = KiteableWaveDetectedMother.newEvent()
                .withSensorId("NP-%s-GH1".formatted(key))
                .buildEvent();

        // when
        waveTopic.pipeInput(kiteableWaves.getSensorId(), kiteableWaves);

        // then
        var result = waveRekeyTopic.readRecordsToList();

        assertThat(result.getLast().key()).isEqualTo("Nieuwpoort");
    }

    @Test
    void givenNotKiteableWaves_valueShouldBeTheSame() {
        //given
        var key = UUID.randomUUID().toString();
        var kiteableWaves = KiteableWaveDetectedMother.newEvent()
                .withSensorId("NP-%s-GH1".formatted(key))
                .buildEvent();

        // when
        waveTopic.pipeInput(kiteableWaves.getSensorId(), kiteableWaves);

        // then
        var result = waveRekeyTopic.readRecordsToList();

        assertThat(result.getLast().value()).isEqualTo(new KiteableWaveDetected(
                "NP-%s-GH1".formatted(key),
                "Nieuwpoort - Buoy",
                "39.0",
                "cm",
                "10% highest waves"
        ));
    }
}