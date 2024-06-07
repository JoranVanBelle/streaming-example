package org.streaming.example.domain.http.feedback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FeedbackRequest(
        String username,
        String location,
        String comment,
        String timestamp
) {
}
