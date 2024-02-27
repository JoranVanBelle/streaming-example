package org.streaming.example.domain.kafka;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;

/**
 * A builder used to create Sink-nodes in the topology
 */
public class SinkDefinition {

    private final Serializer<?> valueSerializer;
    private final String topic;
    private final String[] parents;

    public SinkDefinition(Serializer<?> valueSerializer, String topic, String[] parents) {
        this.valueSerializer = valueSerializer;
        this.topic = topic;
        this.parents = parents;
    }

    public SinkDefinition(String topic) {
        this.topic = topic;
        valueSerializer = new StringSerializer();
        parents = new String[]{};
    }

    public String topic() {
        return topic;
    }

    public Serializer<?> valueSerializer() {
        return valueSerializer;
    }

    public String[] parents() {
        return parents;
    }

    public static SinkDefinitionBuilder newSinkDefinition() {
        return new SinkDefinitionBuilder();
    }

    public static class SinkDefinitionBuilder {
        private Serializer<?> valueSerializer = new StringSerializer();
        private String topic;
        private String[] parents = new String[]{};

        public SinkDefinitionBuilder withValueSerializer(Serializer<?> valueSerializer) {
            this.valueSerializer = valueSerializer;
            return this;
        }

        public SinkDefinitionBuilder withTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public SinkDefinitionBuilder withParents(List<String> parents) {
            this.parents = parents.toArray(String[]::new);
            return this;
        }

        public SinkDefinitionBuilder withParents(String... parents) {
            this.parents = parents;
            return this;
        }

        public SinkDefinition build() {
            return new SinkDefinition(valueSerializer, topic, parents);
        }
    }
}
