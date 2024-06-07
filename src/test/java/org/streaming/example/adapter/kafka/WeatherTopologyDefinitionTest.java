package org.streaming.example.adapter.kafka;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.streaming.example.domain.IntegrationTest;
import org.streaming.example.domain.MutableClock;
import org.streaming.example.domain.WeatherRepository;
import org.streaming.example.domain.meetnetvlaamsebanken.MeetnetVlaamseBankenProperties;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@IntegrationTest
class WeatherTopologyDefinitionTest {

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));
    @Qualifier("clock")
    @Autowired
    private Clock clock;

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.streams.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
    }

    @Autowired
    MeetnetVlaamseBankenProperties meetnetVlaamseBankenProperties;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    MutableClock mutableClock;

    @BeforeEach
    void setUp() {
        mutableClock.set(Instant.parse("2001-06-19T00:00:00.00Z"));

        weatherRepository.deleteAll();

        String body = String.format("grant_type=password&username=%s&password=%s", meetnetVlaamseBankenProperties.getUsername(), meetnetVlaamseBankenProperties.getPassword());

        stubFor(post("/Token?grant_type=password")
                .withRequestBody(equalTo(body))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getJsonContentFromFile("/json/meetnetvlaamsebanken/login-response.json"))));

        stubFor(get("/V2/currentData")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getJsonContentFromFile("/json/meetnetvlaamsebanken/currentData.json"))));

        stubFor(get("/V2/catalog")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getJsonContentFromFile("/json/meetnetvlaamsebanken/catalog.json"))));
    }

    @AfterEach
    void tearDown() {
        weatherRepository.deleteAll();
    }

    @Test
    void givenOneKiteableAndOneNotKiteableWeather_whenQuerriesAll_ReturnsDataFromPlaces() {
        mutableClock.fastForward(Duration.of(123, MILLIS));

        await().atMost(15, SECONDS)
                .untilAsserted(() ->
                    mockMvc
                            .perform(MockMvcRequestBuilders.get("/weather"))
                            .andDo(print())
                            .andExpect(jsonPath("$.size", is(equalTo(1))))
                                                    .andExpect(jsonPath("$.weather[0].location", is("Nieuwpoort - Buoy")))
                                                    .andExpect(jsonPath("$.weather[0].windSpeed", equalTo(21.4)))
                                                    .andExpect(jsonPath("$.weather[0].windSpeedUnit", is("m/s")))
                                                    .andExpect(jsonPath("$.weather[0].waveHeight", equalTo(179.0)))
                                                    .andExpect(jsonPath("$.weather[0].waveHeightUnit", is("cm")))
                                                    .andExpect(jsonPath("$.weather[0].windDirection", equalTo(265.0)))
                                                    .andExpect(jsonPath("$.weather[0].windDirectionUnit", is("deg")))
                                                    .andExpect(jsonPath("$.weather[0].status", is("KiteableWeatherDetected")))
                );

    }

    private String getJsonContentFromFile(String path) {
        try {
            return new String(Objects.requireNonNull(getClass().getResourceAsStream(path)).readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}