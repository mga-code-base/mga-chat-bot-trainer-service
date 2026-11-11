package com.mygym.app.controller;

import static com.mygym.app.util.ChatConstants.CHAT_REQUEST_MAPPING;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mygym.app.model.ChatRequest;
import com.mygym.app.model.ChatResponse;
import com.mygym.app.service.TrainerChatService;

@RestController
@RequestMapping(CHAT_REQUEST_MAPPING)
public class GymTrainerController {

    private final TrainerChatService chatService;

    public GymTrainerController(TrainerChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return chatService.reply(request);
    }
}
