package com.heyuan.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GmallRedissonTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallRedissonTestApplication.class, args);
    }

}
