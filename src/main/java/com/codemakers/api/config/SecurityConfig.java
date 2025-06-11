package com.codemakers.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author nicope
 * @version 1.0
 * 
 */

@Configuration
public class SecurityConfig {

	@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(
	                "/v3/api-docs/**",
	                "/swagger-ui/**",
	                "/swagger-ui.html",
	                "/api/v1/Usuario", 
	                "/api/v1/Usuario/**", 
	                "/api/v1/**"
	            ).permitAll()
	            .anyRequest().authenticated()
	        );
	    return http.build();
	}
}
