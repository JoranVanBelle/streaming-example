package org.streaming.example.mothers;

import org.streaming.example.adapter.events.UnkiteableWindDirectionDetected;

public class UnkiteableWindDirectionDetectedMother {
    private String sensorId;
    private String location;
    private String value;
    private String unit;
    private String description;

    public UnkiteableWindDirectionDetectedMother(String sensorId, String location, String value, String unit, String description) {
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

    public static UnkiteableWindDirectionDetectedMotherBuilder newEvent() {
        return new UnkiteableWindDirectionDetectedMotherBuilder();
    }
    
    public static class UnkiteableWindDirectionDetectedMotherBuilder {

        private String sensorId = "NPBGH1";
        private String location = "Nieuwpoort - Wind measurement";
        private String value = "-1";
        private String unit = "deg";
        private String description = "Average wind direction";

        public UnkiteableWindDirectionDetectedMotherBuilder withSensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        public UnkiteableWindDirectionDetectedMotherBuilder withLocation(String location) {
            this.location = location;
            return this;
        }

        public UnkiteableWindDirectionDetectedMotherBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        public UnkiteableWindDirectionDetectedMotherBuilder withUnit(String unit) {
            this.unit = unit;
            return this;
        }

        public UnkiteableWindDirectionDetectedMotherBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public UnkiteableWindDirectionDetected buildEvent() {
            return UnkiteableWindDirectionDetected.newBuilder()
                    .setSensorId(sensorId)
                    .setLocation(location)
                    .setValue(value)
                    .setUnit(unit)
                    .setDescription(description)
                    .build();
        }

        public UnkiteableWindDirectionDetectedMother buildMother() {
            
            return new UnkiteableWindDirectionDetectedMother(
                    sensorId,
                    location,
                    value,
                    unit,
                    description
            );
        }
    }
}
