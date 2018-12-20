package com.alexeiboriskin.vkaddfriends.controllers;

import com.alexeiboriskin.vkaddfriends.services.VkService;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.friends.responses.AddResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Controller
public class mainController {

    private final VkService vkService;

    @Autowired
    public mainController(VkService vkService) {
        this.vkService = vkService;
    }

    @GetMapping(value = "/")
    public String vkAuthPage() {
        return "signin";
    }

    @GetMapping(value = "/upload")
    public String uploadFile() {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {

        List<Integer> listOfNicks = processUploadedFile(file);

        if (!listOfNicks.isEmpty()) {
            TransportClient transportClient = new HttpTransportClient();
            VkApiClient vk = new VkApiClient(transportClient);

            AddResponse response = null;
            for (Integer id : listOfNicks) {
                try {

                    response = vk
                        .friends()
                        .add(vkService.getActor(), id)
                        .text("Hi! Please add me as your best fried ASAP")
                        .execute();

                } catch (ApiException e) {
                    log.error("Vk API exception");
                } catch (ClientException e) {
                    log.error("Vk client exception");
                }
            }
        }
        return "redirect:/upload";
    }

    private List<Integer> processUploadedFile(MultipartFile file) {

        try (InputStream inputStream = file.getInputStream()) {
            Stream<String> stream =
                    new BufferedReader(new InputStreamReader(inputStream)).lines();

            return stream.flatMap(line -> Stream.of(line.split("[\\p{Blank" +
                    "}]+"))).filter(c -> c.matches("[0-9]*")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error processing uploaded file: " + file.getOriginalFilename());
        }

        return Collections.emptyList();
    }
}
