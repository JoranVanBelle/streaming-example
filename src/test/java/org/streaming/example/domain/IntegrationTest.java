package org.streaming.example.domain;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used when performing an integration test
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureWebMvc
@ActiveProfiles("it-test")
@WireMockTest(httpPort = 9876)
public @interface IntegrationTest {
}
