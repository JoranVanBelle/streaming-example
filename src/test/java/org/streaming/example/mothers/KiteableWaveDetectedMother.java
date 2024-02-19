package org.streaming.example.mothers;

import org.streaming.example.KiteableWaveDetected;

public class KiteableWaveDetectedMother {
    private String sensorId;
    private String location;
    private String value;
    private String unit;
    private String description;

    public KiteableWaveDetectedMother(String sensorId, String location, String value, String unit, String description) {
        this.sensorId = sensorId;
        this.location = location;
        this.value = value;
        this.unit = unit;
        this.description = description;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getLocation() {
        return location;
    }

    public String getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public String getDescription() {
        return description;
    }

    public static KiteableWaveDetectedBuilder newEvent() {
        return new KiteableWaveDetectedBuilder();
    }
    
    public static class KiteableWaveDetectedBuilder {

        private String sensorId = "NPBGH1";
        private String location = "Nieuwpoort - Buoy";
        private String value = "39.0";
        private String unit = "cm";
        private String description = "10% highest waves";

        public KiteableWaveDetectedBuilder withSensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        public KiteableWaveDetectedBuilder withLocation(String location) {
            this.location = location;
            return this;
        }

        public KiteableWaveDetectedBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        public KiteableWaveDetectedBuilder withUnit(String unit) {
            this.unit = unit;
            return this;
        }

        public KiteableWaveDetectedBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public KiteableWaveDetected buildEvent() {
            return KiteableWaveDetected.newBuilder()
                    .setSensorId(sensorId)
                    .setLocation(location)
                    .setValue(value)
                    .setUnit(unit)
                    .setDescription(description)
                    .build();
        }

        public KiteableWaveDetectedMother buildMother() {
            
            return new KiteableWaveDetectedMother(
                    sensorId,
                    location,
                    value,
                    unit,
                    description
            );
        }
    }
}
