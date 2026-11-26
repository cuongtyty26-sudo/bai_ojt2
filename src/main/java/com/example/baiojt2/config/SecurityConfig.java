package com.example.baiojt2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration permitting public access to the home page and static assets.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/css/**", "/js/**", "/images/**", "/webjars/**", "/error").permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(form -> form.permitAll())
				.logout(logout -> logout.permitAll());

		return http.build();
	}
}
