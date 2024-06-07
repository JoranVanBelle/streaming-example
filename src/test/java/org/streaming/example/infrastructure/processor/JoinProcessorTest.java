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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.streaming.example.adapter.events.KiteableWaveDetected;
import org.streaming.example.adapter.events.KiteableWeatherDetected;
import org.streaming.example.adapter.events.KiteableWindDirectionDetected;
import org.streaming.example.adapter.events.KiteableWindSpeedDetected;
import org.streaming.example.adapter.events.UnkiteableWaveDetected;
import org.streaming.example.adapter.events.UnkiteableWindDirectionDetected;
import org.streaming.example.adapter.events.UnkiteableWindSpeedDetected;
import org.streaming.example.adapter.events.WaveDetected;
import org.streaming.example.adapter.events.WeatherDetected;
import org.streaming.example.adapter.events.WindDirectionDetected;
import org.streaming.example.adapter.events.WindSpeedDetected;
import org.streaming.example.adapter.kafka.KafkaTopicsProperties;
import org.streaming.example.adapter.kafka.WeatherPublisher;
import org.streaming.example.domain.AvroSerdesFactory;
import org.streaming.example.domain.TopologyTest;
import org.streaming.example.infrastructure.KafkaContainerSupport;
import org.streaming.example.mothers.KiteableWaveDetectedMother;
import org.streaming.example.mothers.KiteableWindDirectionDetectedMother;
import org.streaming.example.mothers.KiteableWindSpeedDetectedMother;
import org.streaming.example.mothers.UnkiteableWaveDetectedMother;
import org.streaming.example.mothers.UnkiteableWindDirectionDetectedMother;
import org.streaming.example.mothers.UnkiteableWindSpeedDetectedMother;

import static org.assertj.core.api.Assertions.assertThat;

@TopologyTest(
    includeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = {
            ".*\\infrastructure\\.*"
        }
    )
)
public class JoinProcessorTest extends KafkaContainerSupport {

    @MockBean
    WeatherPublisher weatherPublisher;

    @Autowired
    TopologyTestDriver topologyTestDriver;

    @Autowired
    KafkaTopicsProperties kafkaTopicsProperties;

    @Autowired
    private AvroSerdesFactory avroSerdesFactory;

    TestInputTopic<String, WindSpeedDetected> windRekeyTopic;
    TestInputTopic<String, WaveDetected> waveRekeyTopic;
    TestInputTopic<String, WindDirectionDetected> windDirectionRekeyTopic;
    TestOutputTopic<String, SpecificRecord> weatherTopic;

    @BeforeEach
    void setUp() {
        windRekeyTopic = topologyTestDriver.createInputTopic(
            kafkaTopicsProperties.getRekeyedWindDetected(),
            new StringSerializer(),
            avroSerdesFactory.specificAvroValueSerializer());

        waveRekeyTopic = topologyTestDriver.createInputTopic(
            kafkaTopicsProperties.getRekeyedWaveDetected(),
            new StringSerializer(),
            avroSerdesFactory.specificAvroValueSerializer());

        windDirectionRekeyTopic = topologyTestDriver.createInputTopic(
            kafkaTopicsProperties.getRekeyedWindDirectionDetected(),
            new StringSerializer(),
            avroSerdesFactory.specificAvroValueSerializer());

        weatherTopic = topologyTestDriver.createOutputTopic(
            kafkaTopicsProperties.getKiteWeatherDetected(),
            new StringDeserializer(),
            avroSerdesFactory.specificAvroValueDeserializer());
    }

    @Nested
    class GivenASensorCombinationInputs {

        @Test
        void thenTheEventsCanBeJoined() {
            // given
            var kiteableWaveDetected = KiteableWaveDetectedMother.newEvent()
                .withSensorId("Nieuwpoort")
                .buildEvent();

            var kiteableWindSpeedDetected = KiteableWindSpeedDetectedMother.newEvent()
                .withSensorId("Nieuwpoort")
                .buildEvent();

            var kiteableWindDirectionDetected = KiteableWindDirectionDetectedMother.newEvent()
                .withSensorId("Nieuwpoort")
                .buildEvent();

            // when
            waveRekeyTopic.pipeInput(kiteableWaveDetected.getSensorId(), new WaveDetected(kiteableWaveDetected));
            windRekeyTopic.pipeInput(kiteableWindSpeedDetected.getSensorId(), new WindSpeedDetected(kiteableWindSpeedDetected));
            windDirectionRekeyTopic.pipeInput(kiteableWindDirectionDetected.getSensorId(), new WindDirectionDetected(kiteableWindDirectionDetected));

            // then
            var result = weatherTopic.readValuesToList();
            assertThat(result).contains(
                new WeatherDetected(
                    new KiteableWeatherDetected(
                        "Nieuwpoort - Buoy",
                        "Nieuwpoort - Buoy",
                        "1000",
                        "m/s",
                        "39.0",
                        "cm",
                        "270",
                        "deg"
                    )
                ));
        }
    }

    @Test
    void givenKiteableWeather_whenWavesBecomeUnkiteable_weatherShouldBeUpdated() {
        // given
        var kiteableWaveDetected = KiteableWaveDetectedMother.newEvent()
            .withSensorId("LocationX")
            .withLocation("LocationX")
            .buildEvent();

        var kiteableWindSpeedDetected = KiteableWindSpeedDetectedMother.newEvent()
            .withSensorId("LocationX")
            .withLocation("LocationX")
            .buildEvent();

        var kiteableWindDirectionDetected = KiteableWindDirectionDetectedMother.newEvent()
            .withSensorId("LocationX")
            .withLocation("LocationX")
            .buildEvent();

        var unkiteableWaveDetected = UnkiteableWaveDetectedMother.newEvent()
            .withSensorId("LocationX")
            .withLocation("LocationX")
            .buildEvent();

        // when
        waveRekeyTopic.pipeInput(kiteableWaveDetected.getSensorId(), new WaveDetected(kiteableWaveDetected));
        windRekeyTopic.pipeInput(kiteableWindSpeedDetected.getSensorId(), new WindSpeedDetected(kiteableWindSpeedDetected));
        windDirectionRekeyTopic.pipeInput(kiteableWindDirectionDetected.getSensorId(), new WindDirectionDetected(kiteableWindDirectionDetected));

        // then
        var result = weatherTopic.readValuesToList();

        assertThat(result).contains(
            new WeatherDetected(
                new KiteableWeatherDetected(
                    "LocationX",
                    "LocationX",
                    "1000",
                    "m/s",
                    "39.0",
                    "cm",
                    "270",
                    "deg"
                )
            ));

        waveRekeyTopic.pipeInput(unkiteableWaveDetected.getSensorId(), new WaveDetected(unkiteableWaveDetected));

        assertThat(result).contains(
            new WeatherDetected(
                new KiteableWeatherDetected(
                    "LocationX",
                    "LocationX",
                    "1000",
                    "m/s",
                    "39.0",
                    "cm",
                    "270",
                    "deg"
                )
            ));
    }
}
