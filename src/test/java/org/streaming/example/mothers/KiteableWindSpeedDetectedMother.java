package org.streaming.example.mothers;


import org.streaming.example.adapter.events.KiteableWindSpeedDetected;

public class KiteableWindSpeedDetectedMother {
    private String sensorId;
    private String location;
    private String value;
    private String unit;
    private String description;

    public KiteableWindSpeedDetectedMother(String sensorId, String location, String value, String unit, String description) {
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

    public static KiteableWindSpeedDetectedMotherBuilder newEvent() {
        return new KiteableWindSpeedDetectedMotherBuilder();
    }
    
    public static class KiteableWindSpeedDetectedMotherBuilder {

        private String sensorId = "NPBGH1";
        private String location = "Nieuwpoort - Wind measurement";
        private String value = "1000";
        private String unit = "m/s";
        private String description = "Average wind speed (at 10 m height)";

        public KiteableWindSpeedDetectedMotherBuilder withSensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        public KiteableWindSpeedDetectedMotherBuilder withLocation(String location) {
            this.location = location;
            return this;
        }

        public KiteableWindSpeedDetectedMotherBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        public KiteableWindSpeedDetectedMotherBuilder withUnit(String unit) {
            this.unit = unit;
            return this;
        }

        public KiteableWindSpeedDetectedMotherBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public KiteableWindSpeedDetected buildEvent() {
            return KiteableWindSpeedDetected.newBuilder()
                    .setSensorId(sensorId)
                    .setLocation(location)
                    .setValue(value)
                    .setUnit(unit)
                    .setDescription(description)
                    .build();
        }

        public KiteableWindSpeedDetectedMother buildMother() {
            
            return new KiteableWindSpeedDetectedMother(
                    sensorId,
                    location,
                    value,
                    unit,
                    description
            );
        }
    }
}
