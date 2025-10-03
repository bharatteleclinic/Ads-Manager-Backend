package com.manager.ads.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// public class SecurityConfig {

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//             .csrf().disable() // disable CSRF for Postman testing
//             .authorizeHttpRequests()
//                 .requestMatchers("/auth/**").permitAll() // allow all auth endpoints
//                 .anyRequest().authenticated() // everything else needs authentication
//             .and()
//             .httpBasic(); // can remove if using JWT for auth

//         return http.build();
//     }
// }

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .anyRequest().permitAll(); // ✅ all endpoints are open

        return http.build();
    }
}
