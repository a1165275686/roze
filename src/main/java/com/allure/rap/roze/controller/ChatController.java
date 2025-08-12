package com.allure.rap.roze.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    @PostMapping("/chat")
    public String chat(String msg) {
        log.info("msg:{}", msg);
        return "ok";
    }

}
