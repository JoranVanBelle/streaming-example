package org.streaming.example.domain.meetnetvlaamsebanken;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties("example.http.meetnet-vlaamse-banken.location-key")
public class LocationKeyMapping {

    private Map<String, String> mapping;

    public Map<String, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }
}
