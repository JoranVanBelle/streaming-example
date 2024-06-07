package org.streaming.example.adapter.kafka;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.streaming.example.domain.IntegrationTest;
import org.streaming.example.infrastructure.KafkaContainerSupport;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class FeedbackTopologyDefinitionTest {

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.streams.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    void givenSomeoneEntersFeedback_whenAUsernameIsEntered_thenTheFeedbackCanBeConsulted() throws Exception {
        // given
        String requestBody = """
                {
                  "username": "User1",
                  "location": "Koksijde",
                  "comment": "It's a bit chilly here",
                  "timestamp": "2001-06-19T00:00:01.000"
                }
                """;

        // when
        String location = mockMvc.perform(post("/feedback")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");
        assertThat(location).isNotNull();
        assertThat(location).contains("User1-Koksijde-2001-06-19T00:00:01");

        // then
        await().atMost(15, SECONDS)
                .untilAsserted(() ->
                        mockMvc.perform(get(location))
                                .andDo(print())
                                .andExpect(jsonPath("$.data.feedbackId", is(equalTo("User1-Koksijde-2001-06-19T00:00:01.000"))))
                                .andExpect(jsonPath("$.data.username", is(equalTo("User1"))))
                                .andExpect(jsonPath("$.data.location", is(equalTo("Koksijde"))))
                                .andExpect(jsonPath("$.data.comment", is(equalTo("It's a bit chilly here"))))
                                .andExpect(jsonPath("$.data.timestamp", is(equalTo("2001-06-19T00:00:01"))))
                );
    }

    @Test
    void givenSomeoneEntersFeedback_whenNoUsernameIsEntered_thenTheFeedbackCanBeConsultedAndARandomUsernameIsEntered() throws Exception {
        // given
        String requestBody = """
                {
                  "location": "Nieuwpoort",
                  "comment": "It's a bit chilly here",
                  "timestamp": "2001-06-19T00:00:01.000"
                }
                """;

        // when
        String location = mockMvc.perform(post("/feedback")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");
        assertThat(location).isNotNull();
        assertThat(location).contains("WaveWizard-Nieuwpoort-2001-06-19T00:00:01");

        await().atMost(15, SECONDS)
                .untilAsserted(() ->
                        mockMvc.perform(get(location))
                                .andDo(print())
                                .andExpect(jsonPath("$.data.feedbackId", is(equalTo("WaveWizard-Nieuwpoort-2001-06-19T00:00:01.000"))))
                                .andExpect(jsonPath("$.data.username", is(equalTo("WaveWizard"))))
                                .andExpect(jsonPath("$.data.location", is(equalTo("Nieuwpoort"))))
                                .andExpect(jsonPath("$.data.comment", is(equalTo("It's a bit chilly here"))))
                                .andExpect(jsonPath("$.data.timestamp", is(equalTo("2001-06-19T00:00:01"))))
                );
    }
}
