package com.wbq.gbf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.wbq.*")
public class GbfApplication {

    public static void main(String[] args) {
        SpringApplication.run(GbfApplication.class, args);
    }
}
