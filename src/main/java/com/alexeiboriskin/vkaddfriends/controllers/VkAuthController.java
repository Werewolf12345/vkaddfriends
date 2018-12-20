package com.alexeiboriskin.vkaddfriends.controllers;

import com.alexeiboriskin.vkaddfriends.services.VkService;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class VkAuthController {

    private final Integer APP_ID = 6790669;
    private final String CLIENT_SECRET = "e7ptpOy22xFSJWQbe3EN";
    private final String REDIRECT_URI = "http://localhost:8082/vk-code";

    private final VkService vkService;

    @Autowired
    public VkAuthController(VkService vkService) {
        this.vkService = vkService;
    }

    @GetMapping(value = "/vk-auth")
    public String vkAuthPage() {
        return "redirect:" + receivingCodeUri();
    }

    @GetMapping(value = "/vk-code")
    public String vkGetCode(@RequestParam("code") String code) throws ClientException, ApiException {

            TransportClient transportClient = new HttpTransportClient();
            VkApiClient vk = new VkApiClient(transportClient);

            UserAuthResponse authResponse =
                    vk.oauth().userAuthorizationCodeFlow(APP_ID,
                            CLIENT_SECRET, REDIRECT_URI, code).execute();

            UserActor actor = new UserActor(authResponse.getUserId(),
                    authResponse.getAccessToken());
            vkService.setActor(actor);

        return "redirect:/";
    }

    private String receivingCodeUri() {

        return "https://oauth.vk.com/authorize" + "?client_id=" + APP_ID
                + "&display=" + "popup"
                + "&redirect_uri=" + REDIRECT_URI
                + "&scope=" + "friends"
                + "&response_type=" + "code"
                + "&v" + "5.92";
    }
}
