package org.streaming.example.mothers;


import org.streaming.example.adapter.events.RawDataMeasured;

public class RawWindSpeedMeasured {

    public static RawDataMeasuredBuilder newEvent() {
        return new RawDataMeasuredBuilder();
    }

    public static class RawDataMeasuredBuilder {

        private String sensorId = "NP7WVC";
        private String location = "Nieuwpoort - Wind measurement";
        private String value = "5.9";
        private String unit = "m/s";
        private String description = "Average wind speed (at 10 m height)";

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

        public RawDataMeasuredBuilder withNotKiteableWind() {
            this.value = "0";
            return this;
        }

        public RawDataMeasuredBuilder withKiteableWind() {
            this.value = "1000";
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
