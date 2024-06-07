package org.streaming.example.domain;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAmount;

/**
 * A Mutable clock that can be used during testing
 */
public class MutableClock extends Clock {

    private Instant instant;

    private final ZoneId zone;

    public MutableClock(Instant instant, ZoneId zone) {
        this.instant = instant;
        this.zone = zone;
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new MutableClock(instant, zone);
    }

    @Override
    public Instant instant() {
        return instant;
    }

    public void fastForward(TemporalAmount temporalAmount) {
        set(instant().plus(temporalAmount));
    }

    public void rewind(TemporalAmount temporalAmount) {
        set(instant().minus(temporalAmount));
    }

    public void set(Instant instant) {
        this.instant = instant;
    }
}