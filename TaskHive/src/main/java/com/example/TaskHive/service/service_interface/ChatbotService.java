package com.example.TaskHive.service.service_interface;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public interface ChatbotService
{

    Flux<String> chat(UserDetails userDetails, String message);
}
