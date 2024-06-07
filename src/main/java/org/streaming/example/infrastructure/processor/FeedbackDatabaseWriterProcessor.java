package org.streaming.example.infrastructure.processor;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.Record;
import org.streaming.example.FeedbackGiven;
import org.streaming.example.domain.FeedbackEntity;
import org.streaming.example.domain.FeedbackRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

public class FeedbackDatabaseWriterProcessor implements Processor<String, FeedbackGiven, Void, Void> {

    public static final String NAME = FeedbackDatabaseWriterProcessor.class.getSimpleName();
    private final FeedbackRepository feedbackRepository;

    public FeedbackDatabaseWriterProcessor(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public void process(Record<String, FeedbackGiven> record) {
        var feedback = record.value();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        LocalDateTime dateTime = LocalDateTime.parse(feedback.getTimestamp(), formatter);
        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        feedbackRepository.save(
            new FeedbackEntity(
                feedback.getFeedbackId(),
                feedback.getUsername(),
                feedback.getLocation(),
                feedback.getComment(),
                LocalDateTime.parse(formattedDateTime)
            )
        );
    }
}
