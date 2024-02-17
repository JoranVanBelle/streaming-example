package org.streaming.example.domain.kafka;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class SourceDefinition {

    private final Deserializer<?> valueDeserializer;
    private final String topic;

    public SourceDefinition(Deserializer<?> valueDeserializer, String topic) {
        this.valueDeserializer = valueDeserializer;
        this.topic = topic;
    }

    public SourceDefinition(String topic) {
        this.topic = topic;
        valueDeserializer = new StringDeserializer();
    }

    public String topic() {
        return topic;
    }

    public Deserializer<?> valueDeserializer() {
        return valueDeserializer;
    }



    public static SourceDefinitionBuilder newSourceDefinition() {
        return new SourceDefinitionBuilder();
    }

    public static class SourceDefinitionBuilder {

        private String topic;
        private Deserializer<?> valueDeserializer = new StringDeserializer();

        public SourceDefinitionBuilder withTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public SourceDefinitionBuilder withValueDeserializer(Deserializer<?> valueDeserializer) {
            this.valueDeserializer = valueDeserializer;
            return this;
        }

        public SourceDefinition build() {
            return new SourceDefinition(valueDeserializer, topic);
        }

    }
}
