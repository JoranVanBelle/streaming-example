package org.streaming.example.adapter.kafka;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import org.streaming.example.RawDataMeasured;
import org.streaming.example.domain.MutableClock;
import org.streaming.example.domain.WeatherEntity;
import org.streaming.example.domain.meetnetvlaamsebanken.MeetnetVlaamseBankenProperties;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureWebMvc
@ActiveProfiles("it-test")
@WireMockTest(httpPort = 9876)
@EnableKafkaStreams
@Disabled
class WeatherTopologyDefinitionTest {

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.streams.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
    }

    @Autowired
    KafkaTopicsProperties kafkaTopicsProperties;

    @Autowired
    MeetnetVlaamseBankenProperties meetnetVlaamseBankenProperties;

    @Autowired
    KafkaTemplate<String, RawDataMeasured> template;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MutableClock mutableClock;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TestListener testListener;

    @Autowired
    WeatherPublisher weatherPublisher;

    @BeforeEach
    void setUp() {
        String body = String.format("grant_type=password&username=%s&password=%s", meetnetVlaamseBankenProperties.getUsername(), meetnetVlaamseBankenProperties.getPassword());

        stubFor(post("/Token?grant_type=password")
                .withRequestBody(equalTo(body))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getJsonContentFromFile("/meetnetvlaamsebanken/login-response.json"))));

        stubFor(get("/V2/currentData")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getJsonContentFromFile("/meetnetvlaamsebanken/currentData.json"))));

        stubFor(get("/V2/catalog")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getJsonContentFromFile("/meetnetvlaamsebanken/catalog.json"))));
    }

    @Test
    void givenOneKiteableAndOneNotKiteableWeather_whenQuerriesAll_ReturnsDataFromPlaces() {

        mutableClock.fastForward(Duration.of(123, MILLIS));

        await().atMost(15, SECONDS)
                .untilAsserted(() -> {
                    mockMvc
                            .perform(MockMvcRequestBuilders.get("/weather"))
                            .andDo(print())
                            .andExpect(jsonPath("$.weather.size()", is(equalTo(1))))
                            .andExpect(jsonPath("$.weather[0]", equalTo(
                                    new WeatherEntity(
                                            "Nieuwpoort - Buoy",
                                            LocalDateTime.parse("2001-06-20T01:00:00.123"),
                                            21.4,
                                            "m/s",
                                            179.0,
                                            "cm",
                                            265.0,
                                            "deg",
                                            "KiteableWeatherDetected"
                                    )
                            )));
                });

    }

    //    @Test
//    void givenThreeDifferentSensorOutputs_thenThreeIndividualEventsShouldBeFound() {
//        // given
//        RawDataMeasured rawWaveHeight = RawWaveHeightMeasured.newEvent().build();
//        RawDataMeasured rawWindSpeed = RawWindSpeedMeasured.newEvent().build();
//        RawDataMeasured rawWindDirection = RawWindDirectionMeasured.newEvent().build();
//
//        // when
//        template.send(kafkaTopicsProperties.getRawDataMeasured(), rawWaveHeight.getSensorId(), rawWaveHeight);
//        template.send(kafkaTopicsProperties.getRawDataMeasured(), rawWindSpeed.getSensorId(), rawWindSpeed);
//        template.send(kafkaTopicsProperties.getRawDataMeasured(), rawWindDirection.getSensorId(), rawWindDirection);
//
//        // then
//        await().atMost(10, HOURS)
//                .untilAsserted(() -> {
//                    assertThat(testListener.peek()).contains(
//                            new KiteableWaveDetected(
//                                    "NPBGH1",
//                                    "Nieuwpoort - Buoy",
//                                    "109.0",
//                                    "cm",
//                                    "10% highest waves")
//                    );
//                });
//
//        testListener.readRecordsToList();
//    }
//
//    @Test
//    void givenNotKiteableWind_whenKiteableWindDetected_newEventShouldBeFound() {
//        // given
//        var notKiteableWind = RawWindSpeedMeasured.newEvent().withNotKiteableWind().build();
//        var kiteableWind = RawWindSpeedMeasured.newEvent().withKiteableWind().build();
//
//        // when
//        template.send(kafkaTopicsProperties.getRawDataMeasured(), notKiteableWind.getSensorId(), notKiteableWind);
//
//        await().atMost(10, SECONDS)
//                .untilAsserted(() -> assertThat(testListener.peek()).size().isEqualTo(1));
//
//        template.send(kafkaTopicsProperties.getRawDataMeasured(), kiteableWind.getSensorId(), kiteableWind);
//
//        // then
//        await().atMost(10, SECONDS)
//                .untilAsserted(() -> assertThat(testListener.peek()).size().isEqualTo(2));
//        assertThat(testListener.readRecordsToList()).size().isEqualTo(2);
//    }
//
//    @Test
//    void givenNotKiteableWaves_whenNotKiteableWaveDetected_oneEventShouldBeFound() {
//        // given
//        var notKiteableWaves = RawWaveHeightMeasured.newEvent().withNotKiteableWave().build();
//        var notKiteableWaves2 = RawWaveHeightMeasured.newEvent().withNotKiteableWave().build();
//
//        // when
//        template.send(kafkaTopicsProperties.getRawDataMeasured(), notKiteableWaves.getSensorId(), notKiteableWaves);
//
//        await().atMost(10, SECONDS)
//                .untilAsserted(() -> assertThat(testListener.peek()).size().isEqualTo(1));
//
//        template.send(kafkaTopicsProperties.getRawDataMeasured(), notKiteableWaves2.getSensorId(), notKiteableWaves2);
//
//        // then
//        await().atMost(10, SECONDS)
//                .untilAsserted(() -> assertThat(testListener.peek()).size().isEqualTo(1));
//
//        assertThat(testListener.readRecordsToList()).size().isEqualTo(1);
//    }
//
//    @Test
//    void givenKiteableWindDirection_whenNotKiteableWindDirectionDetected_twoEventsShouldBeFound() {
//        // given
//        var kiteableWindDirection = RawWindDirectionMeasured.newEvent().withKiteableWindDirection().build();
//        var notKiteableWindDirection = RawWindDirectionMeasured.newEvent().withNotKiteableWindDirection().build();
//
//        // when
//        template.send(kafkaTopicsProperties.getRawDataMeasured(), kiteableWindDirection.getSensorId(), kiteableWindDirection);
//
//        await().atMost(10, SECONDS)
//                .untilAsserted(() -> assertThat(testListener.peek()).size().isEqualTo(1));
//
//        template.send(kafkaTopicsProperties.getRawDataMeasured(), notKiteableWindDirection.getSensorId(), notKiteableWindDirection);
//
//        // then
//        await().atMost(10, SECONDS)
//                .untilAsserted(() -> assertThat(testListener.peek()).size().isEqualTo(2));
//
//        assertThat(testListener.readRecordsToList().size()).isEqualTo(2);
//    }

    private String getJsonContentFromFile(String path) {
        try {
            return new String(Objects.requireNonNull(getClass().getResourceAsStream(path)).readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}