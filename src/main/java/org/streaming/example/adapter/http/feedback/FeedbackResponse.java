package org.streaming.example.adapter.http.feedback;

import org.streaming.example.domain.http.ResponseEntityStatus;

public record FeedbackResponse(
        ResponseEntityStatus status,
        Feedback feedback
) {

}
