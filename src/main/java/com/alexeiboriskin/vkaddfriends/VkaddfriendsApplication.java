package com.alexeiboriskin.vkaddfriends;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@SpringBootApplication
public class VkaddfriendsApplication {

    public static void main(String[] args) {
        SpringApplication.run(VkaddfriendsApplication.class, args);
    }

}

