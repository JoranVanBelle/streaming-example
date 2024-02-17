package org.streaming.example.mothers;

import org.streaming.example.RawDataMeasured;

public class RawWaveHeightMeasured {

    public static RawDataMeasuredBuilder newEvent() {
        return new RawDataMeasuredBuilder();
    }

    public static class RawDataMeasuredBuilder {

        private String sensorId = "NPBGH1";
        private String location = "Nieuwpoort - Buoy";
        private String value = "39.0";
        private String unit = "cm";
        private String description = "10% highest waves";

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

        public RawDataMeasuredBuilder withNotKiteableWave() {
            this.value = "0";
            return this;
        }

        public RawDataMeasuredBuilder withKiteableWave() {
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
