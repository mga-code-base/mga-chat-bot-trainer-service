package com.mygym.app.service;

import static com.mygym.app.util.ChatConstants.NICE_MODE_PROMPT;
import static com.mygym.app.util.ChatConstants.RUDE_MODE_PROMPT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Service;

import com.mygym.app.model.ChatRequest;
import com.mygym.app.model.ChatResponse;

@Service
public class TrainerChatService {

    public static final String MODEL_CIRCUIT = "ollama";
    static final String FALLBACK_REPLY = "Your trainer is catching their breath. Give it a minute and ask again.";
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerChatService.class);

    private final ChatClient chatClient;
    private final CircuitBreaker circuitBreaker;

    public TrainerChatService(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory,
            CircuitBreakerFactory<?, ?> circuitBreakers) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.circuitBreaker = circuitBreakers.create(MODEL_CIRCUIT);
    }

    @ConcurrencyLimit(4)
    public ChatResponse reply(ChatRequest request) {
        String systemPrompt = "nice".equalsIgnoreCase(request.mode()) ? NICE_MODE_PROMPT : RUDE_MODE_PROMPT;
        return circuitBreaker.run(() -> ChatResponse.of(chatClient.prompt()
                .system(systemPrompt)
                .user(request.message())
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, request.sessionId()))
                .call()
                .content()), failure -> {
                    LOGGER.warn("Model call failed for session {}: {}", request.sessionId(), failure.toString());
                    return ChatResponse.fallback(FALLBACK_REPLY);
                });
    }
}
