package com.FromPegaToJava.demo;

import com.frompegatojava.greeting.GreetingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    GreetingService greetingService;

    @Test
    void contextLoads() {
        assertThat(greetingService).isNotNull();
        assertThat(greetingService.greet("World")).isEqualTo("Hello, World!");
    }
}
