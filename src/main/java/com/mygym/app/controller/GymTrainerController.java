package com.mygym.app.controller;

import static com.mygym.app.util.ChatConstants.CHAT_REQUEST_MAPPING;
import static com.mygym.app.util.ChatConstants.NICE_MODE_PROMPT;
import static com.mygym.app.util.ChatConstants.RUDE_MODE_PROMPT;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mygym.app.model.ChatRequest;
import com.mygym.app.model.ChatResponse;

@RestController
@RequestMapping(CHAT_REQUEST_MAPPING)
@CrossOrigin(origins = "*")
public class GymTrainerController {

    private final ChatClient chatClient;

    public GymTrainerController(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {

        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        this.chatClient = chatClientBuilder
                .defaultAdvisors(memoryAdvisor) 
                .build();
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String systemPrompt = "nice".equalsIgnoreCase(request.getMode()) 
                ? NICE_MODE_PROMPT 
                : RUDE_MODE_PROMPT;

        String aiResponse = this.chatClient.prompt()
                .system(systemPrompt)
                .user(request.getMessage())
                .advisors(advisorSpec -> advisorSpec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, request.getSessionId()))
                .call()
                .content();

        return new ChatResponse(aiResponse);
    }
}
