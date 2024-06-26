package org.streaming.example.infrastructure.processor;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.streaming.example.adapter.events.KiteableWaveDetected;
import org.streaming.example.adapter.events.UnkiteableWaveDetected;
import org.streaming.example.adapter.events.WaveDetected;
import org.streaming.example.adapter.kafka.KafkaTopicsProperties;
import org.streaming.example.adapter.kafka.WeatherPublisher;
import org.streaming.example.domain.AvroSerdesFactory;
import org.streaming.example.domain.TopologyTest;
import org.streaming.example.infrastructure.KafkaContainerSupport;
import org.streaming.example.mothers.RawWaveHeightMeasured;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TopologyTest(
    includeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = {
            ".*\\infrastructure\\.*"
        }
    )
)
class WaveProcessorTest extends KafkaContainerSupport {

    @MockBean
    WeatherPublisher weatherPublisher;

    @Autowired
    TopologyTestDriver topologyTestDriver;

    @Autowired
    KafkaTopicsProperties kafkaTopicsProperties;

    @Autowired
    private AvroSerdesFactory avroSerdesFactory;

    TestInputTopic<String, Object> rawDataMeasuredTopic;
    TestOutputTopic<String, SpecificRecord> waveTopic;

    @BeforeEach
    void setUp() {
        rawDataMeasuredTopic = topologyTestDriver.createInputTopic(
            kafkaTopicsProperties.getRawDataMeasured(),
            new StringSerializer(),
            avroSerdesFactory.avroSerializer());

        waveTopic = topologyTestDriver.createOutputTopic(
            kafkaTopicsProperties.getWaveDetected(),
            new StringDeserializer(),
            avroSerdesFactory.specificAvroValueDeserializer());
    }

    @Test
    void givenNotKiteableWaves_whenNotKiteableWaveDetected_oneEventShouldBeFound() {
        //given
        var key = UUID.randomUUID().toString();
        var notKiteableWaves = RawWaveHeightMeasured.newEvent()
            .withNotKiteableWave()
            .withSensorId("NP-%s-GH1".formatted(key))
            .build();
        var notKiteableWaves2 = RawWaveHeightMeasured.newEvent()
            .withNotKiteableWave()
            .withSensorId("NP-%s-GH1".formatted(key))
            .withValue("-1")
            .build();

        // when
        rawDataMeasuredTopic.pipeInput(notKiteableWaves.getSensorId(), notKiteableWaves);
        rawDataMeasuredTopic.pipeInput(notKiteableWaves2.getSensorId(), notKiteableWaves2);

        // then
        var result = waveTopic.readValuesToList();

        assertThat(result).contains(new WaveDetected(
            new UnkiteableWaveDetected(
                notKiteableWaves.getSensorId(),
                notKiteableWaves.getLocation(),
                notKiteableWaves.getValue(),
                notKiteableWaves.getUnit(),
                notKiteableWaves.getDescription()
            ))
        );

        assertThat(result).doesNotContain(
            new WaveDetected(
                new UnkiteableWaveDetected(
                    notKiteableWaves2.getSensorId(),
                    notKiteableWaves2.getLocation(),
                    notKiteableWaves2.getValue(),
                    notKiteableWaves2.getUnit(),
                    notKiteableWaves2.getDescription()
                ))
        );
    }

