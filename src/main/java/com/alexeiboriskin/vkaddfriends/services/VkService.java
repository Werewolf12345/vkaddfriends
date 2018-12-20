package com.alexeiboriskin.vkaddfriends.services;

import com.vk.api.sdk.client.actors.UserActor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Data
@Service
public class VkService {
    private UserActor actor;
}
