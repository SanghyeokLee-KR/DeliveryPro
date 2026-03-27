package com.icia.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeliveryProApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryProApplication.class, args);
    }

}
