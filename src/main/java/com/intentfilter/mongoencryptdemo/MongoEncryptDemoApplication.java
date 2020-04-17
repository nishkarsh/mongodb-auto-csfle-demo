package com.intentfilter.mongoencryptdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MongoEncryptDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongoEncryptDemoApplication.class, args);
    }
}
