package com.frompegatojava.greeting;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;

import static org.assertj.core.api.Assertions.assertThat;


class GreetingPropertiesValidationTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GreetingAutoConfiguration.class, ValidationAutoConfiguration.class));

    @Test
    void failsWhenPrefixBlank() {
        runner.withPropertyValues("greeting.enabled=true","greeting.prefix=   ")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .rootCause()
                            .isInstanceOf(BindValidationException.class)
                            .hasMessageContaining("greeting.prefix");
                });
    }

    @Test
    void failsWhenPrefixMissing() {
        runner.withPropertyValues("greeting.enabled=true")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .rootCause()
                            .isInstanceOf(BindValidationException.class)
                            .hasMessageContaining("greeting.prefix");
                });
    }

    @Test
    void passesWithValidPrefix() {
        runner.withPropertyValues("greeting.enabled=true","greeting.prefix=Hello")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    GreetingProperties p = context.getBean(GreetingProperties.class);
                    assertThat(p.getPrefix()).isEqualTo("Hello");
                });
    }

    @Test
    void shouldCreateDefaultBeansWhenEnabled() {
        runner.withPropertyValues("greeting.enabled=true","greeting.prefix=Hello")
                .run(context -> {
                    assertThat(context).hasSingleBean(GreetingService.class);
                    assertThat(context.getBean(GreetingService.class))
                            .isInstanceOf(DefaultGreetingService.class);
                });
    }


}
