package org.streaming.example.infrastructure;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class KafkaContainerSupport {

    @ServiceConnection
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.1"));

    @BeforeAll
    static void beforeAll() {
        if (!kafkaContainer.isRunning()) {
            kafkaContainer.start();
        }
    }

}
