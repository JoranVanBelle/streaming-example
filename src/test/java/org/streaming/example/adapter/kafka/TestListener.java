package org.streaming.example.adapter.kafka;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestListener {
    public List<SpecificRecord> events = new ArrayList<>();

    @KafkaListener(topics = {"${example.kafka.topics.wind-detected}", "${example.kafka.topics.wave-detected}", "${example.kafka.topics.wind-direction-detected}"})
    void handleCombinationInputEvent(SpecificRecord event) {
        events.add(event);
    }

    public List<SpecificRecord> peek() {
        return events;
    }

    public List<SpecificRecord> readRecordsToList() {
        final List<SpecificRecord> eventsCopy = events;
        events = new ArrayList<>();
        return eventsCopy;
    }
}
