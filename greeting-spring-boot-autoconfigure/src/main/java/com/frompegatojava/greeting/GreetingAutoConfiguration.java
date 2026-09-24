package com.frompegatojava.greeting;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "greeting",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true          // ← если свойства нет — считаем включённой
)
@EnableConfigurationProperties(GreetingProperties.class)
public class GreetingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(GreetingService.class)
    public GreetingService greetingService(GreetingProperties props) {
        return new DefaultGreetingService(props.getPrefix());
    }
}