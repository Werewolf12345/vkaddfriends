package com.alexeiboriskin.vkaddfriends.controllers;

import com.alexeiboriskin.vkaddfriends.services.VkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

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

    @GetMapping(value = "/signin")
    public String vkAuthPage() {
        return "signin";
    }

    @GetMapping(value = "/index")
    public String appDataInput() {
        return "index";
    }

    @GetMapping(value = "/upload")
    public ModelAndView uploadFile(@RequestParam(value = "message",required = false) String message) {
        ModelAndView model = new ModelAndView();
        model.setViewName("upload");

        if (message != null) {
            model.addObject("message", message);
        }

        return model;
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {

        List<Integer> listOfNicks = processUploadedFile(file);
        vkService.appendToList(listOfNicks);

        return "redirect:/upload?message=" + "Added " + listOfNicks.size() + " friends to waiting list";
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
