package org.streaming.example.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.cglib.core.Local;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class FeedbackEntity {

    @Id
    private String feedbackId;

    @NonNull
    private String username;

    @NonNull
    private String location;

    @NonNull
    private String comment;

    @NonNull
    private LocalDateTime timestamp;

    public FeedbackEntity() {}

    public FeedbackEntity(
        String feedbackId,
        String username,
        String location,
        String comment,
        LocalDateTime timestamp) {
        this.feedbackId = feedbackId;
        this.username = username;
        this.location = location;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getComment() {
        return comment;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    @NonNull
    public String getLocation() {
        return location;
    }

    @NonNull
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public static FeedbackEntityBuilder newFeedbackEntity() {
        return new FeedbackEntityBuilder();
    }

    public static class FeedbackEntityBuilder {

        private String feedbackId;
        private String username;
        private String location;
        private String comment;
        private LocalDateTime timestamp;

        public FeedbackEntityBuilder withFeedbackId(String feedbackId) {
            this.feedbackId = feedbackId;
            return this;
        }

        public FeedbackEntityBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public FeedbackEntityBuilder withLocation(String location) {
            this.location = location;
            return this;
        }

        public FeedbackEntityBuilder withComment(String comment) {
            this.comment = comment;
            return this;
        }

        public FeedbackEntityBuilder withTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public FeedbackEntity build() {
            return new FeedbackEntity(feedbackId, username, location, comment, timestamp);
        }

    }

}
