package org.streaming.example.application.feedback;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.streaming.example.FeedbackGiven;
import org.streaming.example.adapter.FeedbackUsernameProperties;
import org.streaming.example.adapter.kafka.KafkaTopicsProperties;
import org.streaming.example.domain.http.UseCaseResult;
import org.streaming.example.domain.http.feedback.FeedbackRequest;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import static org.streaming.example.domain.http.ResponseEntityStatus.error;
import static org.streaming.example.domain.http.ResponseEntityStatus.success;

@Component
public class AddUseCase {

    private final KafkaTemplate<String, FeedbackGiven> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;
    private final FeedbackUsernameProperties feedbackUsernameProperties;
    private final Random random;

    public AddUseCase(KafkaTemplate<String, FeedbackGiven> kafkaTemplate, KafkaTopicsProperties kafkaTopicsProperties, FeedbackUsernameProperties feedbackUsernameProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicsProperties = kafkaTopicsProperties;
        this.feedbackUsernameProperties = feedbackUsernameProperties;
        random = new Random();
    }

    public UseCaseResult<String> execute(FeedbackRequest feedbackRequest) {
        var feedbackGiven  = mapToFeedbackGiven(feedbackRequest);
        try {
        var producerRecord = new ProducerRecord<>(kafkaTopicsProperties.getFeedbackGiven(), feedbackGiven.getFeedbackId(), feedbackGiven);
            kafkaTemplate.send(producerRecord).get();
        } catch (InterruptedException | ExecutionException e) {
            return new UseCaseResult<>(error, "something went wrong while sending the data to kafka");
        }
        return new UseCaseResult<>(success, feedbackGiven.getFeedbackId());
    }

    private FeedbackGiven mapToFeedbackGiven(FeedbackRequest feedbackRequest) {
        var usernames = feedbackUsernameProperties.getAnonymousUsernames();
        var username = feedbackRequest.username() != null ? feedbackRequest.username() : usernames.get(random.nextInt(usernames.size()));
        return FeedbackGiven.newBuilder()
                .setFeedbackId(getKey(username, feedbackRequest))
                .setLocation(feedbackRequest.location())
                .setComment(feedbackRequest.comment())
                .setUsername(username)
                .setTimestamp(feedbackRequest.timestamp())
                .build();
    }
    private String getKey(String username, FeedbackRequest feedbackRequest) {
        return "%s-%s-%s".formatted(username, feedbackRequest.location(), feedbackRequest.timestamp());
    }
}
