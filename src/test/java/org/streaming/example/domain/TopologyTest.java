package org.streaming.example.domain;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used when testing a (part of a) topology
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
//@BootstrapWith(SpringBootTestContextBootstrapper.class)
//@ExtendWith({SpringExtension.class})
//@EnableConfigurationProperties({KafkaProperties.class, KafkaTopicsProperties.class, WeatherRepository.class})
//@Import({MockAvroSerdesConfiguration.class, TopologyConfiguration.class, TopologyTestDriverConfiguration.class})
//@ImportAutoConfiguration
//@OverrideAutoConfiguration(enabled = false)
@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
public @interface TopologyTest {

    /**
     * A set of include filters which can be used to add otherwise filtered beans to the
     * application context.
     * @return include filters to apply
     */
    ComponentScan.Filter[] includeFilters() default {};

}
