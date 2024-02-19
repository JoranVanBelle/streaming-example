package org.streaming.example.infrastructure.processor;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.streaming.example.KiteableWaveDetected;
import org.streaming.example.KiteableWeatherDetected;
import org.streaming.example.KiteableWindDetected;
import org.streaming.example.KiteableWindDirectionDetected;
import org.streaming.example.UnkiteableWaveDetected;
import org.streaming.example.UnkiteableWindDetected;
import org.streaming.example.UnkiteableWindDirectionDetected;
import org.streaming.example.adapter.kafka.KafkaTopicsProperties;
import org.streaming.example.adapter.kafka.WeatherPublisher;
import org.streaming.example.domain.AvroSerdesFactory;
import org.streaming.example.domain.TopologyTest;
import org.streaming.example.mothers.KiteableWaveDetectedMother;
import org.streaming.example.mothers.KiteableWindDirectionDetectedMother;
import org.streaming.example.mothers.KiteableWindSpeedDetectedMother;
import org.streaming.example.mothers.UnkiteableWaveDetectedMother;
import org.streaming.example.mothers.UnkiteableWindDirectionDetectedMother;
import org.streaming.example.mothers.UnkiteableWindSpeedDetectedMother;

import java.util.UUID;

@TopologyTest
public class JoinProcessorTest {

    @MockBean
    WeatherPublisher weatherPublisher;

    @Autowired
    TopologyTestDriver topologyTestDriver;

    @Autowired
    KafkaTopicsProperties kafkaTopicsProperties;

    @Autowired
    private AvroSerdesFactory avroSerdesFactory;

    TestInputTopic<String, Object> windRekeyTopic;
    TestInputTopic<String, Object> waveRekeyTopic;
    TestInputTopic<String, Object> windDirectionRekeyTopic;
    TestOutputTopic<String, SpecificRecord> weatherTopic;

    @BeforeEach
    void setUp() {
        windRekeyTopic = topologyTestDriver.createInputTopic(
                kafkaTopicsProperties.getRekeyedWindDetected(),
                new StringSerializer(),
                avroSerdesFactory.avroSerializer());

        waveRekeyTopic = topologyTestDriver.createInputTopic(
                kafkaTopicsProperties.getRekeyedWaveDetected(),
                new StringSerializer(),
                avroSerdesFactory.avroSerializer());

        windDirectionRekeyTopic = topologyTestDriver.createInputTopic(
                kafkaTopicsProperties.getRekeyedWindDirectionDetected(),
                new StringSerializer(),
                avroSerdesFactory.avroSerializer());

        weatherTopic = topologyTestDriver.createOutputTopic(
                kafkaTopicsProperties.getKiteWeatherDetected(),
                new StringDeserializer(),
                avroSerdesFactory.specificAvroValueDeserializer());
    }

    @Nested
    class GivenASensorCombinationInputs {

        KiteableWaveDetected kiteableWaveDetected;
        KiteableWindDetected kiteableWindDetected;
        KiteableWindDirectionDetected kiteableWindDirectionDetected;
        UnkiteableWaveDetected unkiteableWaveDetected;
        UnkiteableWindDetected unkiteableWindDetected;
        UnkiteableWindDirectionDetected unkiteableWindDirectionDetected;

        @BeforeEach
        void givenASensorCombinationInputs() {
            kiteableWaveDetected = KiteableWaveDetectedMother.newEvent()
                    .withSensorId("Nieuwpoort")
                    .buildEvent();

            kiteableWindDetected = KiteableWindSpeedDetectedMother.newEvent()
                    .withSensorId("Nieuwpoort")
                    .buildEvent();

            kiteableWindDirectionDetected = KiteableWindDirectionDetectedMother.newEvent()
                    .withSensorId("Nieuwpoort")
                    .buildEvent();

            unkiteableWaveDetected = UnkiteableWaveDetectedMother.newEvent()
                    .withSensorId("Nieuwpoort")
                    .buildEvent();

            unkiteableWindDetected = UnkiteableWindSpeedDetectedMother.newEvent()
                    .withSensorId("Nieuwpoort")
                    .buildEvent();

            unkiteableWindDirectionDetected = UnkiteableWindDirectionDetectedMother.newEvent()
                    .withSensorId("Nieuwpoort")
                    .buildEvent();
        }

        @Test
        void thenTheEventsCanBeJoined() {
            // given - when
            waveRekeyTopic.pipeInput(kiteableWaveDetected.getSensorId(), kiteableWaveDetected);
            windRekeyTopic.pipeInput(kiteableWindDetected.getSensorId(), kiteableWindDetected);
            windDirectionRekeyTopic.pipeInput(kiteableWindDirectionDetected.getSensorId(), kiteableWindDirectionDetected);

            // then
            var result = weatherTopic.readKeyValuesToList();
            result.getFirst().equals(new KiteableWeatherDetected(
                    "Nieuwpoort",
                    "Nieuwpoort",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ));
        }
    }
}
