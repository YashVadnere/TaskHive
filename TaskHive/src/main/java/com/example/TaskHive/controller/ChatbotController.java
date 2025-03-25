package com.example.TaskHive.controller;

import com.example.TaskHive.service.service_interface.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
public class ChatbotController
{
    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService)
    {
        this.chatbotService = chatbotService;
    }

    @GetMapping("/chatbot")
    public ResponseEntity<Flux<String>> chat(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String message
    ) {
        return new ResponseEntity<>(chatbotService.chat(userDetails, message), HttpStatus.OK);
    }

}
