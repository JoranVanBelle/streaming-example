package org.streaming.example.adapter.kafka;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import org.streaming.example.adapter.events.FeedbackGiven;
import org.streaming.example.adapter.events.WeatherDetected;
import org.streaming.example.domain.AvroSerdesFactory;
import org.streaming.example.domain.FeedbackRepository;
import org.streaming.example.domain.WeatherRepository;
import org.streaming.example.domain.kafka.ProcessorDefinition;
import org.streaming.example.domain.kafka.SinkDefinition;
import org.streaming.example.domain.kafka.SourceDefinition;
import org.streaming.example.domain.kafka.StateStoreDefinition;
import org.streaming.example.domain.kafka.TopologyDefinition;
import org.streaming.example.infrastructure.processor.FeedbackDatabaseWriterProcessor;
import org.streaming.example.infrastructure.processor.WeatherDatabaseWriterProcessor;

import java.util.List;

@Component
public class DatabaseWriterTopologyDefinition implements TopologyDefinition {

    private final KafkaTopicsProperties kafkaTopicsProperties;
    private final AvroSerdesFactory avroSerdesFactory;
    private final WeatherRepository weatherRepository;
    private final FeedbackRepository feedbackRepository;

    public DatabaseWriterTopologyDefinition(KafkaTopicsProperties kafkaTopicsProperties, AvroSerdesFactory avroSerdesFactory, WeatherRepository weatherRepository, FeedbackRepository feedbackRepository) {
        this.kafkaTopicsProperties = kafkaTopicsProperties;
        this.avroSerdesFactory = avroSerdesFactory;
        this.weatherRepository = weatherRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public List<SourceDefinition> sources() {
        return List.of(
                SourceDefinition.newSourceDefinition()
                        .withTopic(kafkaTopicsProperties.getKiteWeatherDetected())
                        .withValueDeserializer(avroSerdesFactory.specificAvroValueDeserializer())
                        .build(),
                SourceDefinition.newSourceDefinition()
                        .withTopic(kafkaTopicsProperties.getFeedbackGiven())
                        .withValueDeserializer(avroSerdesFactory.specificAvroValueDeserializer())
                        .build()
        );
    }

    @Override
    public List<ProcessorDefinition<?, ?, ?, ?>> processors() {
        return List.of(ProcessorDefinition.<String, WeatherDetected, Void, Void>newProcessorDefinition()
                        .withName(WeatherDatabaseWriterProcessor.NAME)
                        .withParents(kafkaTopicsProperties.getKiteWeatherDetected())
                        .withProcessorSupplier(() -> new WeatherDatabaseWriterProcessor(weatherRepository))
                .build(),
                ProcessorDefinition.<String, FeedbackGiven, Void, Void>newProcessorDefinition()
                        .withName(FeedbackDatabaseWriterProcessor.NAME)
                        .withParents(kafkaTopicsProperties.getFeedbackGiven())
                        .withProcessorSupplier(() -> new FeedbackDatabaseWriterProcessor(feedbackRepository))
                        .build()
        );
    }

    @Override
    public List<SinkDefinition> sinks() {
        return List.of();
    }

    @Override
    public List<StateStoreDefinition> stateStores() {
        return List.of();
    }
}
