package org.streaming.example.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.streaming.example.domain.MutableClock;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Configuration
public class TestClockConfiguration {

    @Bean
    @Primary
    Clock testClock() {
        return new MutableClock(Instant.parse("2001-06-19T23:00:00.00Z"),
                ZoneId.of("Europe/Brussels"));
    }

}