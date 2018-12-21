package com.alexeiboriskin.vkaddfriends.controllers;

import com.alexeiboriskin.vkaddfriends.services.VkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class VkAuthController {

    private final VkService vkService;
    private Integer appId;

    @Autowired
    public VkAuthController(VkService vkService) {
        this.vkService = vkService;
    }

    @GetMapping(value = "/vk-auth")
    public String vkAuthPage() {
        return "redirect:" + receivingCodeUri();
    }

    @PostMapping(value = "/vk-token")
    public String vkParseUrl(@RequestParam("urlstring") String urlString) {

        vkService.setToken(urlString.replaceAll(".+(access_token=)", "")
            .replaceAll("&.+", ""));
        vkService.setUserId(Integer.parseInt(urlString.replaceAll(".+(user_id=)", "")
            .replaceAll("&.+", "")));

        return "redirect:/upload";
    }

    @PostMapping(value = "/vk-app")
    public String vkGetAppData(@RequestParam("appid") Integer appId) {

        this.appId = appId;

        return "redirect:/signin";
    }

    private String receivingCodeUri() {

        return "https://oauth.vk.com/authorize" + "?client_id=" + appId
                + "&display=" + "popup"
                + "&redirect_uri=" + "https://oauth.vk.com/blank.html"
                + "&scope=" + "wall,offline,friends"
                + "&response_type=" + "token"
                + "&v" + "5.92";
    }
}
