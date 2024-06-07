package org.streaming.example.adapter.kafka;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.streaming.example.adapter.events.RawDataMeasured;
import org.streaming.example.adapter.events.WaveDetected;
import org.streaming.example.adapter.events.WeatherDetected;
import org.streaming.example.adapter.events.WindDirectionDetected;
import org.streaming.example.adapter.events.WindSpeedDetected;
import org.streaming.example.domain.AvroSerdesFactory;
import org.streaming.example.domain.kafka.ProcessorDefinition;
import org.streaming.example.domain.kafka.SinkDefinition;
import org.streaming.example.domain.kafka.SourceDefinition;
import org.streaming.example.domain.kafka.StateStoreDefinition;
import org.streaming.example.domain.kafka.TopologyDefinition;
import org.streaming.example.domain.meetnetvlaamsebanken.LocationKeyMapping;
import org.streaming.example.infrastructure.processor.JoinProcessor;
import org.streaming.example.infrastructure.processor.WaveProcessor;
import org.streaming.example.infrastructure.processor.WaveRekeyProcessor;
import org.streaming.example.infrastructure.processor.WaveStateStorePopulatorProcessor;
import org.streaming.example.infrastructure.processor.WindDirectionProcessor;
import org.streaming.example.infrastructure.processor.WindDirectionRekeyProcessor;
import org.streaming.example.infrastructure.processor.WindDirectionStateStorePopulatorProcessor;
import org.streaming.example.infrastructure.processor.WindSpeedDetectedRekeyProcessor;
import org.streaming.example.infrastructure.processor.WindSpeedProcessor;
import org.streaming.example.infrastructure.processor.WindSpeedStateStorePopulatorProcessor;

import java.time.Clock;
import java.util.List;

import static org.streaming.example.infrastructure.processor.WaveProcessor.WAVE_PROCESSOR_STATE_STORE_NAME;
import static org.streaming.example.infrastructure.processor.WaveStateStorePopulatorProcessor.REKEYED_WAVE_STATE_STORE_NAME;
import static org.streaming.example.infrastructure.processor.WindDirectionProcessor.WIND_DIRECTION_PROCESSOR_STATE_STORE_NAME;
import static org.streaming.example.infrastructure.processor.WindDirectionStateStorePopulatorProcessor.REKEYED_WIND_DIRECTION_STATE_STORE_NAME;
import static org.streaming.example.infrastructure.processor.WindSpeedProcessor.WIND_PROCESSOR_STATE_STORE_NAME;
import static org.streaming.example.infrastructure.processor.WindSpeedStateStorePopulatorProcessor.REKEYED_WIND_SPEED_STATE_STORE_NAME;

@Component
public class WeatherTopologyDefinition implements TopologyDefinition {

    private final KafkaTopicsProperties kafkaTopicsProperties;
    private final AvroSerdesFactory avroSerdesFactory;
    private final Clock clock;
    private final double windSpeed;
    private final double waveHeight;
    private final double waveDirectionFrom;
    private final double waveDirectionUntil;
    private final LocationKeyMapping locationKeyMapping;

    public WeatherTopologyDefinition(
            KafkaTopicsProperties kafkaTopicsProperties,
            AvroSerdesFactory avroSerdesFactory,
            Clock clock,
            @Value("${example.weather.wind-speed}") double windSpeed,
            @Value("${example.weather.wave-height}") double waveHeight,
            @Value("${example.weather.wind-direction.from}") int waveDirectionFrom,
            @Value("${example.weather.wind-direction.until}") int waveDirectionUntil, LocationKeyMapping locationKeyMapping) {
        this.kafkaTopicsProperties = kafkaTopicsProperties;
        this.avroSerdesFactory = avroSerdesFactory;
        this.clock = clock;
        this.windSpeed = windSpeed;
        this.waveHeight = waveHeight;
        this.waveDirectionFrom = waveDirectionFrom;
        this.waveDirectionUntil = waveDirectionUntil;
        this.locationKeyMapping = locationKeyMapping;
    }

