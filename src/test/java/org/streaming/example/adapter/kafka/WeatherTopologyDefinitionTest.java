package org.streaming.example.adapter.kafka;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.streaming.example.KiteableWaveDetected;
import org.streaming.example.RawDataMeasured;
import org.streaming.example.mothers.RawWaveHeightMeasured;
import org.streaming.example.mothers.RawWindDirectionMeasured;
import org.streaming.example.mothers.RawWindSpeedMeasured;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Disabled
class WeatherTopologyDefinitionTest {

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.streams.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
    }

    @MockBean
    WeatherPublisher weatherPublisher;

    @Autowired
    KafkaTopicsProperties kafkaTopicsProperties;

    @Autowired
    KafkaTemplate<String, RawDataMeasured> template;

    @Autowired
    TestListener testListener;

    @Test
    void givenThreeDifferentSensorOutputs_thenThreeIndividualEventsShouldBeFound() {
        // given
        RawDataMeasured rawWaveHeight = RawWaveHeightMeasured.newEvent().build();
        RawDataMeasured rawWindSpeed = RawWindSpeedMeasured.newEvent().build();
        RawDataMeasured rawWindDirection = RawWindDirectionMeasured.newEvent().build();

        // when
        template.send(kafkaTopicsProperties.getRawDataMeasured(), rawWaveHeight.getSensorId(), rawWaveHeight);
        template.send(kafkaTopicsProperties.getRawDataMeasured(), rawWindSpeed.getSensorId(), rawWindSpeed);
        template.send(kafkaTopicsProperties.getRawDataMeasured(), rawWindDirection.getSensorId(), rawWindDirection);

        // then
        await().atMost(10, HOURS)
                .untilAsserted(() -> {
                    assertThat(testListener.peek()).contains(
                            new KiteableWaveDetected(
                                    "NPBGH1",
                                    "Nieuwpoort - Buoy",
                                    "109.0",
                                    "cm",
                                    "10% highest waves")
                    );
                });

        testListener.readRecordsToList();
    }

    @Test
    void givenNotKiteableWind_whenKiteableWindDetected_newEventShouldBeFound() {
        // given
        var notKiteableWind = RawWindSpeedMeasured.newEvent().withNotKiteableWind().build();
        var kiteableWind = RawWindSpeedMeasured.newEvent().withKiteableWind().build();

        // when
        template.send(kafkaTopicsProperties.getRawDataMeasured(), notKiteableWind.getSensorId(), notKiteableWind);

        await().atMost(10, SECONDS)
                .untilAsserted(() -> assertThat(testListener.peek()).size().isEqualTo(1));

        template.send(kafkaTopicsProperties.getRawDataMeasured(), kiteableWind.getSensorId(), kiteableWind);

        // then
        await().atMost(10, SECONDS)
                .untilAsserted(() -> assertThat(testListener.peek()).size().isEqualTo(2));
        assertThat(testListener.readRecordsToList()).size().isEqualTo(2);
    }

    @Test
    void givenNotKiteableWaves_whenNotKiteableWaveDetected_oneEventShouldBeFound() {
        // given
        var notKiteableWaves = RawWaveHeightMeasured.newEvent().withNotKiteableWave().build();
        var notKiteableWaves2 = RawWaveHeightMeasured.newEvent().withNotKiteableWave().build();

        // when
        template.send(kafkaTopicsProperties.getRawDataMeasured(), notKiteableWaves.getSensorId(), notKiteableWaves);

        await().atMost(10, SECONDS)
                .untilAsserted(() -> assertThat(testListener.peek()).size().isEqualTo(1));

        template.send(kafkaTopicsProperties.getRawDataMeasured(), notKiteableWaves2.getSensorId(), notKiteableWaves2);

        // then
        await().atMost(10, SECONDS)
                .untilAsserted(() -> assertThat(testListener.peek()).size().isEqualTo(1));

        assertThat(testListener.readRecordsToList()).size().isEqualTo(1);
    }

    @Test
    void givenKiteableWindDirection_whenNotKiteableWindDirectionDetected_twoEventsShouldBeFound() {
        // given
        var kiteableWindDirection = RawWindDirectionMeasured.newEvent().withKiteableWindDirection().build();
        var notKiteableWindDirection = RawWindDirectionMeasured.newEvent().withNotKiteableWindDirection().build();

        // when
        template.send(kafkaTopicsProperties.getRawDataMeasured(), kiteableWindDirection.getSensorId(), kiteableWindDirection);

        await().atMost(10, SECONDS)
                .untilAsserted(() -> assertThat(testListener.peek()).size().isEqualTo(1));

        template.send(kafkaTopicsProperties.getRawDataMeasured(), notKiteableWindDirection.getSensorId(), notKiteableWindDirection);

        // then
        await().atMost(10, SECONDS)
                .untilAsserted(() -> assertThat(testListener.peek()).size().isEqualTo(2));

        assertThat(testListener.readRecordsToList().size()).isEqualTo(2);
    }
}