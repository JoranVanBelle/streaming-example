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
import org.streaming.example.adapter.events.KiteableWindSpeedDetected;
import org.streaming.example.adapter.events.WindSpeedDetected;
import org.streaming.example.adapter.kafka.KafkaTopicsProperties;
import org.streaming.example.adapter.kafka.WeatherPublisher;
import org.streaming.example.domain.AvroSerdesFactory;
import org.streaming.example.domain.TopologyTest;
import org.streaming.example.infrastructure.KafkaContainerSupport;
import org.streaming.example.mothers.KiteableWindSpeedDetectedMother;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TopologyTest
public class WindRekeyProcessorTest extends KafkaContainerSupport {

    @MockBean
    WeatherPublisher weatherPublisher;

    @Autowired
    TopologyTestDriver topologyTestDriver;

    @Autowired
    KafkaTopicsProperties kafkaTopicsProperties;

    @Autowired
    private AvroSerdesFactory avroSerdesFactory;
    TestInputTopic<String, Object> windTopic;
    TestOutputTopic<String, SpecificRecord> windRekeyTopic;

    @BeforeEach
    void setUp() {
        windTopic = topologyTestDriver.createInputTopic(
            kafkaTopicsProperties.getWindDetected(),
            new StringSerializer(),
            avroSerdesFactory.avroSerializer());


        windRekeyTopic = topologyTestDriver.createOutputTopic(
            kafkaTopicsProperties.getRekeyedWindDetected(),
            new StringDeserializer(),
            avroSerdesFactory.specificAvroValueDeserializer());
    }

    @Test
    void givenNotKiteableWaves_eventShouldBeRekeyed() {
        //given
        var key = UUID.randomUUID().toString();
        var kiteableWind = KiteableWindSpeedDetectedMother.newEvent()
            .withSensorId("NP-%s-WVC".formatted(key))
            .buildEvent();

        // when
        windTopic.pipeInput(kiteableWind.getSensorId(), new WindSpeedDetected(kiteableWind));

        // then
        var result = windRekeyTopic.readKeyValuesToList();

        assertThat(result.getFirst().key).isEqualTo("Nieuwpoort");
    }

    @Test
    void givenNotKiteableWaves_valueShouldBeTheSame() {
        //given
        var key = UUID.randomUUID().toString();
        var kiteableWind = KiteableWindSpeedDetectedMother.newEvent()
            .withSensorId("NP-%s-WVC".formatted(key))
            .buildEvent();

        // when
        windTopic.pipeInput(kiteableWind.getSensorId(), new WindSpeedDetected(kiteableWind));

        // then
        var result = windRekeyTopic.readKeyValuesToList();

        assertThat(result.getLast().value).isEqualTo(
            new WindSpeedDetected(
                new KiteableWindSpeedDetected(
                    "NP-%s-WVC".formatted(key),
                    "Nieuwpoort - Wind measurement",
                    "1000",
                    "m/s",
                    "Average wind speed (at 10 m height)"
                )
            ));
    }
}
