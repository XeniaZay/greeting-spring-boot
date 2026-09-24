package com.frompegatojava.greeting;

import org.springframework.boot.context.properties.ConfigurationProperties;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "greeting")
public class GreetingProperties {
    private boolean enabled = true;
    @NotBlank private String prefix;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
}
