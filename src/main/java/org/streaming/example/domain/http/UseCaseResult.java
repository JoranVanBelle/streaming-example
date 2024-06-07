package org.streaming.example.domain.http;

import java.util.Objects;

/**
 * The result of a use case
 * @param status
 * @param data
 * @param <Data>
 */
public record UseCaseResult<Data>(
        ResponseEntityStatus status,
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
