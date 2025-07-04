package com.codemakers.api.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

	private final Environment env;

	@Bean
	public OpenAPI customOpenAPI() {
	return new OpenAPI().info(new Info().title(env.getProperty("spring.application.name") + " Service API")
	.version("1.0").description(env.getProperty("spring.application.name") + " API Description")
	.termsOfService("https://multi-acueductos.com.co")
	.contact(new Contact().name("MULTI ACUEDUCTOS").email("admongestionplus360@gmail.com"))
	.license(new License().name("LICENSE").url("LICENSE URL")));
	}

	@Bean
	public GroupedOpenApi groupPermitted() {
	return GroupedOpenApi.builder().group(env.getProperty("spring.application.name"))
	.packagesToScan("com.codemakers.api.controller")
	.packagesToExclude("com.codemakers.commons.dtos","com.codemakers.commons.entities")
	.build();
	}
}
