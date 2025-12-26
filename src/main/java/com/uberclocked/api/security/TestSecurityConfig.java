package com.uberclocked.api.security;

import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@SuppressFBWarnings(
    value = "SPRING_CSRF_PROTECTION_DISABLED",
    justification = "CSRF disabled intentionally for test profile only")
@Configuration
@Profile("test")
public class TestSecurityConfig {
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }
}
