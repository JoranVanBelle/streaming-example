package org.streaming.example.adapter.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.streaming.example.adapter.MeetnetVlaamseBankenCatalog;
import org.streaming.example.adapter.MeetnetVlaamseBankenCatalog.AvailableData;
import org.streaming.example.adapter.MeetnetVlaamseBankenCatalog.Location;
import org.streaming.example.adapter.MeetnetVlaamseBankenCatalog.Name;
import org.streaming.example.adapter.MeetnetVlaamseBankenCatalog.Parameter;
import org.streaming.example.adapter.MeetnetVlaamseBankenData;
import org.streaming.example.adapter.events.RawDataMeasured;
import org.streaming.example.adapter.http.meetnetvlaamsebanken.MeetnetVlaamseBankenController;
import org.streaming.example.domain.meetnetvlaamsebanken.MeetnetVlaamseBankenProperties;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeatherPublisher {

    private final KafkaTemplate<String, RawDataMeasured> weatherPublisher;
    private final MeetnetVlaamseBankenController meetnetVlaamseBankenController;
    private final MeetnetVlaamseBankenProperties meetnetVlaamseBankenProperties;
    private final KafkaTopicsProperties kafkaTopicsProperties;
    private static final String CULTURE = "en-GB";

    public WeatherPublisher(
            KafkaTemplate<String, RawDataMeasured> weatherPublisher,
            MeetnetVlaamseBankenController meetnetVlaamseBankenController,
            MeetnetVlaamseBankenProperties meetnetVlaamseBankenProperties,
            KafkaTopicsProperties kafkaTopicsProperties) {
        this.weatherPublisher = weatherPublisher;
        this.meetnetVlaamseBankenController = meetnetVlaamseBankenController;
        this.meetnetVlaamseBankenProperties = meetnetVlaamseBankenProperties;
        this.kafkaTopicsProperties = kafkaTopicsProperties;
    }

    @Scheduled(fixedDelayString = "${example.http.meetnet-vlaamse-banken.poll-interval}")
    public void publish() {
        var catalog = meetnetVlaamseBankenController.findCatalogData();
        var data = meetnetVlaamseBankenController.findData();

        for (var sensorId : meetnetVlaamseBankenProperties.getSensorIds()) {
            var sensorData = getSensorData(data, sensorId);
            var availableSensorData = getAvailableSensorData(catalog, sensorId);
            var sensorLocation = getSensorLocation(catalog.locations(), availableSensorData);
            var sensorParameters = getSensorParameters(catalog.parameters(), availableSensorData);

            var rawData = RawDataMeasured.newBuilder()
                    .setSensorId(sensorId)
                    .setLocation(getName(sensorLocation.name()))
                    .setValue(String.valueOf(sensorData.value()))
                    .setUnit(sensorParameters.unit())
                    .setDescription(getName(sensorParameters.name()))
                    .build();

            var producerRecord = new ProducerRecord<>(kafkaTopicsProperties.getRawDataMeasured(), sensorId, rawData);
            weatherPublisher.send(producerRecord);
        }
    }

    private MeetnetVlaamseBankenData getSensorData(List<MeetnetVlaamseBankenData> data, String sensorId) {
        return data.stream()
                .filter(d -> d.id().equals(sensorId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Data from sensor %s could not get retrieved".formatted(sensorId)));
    }

    private AvailableData getAvailableSensorData(MeetnetVlaamseBankenCatalog catalog, String sensorId) {
        return catalog.availableData().stream()
                .filter(d -> d.id().equals(sensorId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Available data from sensor %s could not get retrieved".formatted(sensorId)));
    }

    private Location getSensorLocation(List<Location> locations, AvailableData data) {
        return locations.stream().
                filter(l -> l.id().equals(data.location()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Location from sensor %s could not get retrieved".formatted(data.id())));
    }

    private Parameter getSensorParameters(List<Parameter> parameters, AvailableData data) {
        return parameters.stream()
                .filter(p -> p.id().equals(data.parameter()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Parameters from sensor %s could not get retrieved".formatted(data.id())));
    }

    private String getName(List<Name> list) {
        return list.stream().filter(n -> n.culture().equals(CULTURE)).map(Name::message).collect(Collectors.joining());
    }
}
