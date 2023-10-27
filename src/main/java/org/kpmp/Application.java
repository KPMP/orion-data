package org.kpmp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@EnableCaching
@SpringBootApplication
@ComponentScan(basePackages = {
		"org.kpmp" }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {}))
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}