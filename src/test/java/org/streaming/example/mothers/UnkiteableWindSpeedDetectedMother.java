package org.streaming.example.mothers;


import org.streaming.example.adapter.events.UnkiteableWindSpeedDetected;

public class UnkiteableWindSpeedDetectedMother {
    private String sensorId;
    private String location;
    private String value;
    private String unit;
    private String description;

    public UnkiteableWindSpeedDetectedMother(String sensorId, String location, String value, String unit, String description) {
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

    public static UnkiteableWindSpeedDetectedMotherBuilder newEvent() {
        return new UnkiteableWindSpeedDetectedMotherBuilder();
    }
    
    public static class UnkiteableWindSpeedDetectedMotherBuilder {


        private String sensorId = "NPBGH1";
        private String location = "Nieuwpoort - Wind measurement";
        private String value = "0";
        private String unit = "m/s";
        private String description = "Average wind speed (at 10 m height)";

        public UnkiteableWindSpeedDetectedMotherBuilder withSensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        public UnkiteableWindSpeedDetectedMotherBuilder withLocation(String location) {
            this.location = location;
            return this;
        }

        public UnkiteableWindSpeedDetectedMotherBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        public UnkiteableWindSpeedDetectedMotherBuilder withUnit(String unit) {
            this.unit = unit;
            return this;
        }

        public UnkiteableWindSpeedDetectedMotherBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public UnkiteableWindSpeedDetected buildEvent() {
            return UnkiteableWindSpeedDetected.newBuilder()
                    .setSensorId(sensorId)
                    .setLocation(location)
                    .setValue(value)
                    .setUnit(unit)
                    .setDescription(description)
                    .build();
        }

        public UnkiteableWindSpeedDetectedMother buildMother() {
            
            return new UnkiteableWindSpeedDetectedMother(
                    sensorId,
                    location,
                    value,
                    unit,
                    description
            );
        }
    }
}
