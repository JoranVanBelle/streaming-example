package org.streaming.example.application.feedback;

import org.springframework.stereotype.Component;
import org.streaming.example.adapter.http.feedback.Feedback;
import org.streaming.example.domain.FeedbackRepository;
import org.streaming.example.domain.http.UseCaseResult;

import static org.streaming.example.domain.http.ResponseEntityStatus.notFound;
import static org.streaming.example.domain.http.ResponseEntityStatus.success;

@Component
public class FindByIdUseCase {

    private final FeedbackRepository feedbackRepository;

    public FindByIdUseCase(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public UseCaseResult<?> findById(String id) {
        var entity = feedbackRepository.findById(id);

        if (entity.isPresent()) {
            return new UseCaseResult<>(success, Feedback.of(entity.get()));
        }
        return new UseCaseResult<>(notFound, "No feedback found with this id");
    }
}
