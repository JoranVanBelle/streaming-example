package org.streaming.example.adapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MeetnetVlaamseBankenData(
        @JsonProperty("ID")
        String id,

        @JsonProperty("Timestamp")
        String timestamp,

        @JsonProperty("Value")
        double value
) {
}
