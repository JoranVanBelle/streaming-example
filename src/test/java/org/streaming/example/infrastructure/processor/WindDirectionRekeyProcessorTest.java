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
import org.streaming.example.KiteableWindDirectionDetected;
import org.streaming.example.adapter.kafka.KafkaTopicsProperties;
import org.streaming.example.adapter.kafka.WeatherPublisher;
import org.streaming.example.domain.AvroSerdesFactory;
import org.streaming.example.domain.TopologyTest;
import org.streaming.example.infrastructure.KafkaContainerSupport;
import org.streaming.example.mothers.KiteableWindDirectionDetectedMother;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TopologyTest
public class WindDirectionRekeyProcessorTest extends KafkaContainerSupport {

    @MockBean
    WeatherPublisher weatherPublisher;

    @Autowired
    TopologyTestDriver topologyTestDriver;

    @Autowired
    KafkaTopicsProperties kafkaTopicsProperties;

    @Autowired
    private AvroSerdesFactory avroSerdesFactory;
    TestInputTopic<String, Object> windDirectionTopic;
    TestOutputTopic<String, SpecificRecord> windDirectionRekeyTopic;

    @BeforeEach
    void setUp() {
        windDirectionTopic = topologyTestDriver.createInputTopic(
                kafkaTopicsProperties.getWindDirectionDetected(),
                new StringSerializer(),
                avroSerdesFactory.avroSerializer());


        windDirectionRekeyTopic = topologyTestDriver.createOutputTopic(
                kafkaTopicsProperties.getRekeyedWindDirectionDetected(),
                new StringDeserializer(),
                avroSerdesFactory.specificAvroValueDeserializer());
    }

    @Test
    void givenNotKiteableWaves_eventShouldBeRekeyed() {
        //given
        var key = UUID.randomUUID().toString();
        var kiteableWind = KiteableWindDirectionDetectedMother.newEvent()
                .withSensorId("NP-%s-WRS".formatted(key))
                .buildEvent();

        // when
        windDirectionTopic.pipeInput(kiteableWind.getSensorId(), kiteableWind);

        // then
        var result = windDirectionRekeyTopic.readKeyValuesToList();

        assertThat(result.getFirst().key).isEqualTo("Nieuwpoort");
    }

    @Test
    void givenNotKiteableWaves_valueShouldBeTheSame() {
        //given
        var key = UUID.randomUUID().toString();
        var kiteableWind = KiteableWindDirectionDetectedMother.newEvent()
                .withSensorId("NP-%s-WRS".formatted(key))
                .buildEvent();

        // when
        windDirectionTopic.pipeInput(kiteableWind.getSensorId(), kiteableWind);

        // then
        var result = windDirectionRekeyTopic.readKeyValuesToList();

        assertThat(result.getLast().value).isEqualTo(new KiteableWindDirectionDetected(
                "NP-%s-WRS".formatted(key),
                "Nieuwpoort - Wind measurement",
                "270",
                "deg",
                "Average wind direction"
        ));
    }
}
