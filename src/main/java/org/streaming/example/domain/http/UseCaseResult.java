package org.streaming.example.domain.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.streaming.example.domain.WeatherEntity;

import java.util.List;
import java.util.Objects;

public record UseCaseResult<Data>(
        ResponseEntityStatus status,
        @JsonProperty("weather")
        Data data
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UseCaseResult<?> that = (UseCaseResult<?>) o;
        return status == that.status && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, data);
    }

    @Override
    public String toString() {
        return "UseCaseResult{" +
               "status=" + status +
               ", data=" + data +
               '}';
    }
}
