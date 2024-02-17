package org.streaming.example.adapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MeetnetVlaamseBankenCatalog(
        @JsonProperty("AvailableData")
        List<AvailableData> availableData,
        @JsonProperty("Locations")
        List<Location> locations,
        @JsonProperty("Parameters")
        List<Parameter> parameters,
        @JsonProperty("ParameterTypes")
        Map<String, ParameterType> parametersTypes
) {

    public record AvailableData(
            @JsonProperty("ID")
            String id,
            @JsonProperty("Location")
            String location,
            @JsonProperty("Parameter")
            String parameter
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location(
            @JsonProperty("ID")
            String id,
            @JsonProperty("Name")
            List<Name> name,
            @JsonProperty("Description")
            List<Description> description
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Parameter(
            @JsonProperty("ID")
            String id,
            @JsonProperty("Name")
            List<Name> name,
            @JsonProperty("Unit")
            String unit
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ParameterType(
            @JsonProperty("ID")
            Integer id,
            @JsonProperty("SortOrder")
            Integer sortOrder,
            @JsonProperty("Name")
            List<Name> name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Name(
            @JsonProperty("Culture")
            String culture,
            @JsonProperty("Message")
            String message
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Description(
            @JsonProperty("Culture")
            String culture,
            @JsonProperty("Message")
            String message
    ) {}
}