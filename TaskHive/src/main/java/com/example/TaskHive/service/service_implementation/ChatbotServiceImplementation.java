package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.entity.User;
import com.example.TaskHive.exceptions.ResourceNotFound;
import com.example.TaskHive.repository.*;
import com.example.TaskHive.service.service_interface.ChatbotService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatbotServiceImplementation implements ChatbotService
{
    private final UserRepository userRepository;
    private final ChatClient chatClient;

    @Autowired
    public ChatbotServiceImplementation(
            UserRepository userRepository,
            ChatClient.Builder chatClient

    ) {
        this.userRepository = userRepository;

        this.chatClient = chatClient
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }


    @Override
    public Flux<String> chat(UserDetails userDetails, String message)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        ChatOptions chatOptions = ChatOptions.builder()
                .temperature(0.9)
                .build();

        return chatClient.prompt()
                .system("Your name is TaskBot. You are an intelligent assistant designed to help users with Scrum and Agile processes." +
                        " You assist in managing the Product Backlog, Sprint Backlog, and user Stories." +
                        " You provide guidance on Scrum principles, Sprint planning, and task tracking. \n" +
                        "\n" +
                        "Your responsibilities include:\n" +
                        "- Explaining Scrum concepts such as Sprints, Stories, Epics, and Retrospectives.\n" +
                        "- Assisting users in managing their backlog, creating Stories, and defining acceptance criteria.\n" +
                        "- Providing updates on ongoing Sprints, including completed and pending tasks.\n" +
                        "- Helping team members track progress and ensure tasks align with Sprint goals.\n" +
                        "- Answering questions related to Agile methodologies and best practices.\n" +
                        "\n" +
                        "Be concise and clear in responses. Keep communication professional yet user-friendly.\n" +
                        "The name of the user is "+user.getFullName()+". Greet first and then start the conversation")
                .options(chatOptions)
                .user(message)
                .stream()
                .content();
    }
}
