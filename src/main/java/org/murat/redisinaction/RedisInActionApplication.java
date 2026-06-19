package org.murat.redisinaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RedisInActionApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisInActionApplication.class, args);
    }

}