    @Test
    void givenKiteableWaves_whenKiteableWaveDetected_oneEventShouldBeFound() {
        //given
        var key = UUID.randomUUID().toString();
        var kiteableWaves = RawWaveHeightMeasured.newEvent()
            .withKiteableWave()
            .withSensorId("NP-%s-GH1".formatted(key))
            .build();
        var kiteableWaves2 = RawWaveHeightMeasured.newEvent()
            .withValue("1001")
            .withSensorId("NP-%s-GH1".formatted(key))
            .withValue("-1")
            .build();

        // when
        rawDataMeasuredTopic.pipeInput(kiteableWaves.getSensorId(), kiteableWaves);
        rawDataMeasuredTopic.pipeInput(kiteableWaves2.getSensorId(), kiteableWaves2);

        // then
        var result = waveTopic.readValuesToList();

        assertThat(result).contains(
            new WaveDetected(
                new KiteableWaveDetected(
                    kiteableWaves.getSensorId(),
                    kiteableWaves.getLocation(),
                    kiteableWaves.getValue(),
                    kiteableWaves.getUnit(),
                    kiteableWaves.getDescription()
                )
            ));

        assertThat(result).doesNotContain(
            new WaveDetected(
                new KiteableWaveDetected(
                    kiteableWaves2.getSensorId(),
                    kiteableWaves2.getLocation(),
                    kiteableWaves2.getValue(),
                    kiteableWaves2.getUnit(),
                    kiteableWaves2.getDescription()
                )
            ));
    }

    @Test
    void givenNotKiteableWaves_whenKiteableWaveDetected_twoEventsShouldBeFound() {
        //given
        var key = UUID.randomUUID().toString();
        var notKiteableWaves = RawWaveHeightMeasured.newEvent()
            .withNotKiteableWave()
            .withSensorId("NP-%s-GH1".formatted(key))
            .build();
        var kiteableWaves = RawWaveHeightMeasured.newEvent()
            .withKiteableWave()
            .withSensorId("NP-%s-GH1".formatted(key))
            .build();

        // when
        rawDataMeasuredTopic.pipeInput(notKiteableWaves.getSensorId(), notKiteableWaves);
        rawDataMeasuredTopic.pipeInput(kiteableWaves.getSensorId(), kiteableWaves);

        // then
        var result = waveTopic.readValuesToList();

        assertThat(result).contains(
            new WaveDetected(
                new UnkiteableWaveDetected(
                    notKiteableWaves.getSensorId(),
                    notKiteableWaves.getLocation(),
                    notKiteableWaves.getValue(),
                    notKiteableWaves.getUnit(),
                    notKiteableWaves.getDescription()
                )
            ));

        assertThat(result).contains(
            new WaveDetected(
                new KiteableWaveDetected(
                    kiteableWaves.getSensorId(),
                    kiteableWaves.getLocation(),
                    kiteableWaves.getValue(),
                    kiteableWaves.getUnit(),
                    kiteableWaves.getDescription()
                )
            ));
    }

    @Test
    void givenKiteableWaves_whenNotKiteableWaveDetected_twoEventsShouldBeFound() {
        //given
        var key = UUID.randomUUID().toString();
        var kiteableWaves = RawWaveHeightMeasured.newEvent()
            .withKiteableWave()
            .withSensorId("NP-%s-GH1".formatted(key))
            .build();
        var notKiteableWaves = RawWaveHeightMeasured.newEvent()
            .withNotKiteableWave()
            .withSensorId("NP-%s-GH1".formatted(key))
            .build();

        // when
        rawDataMeasuredTopic.pipeInput(kiteableWaves.getSensorId(), kiteableWaves);
        rawDataMeasuredTopic.pipeInput(notKiteableWaves.getSensorId(), notKiteableWaves);

        // then
        var result = waveTopic.readValuesToList();

        assertThat(result).contains(
            new WaveDetected(
                new KiteableWaveDetected(
                    kiteableWaves.getSensorId(),
                    kiteableWaves.getLocation(),
                    kiteableWaves.getValue(),
                    kiteableWaves.getUnit(),
                    kiteableWaves.getDescription()
                )
            ));

        assertThat(result).contains(
            new WaveDetected(
                new UnkiteableWaveDetected(
                    notKiteableWaves.getSensorId(),
                    notKiteableWaves.getLocation(),
                    notKiteableWaves.getValue(),
                    notKiteableWaves.getUnit(),
                    notKiteableWaves.getDescription()
                )
            ));
    }
}