    @Override
    public List<SourceDefinition> sources() {
        return List.of(
                SourceDefinition.newSourceDefinition()
                        .withTopic(kafkaTopicsProperties.getRawDataMeasured())
                        .withValueDeserializer(avroSerdesFactory.specificAvroValueDeserializer())
                        .build(),
                SourceDefinition.newSourceDefinition()
                        .withTopic(kafkaTopicsProperties.getWindDetected())
                        .withValueDeserializer(avroSerdesFactory.specificAvroValueDeserializer())
                        .build(),
                SourceDefinition.newSourceDefinition()
                        .withTopic(kafkaTopicsProperties.getWaveDetected())
                        .withValueDeserializer(avroSerdesFactory.specificAvroValueDeserializer())
                        .build(),
                SourceDefinition.newSourceDefinition()
                        .withTopic(kafkaTopicsProperties.getWindDirectionDetected())
                        .withValueDeserializer(avroSerdesFactory.specificAvroValueDeserializer())
                        .build(),
                SourceDefinition.newSourceDefinition()
                        .withTopic(kafkaTopicsProperties.getRekeyedWindDetected())
                        .withValueDeserializer(avroSerdesFactory.specificAvroValueDeserializer())
                        .build(),
                SourceDefinition.newSourceDefinition()
                        .withTopic(kafkaTopicsProperties.getRekeyedWaveDetected())
                        .withValueDeserializer(avroSerdesFactory.specificAvroValueDeserializer())
                        .build(),
                SourceDefinition.newSourceDefinition()
                        .withTopic(kafkaTopicsProperties.getRekeyedWindDirectionDetected())
                        .withValueDeserializer(avroSerdesFactory.specificAvroValueDeserializer())
                        .build()
        );
    }

    @Override
    public List<ProcessorDefinition<?, ?, ?, ?>> processors() {
        return List.of(
                ProcessorDefinition.<String, RawDataMeasured, String, WindSpeedDetected>newProcessorDefinition()
                        .withName(WindSpeedProcessor.NAME)
                        .withParents(kafkaTopicsProperties.getRawDataMeasured())
                        .withProcessorSupplier(() -> new WindSpeedProcessor(clock, windSpeed))
                        .build(),
                ProcessorDefinition.<String, RawDataMeasured, String, WaveDetected>newProcessorDefinition()
                        .withName(WaveProcessor.NAME)
                        .withParents(kafkaTopicsProperties.getRawDataMeasured())
                        .withProcessorSupplier(() -> new WaveProcessor(clock, waveHeight))
                        .build(),
                ProcessorDefinition.<String, RawDataMeasured, String, WindDirectionDetected>newProcessorDefinition()
                        .withName(WindDirectionProcessor.NAME)
                        .withParents(kafkaTopicsProperties.getRawDataMeasured())
                        .withProcessorSupplier(() -> new WindDirectionProcessor(clock, waveDirectionFrom, waveDirectionUntil))
                        .build(),
                ProcessorDefinition.<String, WindSpeedDetected, String, WindSpeedDetected>newProcessorDefinition()
                        .withName(WindSpeedDetectedRekeyProcessor.NAME)
                        .withParents(kafkaTopicsProperties.getWindDetected())
                        .withProcessorSupplier(() -> new WindSpeedDetectedRekeyProcessor(clock, locationKeyMapping))
                        .build(),
                ProcessorDefinition.<String, WaveDetected, String, WaveDetected>newProcessorDefinition()
                        .withName(WaveRekeyProcessor.NAME)
                        .withParents(kafkaTopicsProperties.getWaveDetected())
                        .withProcessorSupplier(() -> new WaveRekeyProcessor(clock, locationKeyMapping))
                        .build(),
                ProcessorDefinition.<String, WindDirectionDetected, String, WindDirectionDetected>newProcessorDefinition()
                        .withName(WindDirectionRekeyProcessor.NAME)
                        .withParents(kafkaTopicsProperties.getWindDirectionDetected())
                        .withProcessorSupplier(() -> new WindDirectionRekeyProcessor(clock, locationKeyMapping))
                        .build(),
                ProcessorDefinition.<String, WindSpeedDetected, String, WindSpeedDetected>newProcessorDefinition()
                        .withName(WindSpeedStateStorePopulatorProcessor.NAME)
                        .withParents(kafkaTopicsProperties.getRekeyedWindDetected())
                        .withProcessorSupplier(() -> new WindSpeedStateStorePopulatorProcessor(clock))
                        .build(),
                ProcessorDefinition.<String, WaveDetected, String, WaveDetected>newProcessorDefinition()
                        .withName(WaveStateStorePopulatorProcessor.NAME)
                        .withParents(kafkaTopicsProperties.getRekeyedWaveDetected())
                        .withProcessorSupplier(() -> new WaveStateStorePopulatorProcessor(clock))
                        .build(),
                ProcessorDefinition.<String, WindDirectionDetected, String, WindDirectionDetected>newProcessorDefinition()
                        .withName(WindDirectionStateStorePopulatorProcessor.NAME)
                        .withParents(kafkaTopicsProperties.getRekeyedWindDirectionDetected())
                        .withProcessorSupplier(() -> new WindDirectionStateStorePopulatorProcessor(clock))
                        .build(),
                ProcessorDefinition.<String, SpecificRecord, String, WeatherDetected>newProcessorDefinition()
                        .withName(JoinProcessor.NAME)
                        .withParents(WindSpeedStateStorePopulatorProcessor.NAME, WaveStateStorePopulatorProcessor.NAME, WindDirectionStateStorePopulatorProcessor.NAME)
                        .withProcessorSupplier(() -> new JoinProcessor(clock))
                        .build()
        );
    }

