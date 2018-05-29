package org.kpmp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = { "org.kpmp" })
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}