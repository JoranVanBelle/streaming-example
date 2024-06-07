package org.streaming.example.adapter.http.feedback;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.streaming.example.application.feedback.AddUseCase;
import org.streaming.example.application.feedback.FindByIdUseCase;
import org.streaming.example.domain.http.UseCaseResult;
import org.streaming.example.domain.http.feedback.FeedbackRequest;

import java.net.URI;

@RestController
public class FeedbackController {

    private final AddUseCase addUseCase;
    private final FindByIdUseCase findByIdUseCase;

    public FeedbackController(AddUseCase addUseCase, FindByIdUseCase findByIdUseCase) {
        this.addUseCase = addUseCase;
        this.findByIdUseCase = findByIdUseCase;
    }

    @PostMapping(value = "/feedback")
    public ResponseEntity<?> addFeedback(@RequestBody FeedbackRequest feedbackRequest) {
        var addFeedbackResponse = addUseCase.execute(feedbackRequest);
        return ResponseEntity.created(URI.create("/feedback/" + addFeedbackResponse.data())).build();
    }

    @GetMapping("/feedback/{id}")
    public ResponseEntity<?> getFeedbackById(@PathVariable String id) {
        return responseEntityOf(findByIdUseCase.findById(id));
    }

    private ResponseEntity<?> responseEntityOf(UseCaseResult<?> result) {
        return switch (result.status()) {
            case success -> ResponseEntity.ok(result);
            case notFound -> ResponseEntity.notFound().build();
            case fail -> ResponseEntity.unprocessableEntity().body(result);
            case error -> ResponseEntity.internalServerError().body(result);
        };
    }
}