    @Override
    public List<SinkDefinition> sinks() {
        return List.of(
                SinkDefinition.newSinkDefinition()
                        .withTopic(kafkaTopicsProperties.getWindDetected())
                        .withParents(WindSpeedProcessor.NAME)
                        .withValueSerializer(avroSerdesFactory.avroSerializer())
                        .build(),
                SinkDefinition.newSinkDefinition()
                        .withTopic(kafkaTopicsProperties.getWaveDetected())
                        .withParents(WaveProcessor.NAME)
                        .withValueSerializer(avroSerdesFactory.avroSerializer())
                        .build(),
                SinkDefinition.newSinkDefinition()
                        .withTopic(kafkaTopicsProperties.getWindDirectionDetected())
                        .withParents(WindDirectionProcessor.NAME)
                        .withValueSerializer(avroSerdesFactory.avroSerializer())
                        .build(),
                SinkDefinition.newSinkDefinition()
                        .withTopic(kafkaTopicsProperties.getRekeyedWindDetected())
                        .withParents(WindSpeedDetectedRekeyProcessor.NAME)
                        .withValueSerializer(avroSerdesFactory.avroSerializer())
                        .build(),
                SinkDefinition.newSinkDefinition()
                        .withTopic(kafkaTopicsProperties.getRekeyedWaveDetected())
                        .withParents(WaveRekeyProcessor.NAME)
                        .withValueSerializer(avroSerdesFactory.avroSerializer())
                        .build(),
                SinkDefinition.newSinkDefinition()
                        .withTopic(kafkaTopicsProperties.getRekeyedWindDirectionDetected())
                        .withParents(WindDirectionRekeyProcessor.NAME)
                        .withValueSerializer(avroSerdesFactory.avroSerializer())
                        .build(),
                SinkDefinition.newSinkDefinition()
                        .withTopic(kafkaTopicsProperties.getKiteWeatherDetected())
                        .withParents(JoinProcessor.NAME)
                        .withValueSerializer(avroSerdesFactory.avroSerializer())
                        .build()
        );
    }

    @Override
    public List<StateStoreDefinition> stateStores() {
        return List.of(
                StateStoreDefinition.newStateStoreDefinition()
                        .withName(WIND_PROCESSOR_STATE_STORE_NAME)
                        .withProcessors(WindSpeedProcessor.NAME)
                        .withValueSerdes(avroSerdesFactory.rawDataMeasuredSerde())
                        .build(),
                StateStoreDefinition.newStateStoreDefinition()
                        .withName(WAVE_PROCESSOR_STATE_STORE_NAME)
                        .withProcessors(WaveProcessor.NAME)
                        .withValueSerdes(avroSerdesFactory.rawDataMeasuredSerde())
                        .build(),
                StateStoreDefinition.newStateStoreDefinition()
                        .withName(WIND_DIRECTION_PROCESSOR_STATE_STORE_NAME)
                        .withProcessors(WindDirectionProcessor.NAME)
                        .withValueSerdes(avroSerdesFactory.rawDataMeasuredSerde())
                        .build(),
                StateStoreDefinition.newStateStoreDefinition()
                        .withName(REKEYED_WIND_SPEED_STATE_STORE_NAME)
                        .withProcessors(WindSpeedStateStorePopulatorProcessor.NAME, JoinProcessor.NAME)
                        .withValueSerdes(avroSerdesFactory.windSpeedDetectedSerde())
                        .build(),
                StateStoreDefinition.newStateStoreDefinition()
                        .withName(REKEYED_WAVE_STATE_STORE_NAME)
                        .withProcessors(WaveStateStorePopulatorProcessor.NAME, JoinProcessor.NAME)
                        .withValueSerdes(avroSerdesFactory.waveDetectedSerde())
                        .build(),
                StateStoreDefinition.newStateStoreDefinition()
                        .withName(REKEYED_WIND_DIRECTION_STATE_STORE_NAME)
                        .withProcessors(WindDirectionStateStorePopulatorProcessor.NAME, JoinProcessor.NAME)
                        .withValueSerdes(avroSerdesFactory.windDirectionDetectedSerde())
                        .build()
        );
    }
}
