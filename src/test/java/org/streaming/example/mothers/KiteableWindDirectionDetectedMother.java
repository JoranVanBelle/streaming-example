package org.streaming.example.mothers;

import org.streaming.example.KiteableWaveDetected;
import org.streaming.example.KiteableWindDirectionDetected;

public class KiteableWindDirectionDetectedMother {
    private String sensorId;
    private String location;
    private String value;
    private String unit;
    private String description;

    public KiteableWindDirectionDetectedMother(String sensorId, String location, String value, String unit, String description) {
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

    public static KiteableWindDirectionDetectedMotherBuilder newEvent() {
        return new KiteableWindDirectionDetectedMotherBuilder();
    }
    
    public static class KiteableWindDirectionDetectedMotherBuilder {

        private String sensorId = "NPBGH1";
        private String location = "Nieuwpoort - Wind measurement";
        private String value = "270";
        private String unit = "deg";
        private String description = "Average wind direction";

        public KiteableWindDirectionDetectedMotherBuilder withSensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        public KiteableWindDirectionDetectedMotherBuilder withLocation(String location) {
            this.location = location;
            return this;
        }

        public KiteableWindDirectionDetectedMotherBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        public KiteableWindDirectionDetectedMotherBuilder withUnit(String unit) {
            this.unit = unit;
            return this;
        }

        public KiteableWindDirectionDetectedMotherBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public KiteableWindDirectionDetected buildEvent() {
            return KiteableWindDirectionDetected.newBuilder()
                    .setSensorId(sensorId)
                    .setLocation(location)
                    .setValue(value)
                    .setUnit(unit)
                    .setDescription(description)
                    .build();
        }

        public KiteableWindDirectionDetectedMother buildMother() {
            
            return new KiteableWindDirectionDetectedMother(
                    sensorId,
                    location,
                    value,
                    unit,
                    description
            );
        }
    }
}
