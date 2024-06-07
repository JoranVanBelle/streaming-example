package org.streaming.example.adapter.http.feedback;

import org.streaming.example.domain.FeedbackEntity;

import java.time.LocalDateTime;

public record Feedback(
    String feedbackId,
    String location,
    String username,
    String comment,
    LocalDateTime timestamp
) {
    public static Feedback of(FeedbackEntity entity) {
        return new Feedback(
            entity.getFeedbackId(),
            entity.getLocation(),
            entity.getUsername(),
            entity.getComment(),
            entity.getTimestamp()
        );
    }
}
