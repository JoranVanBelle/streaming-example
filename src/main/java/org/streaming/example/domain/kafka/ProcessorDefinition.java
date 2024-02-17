package org.streaming.example.domain.kafka;

import org.apache.kafka.streams.processor.api.ProcessorSupplier;

import java.util.List;

public class ProcessorDefinition<KIn, VIn, KOut, VOut> {

    private final String name;
    private final ProcessorSupplier<KIn, VIn, KOut, VOut> supplier;
    private final String[] parents;

    public ProcessorDefinition(String name, ProcessorSupplier<KIn, VIn, KOut, VOut> supplier, String[] parents) {
        this.name = name;
        this.supplier = supplier;
        this.parents = parents;
    }

    public String name() {
        return name;
    }

    public ProcessorSupplier<KIn, VIn, KOut, VOut> supplier() {
        return supplier;
    }

    public String[] parents() {
        return parents;
    }

    public static <KIn, VIn, KOut, VOut> ProcessorDefinitionBuilder<KIn, VIn, KOut, VOut> newProcessorDefinition() {
        return new ProcessorDefinitionBuilder<>();
    }

    public static class ProcessorDefinitionBuilder<KIn, VIn, KOut, VOut> {
        private String name;
        private ProcessorSupplier<KIn, VIn, KOut, VOut> supplier;
        private String[] parents;

        public ProcessorDefinitionBuilder<KIn, VIn, KOut, VOut> withName(String name) {
            this.name = name;
            return this;
        }

        public ProcessorDefinitionBuilder<KIn, VIn, KOut, VOut> withProcessorSupplier(ProcessorSupplier<KIn, VIn, KOut, VOut> supplier) {
            this.supplier = supplier;
            return this;
        }

        public ProcessorDefinitionBuilder<KIn, VIn, KOut, VOut> withParents(List<String> parents) {
            this.parents = parents.toArray(String[]::new);
            return this;
        }

        public ProcessorDefinitionBuilder<KIn, VIn, KOut, VOut> withParents(String... parents) {
            this.parents = parents;
            return this;
        }

        public ProcessorDefinition<KIn, VIn, KOut, VOut> build() {
            return new ProcessorDefinition<>(name, supplier, parents);
        }
    }
}
