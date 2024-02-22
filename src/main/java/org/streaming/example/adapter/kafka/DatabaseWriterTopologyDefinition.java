package org.streaming.example.adapter.kafka;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;
import org.streaming.example.domain.AvroSerdesFactory;
import org.streaming.example.domain.WeatherRepository;
import org.streaming.example.domain.kafka.ProcessorDefinition;
import org.streaming.example.domain.kafka.SinkDefinition;
import org.streaming.example.domain.kafka.SourceDefinition;
import org.streaming.example.domain.kafka.StateStoreDefinition;
import org.streaming.example.domain.kafka.TopologyDefinition;
import org.streaming.example.infrastructure.processor.WeatherDatabaseWriterProcessor;

import java.util.List;

@Component
public class DatabaseWriterTopologyDefinition implements TopologyDefinition {

    private final KafkaTopicsProperties kafkaTopicsProperties;
    private final AvroSerdesFactory avroSerdesFactory;
    private final WeatherRepository weatherRepository;

    public DatabaseWriterTopologyDefinition(KafkaTopicsProperties kafkaTopicsProperties, AvroSerdesFactory avroSerdesFactory, org.streaming.example.domain.WeatherRepository weatherRepository) {
        this.kafkaTopicsProperties = kafkaTopicsProperties;
        this.avroSerdesFactory = avroSerdesFactory;
        this.weatherRepository = weatherRepository;
    }

    @Override
    public List<SourceDefinition> sources() {
        return List.of(
                SourceDefinition.newSourceDefinition()
                        .withTopic(kafkaTopicsProperties.getKiteWeatherDetected())
                        .withValueDeserializer(avroSerdesFactory.specificAvroValueDeserializer())
                        .build()
        );
    }

    @Override
    public List<ProcessorDefinition<?, ?, ?, ?>> processors() {
        return List.of(ProcessorDefinition.<String, SpecificRecord, Void, Void>newProcessorDefinition()
                        .withName(WeatherDatabaseWriterProcessor.NAME)
                        .withParents(kafkaTopicsProperties.getKiteWeatherDetected())
                        .withProcessorSupplier(() -> new WeatherDatabaseWriterProcessor(weatherRepository))
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
