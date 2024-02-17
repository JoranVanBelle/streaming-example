package org.streaming.example.mothers;

import org.streaming.example.RawDataMeasured;

public class RawWindDirectionMeasured {

    public static RawDataMeasuredBuilder newEvent() {
        return new RawDataMeasuredBuilder();
    }

    public static class RawDataMeasuredBuilder {

        private String sensorId = "NP7WRS";
        private String location = "Nieuwpoort - Wind measurement";
        private String value = "185.0";
        private String unit = "deg";
        private String description = "Average wind direction";

        public RawDataMeasuredBuilder withSensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        public RawDataMeasuredBuilder withLocation(String location) {
            this.location = location;
            return this;
        }

        public RawDataMeasuredBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        public RawDataMeasuredBuilder withUnit(String unit) {
            this.unit = unit;
            return this;
        }

        public RawDataMeasuredBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public RawDataMeasuredBuilder withNotKiteableWindDirection() {
            this.value = "-1";
            return this;
        }

        public RawDataMeasuredBuilder withKiteableWindDirection() {
            this.value = "270";
            return this;
        }

        public RawDataMeasured build() {
            return RawDataMeasured.newBuilder()
                    .setSensorId(sensorId)
                    .setLocation(location)
                    .setValue(value)
                    .setUnit(unit)
                    .setDescription(description)
                    .build();
        }
    }
}
