package com.FromPegaToJava.demo;

import com.frompegatojava.greeting.GreetingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("custom-greeting")
public class UserGreetingConfig {

    @Bean
    public GreetingService customGreeting() {
        return name -> "Привет из demo, " + name + "!";
    }
}