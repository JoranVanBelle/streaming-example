package org.streaming.example.adapter.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("example.kafka.topics")
public class KafkaTopicsProperties {

    private String rawDataMeasured;
    private String windDetected;
    private String waveDetected;
    private String windDirectionDetected;
    private String rekeyedWindDetected;
    private String rekeyedWaveDetected;
    private String rekeyedWindDirectionDetected;

    public String getRekeyedWindDirectionDetected() {
        return rekeyedWindDirectionDetected;
    }

    public void setRekeyedWindDirectionDetected(String rekeyedWindDirectionDetected) {
        this.rekeyedWindDirectionDetected = rekeyedWindDirectionDetected;
    }

    public String getRekeyedWindDetected() {
        return rekeyedWindDetected;
    }

    public void setRekeyedWindDetected(String rekeyedWindDetected) {
        this.rekeyedWindDetected = rekeyedWindDetected;
    }

    public String getRekeyedWaveDetected() {
        return rekeyedWaveDetected;
    }

    public void setRekeyedWaveDetected(String rekeyedWaveDetected) {
        this.rekeyedWaveDetected = rekeyedWaveDetected;
    }

    public String getWindDirectionDetected() {
        return windDirectionDetected;
    }

    public void setWindDirectionDetected(String windDirectionDetected) {
        this.windDirectionDetected = windDirectionDetected;
    }

    public String getWaveDetected() {
        return waveDetected;
    }

    public void setWaveDetected(String waveDetected) {
        this.waveDetected = waveDetected;
    }

    public String getWindDetected() {
        return windDetected;
    }

    public void setWindDetected(String windDetected) {
        this.windDetected = windDetected;
    }

    public String getRawDataMeasured() {
        return rawDataMeasured;
    }

    public void setRawDataMeasured(String rawDataMeasured) {
        this.rawDataMeasured = rawDataMeasured;
    }

    public List<String> topics() {
        return List.of(
                rawDataMeasured, windDetected, waveDetected, windDetected
        );
    }
}
