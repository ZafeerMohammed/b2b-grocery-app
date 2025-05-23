package com.b2bapp.grocery.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests(auth -> auth
                                                                            .anyRequest()
                                                                            .permitAll()
                ).csrf(csrf -> csrf.disable());

        return httpSecurity.build();

    }


}
