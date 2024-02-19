package org.streaming.example.mothers;

import org.streaming.example.KiteableWaveDetected;
import org.streaming.example.UnkiteableWaveDetected;

public class UnkiteableWaveDetectedMother {

    public static UnkiteableWaveDetectedBuilder newEvent() {
        return new UnkiteableWaveDetectedBuilder();
    }

    public static class UnkiteableWaveDetectedBuilder {

        private String sensorId = "NPBGH1";
        private String location = "Nieuwpoort - Buoy";
        private String value = "39.0";
        private String unit = "cm";
        private String description = "10% highest waves";

        public UnkiteableWaveDetectedBuilder withSensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        public UnkiteableWaveDetectedBuilder withLocation(String location) {
            this.location = location;
            return this;
        }

        public UnkiteableWaveDetectedBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        public UnkiteableWaveDetectedBuilder withUnit(String unit) {
            this.unit = unit;
            return this;
        }

        public UnkiteableWaveDetectedBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public UnkiteableWaveDetected build() {

            return UnkiteableWaveDetected.newBuilder()
                    .setSensorId(sensorId)
                    .setLocation(location)
                    .setValue(value)
                    .setUnit(unit)
                    .setDescription(description)
                    .build();
        }
    }
    
}
