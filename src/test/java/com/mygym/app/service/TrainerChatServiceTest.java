package com.mygym.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import com.mygym.app.model.ChatRequest;
import com.mygym.app.model.ChatResponse;
import com.mygym.app.util.ChatConstants;

@ExtendWith(MockitoExtension.class)
class TrainerChatServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatMemory chatMemory;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    @Mock
    private CircuitBreakerFactory<?, ?> circuitBreakers;

    @Mock
    private CircuitBreaker circuitBreaker;

    private TrainerChatService service;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.defaultAdvisors(any(Advisor[].class))).thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(circuitBreakers.create(TrainerChatService.MODEL_CIRCUIT)).thenReturn(circuitBreaker);
        service = new TrainerChatService(chatClientBuilder, chatMemory, circuitBreakers);
    }

    @Test
    @DisplayName("Should use the nice prompt and return the model reply")
    void testReply_NiceMode() {
        passThroughCircuit();
        stubModel(ChatConstants.NICE_MODE_PROMPT, "How do I fix my squat stance?", "Feet shoulder-width apart!");

        ChatResponse response = service.reply(new ChatRequest("How do I fix my squat stance?", "nice", "session-101"));

        assertEquals("Feet shoulder-width apart!", response.response());
        assertFalse(response.degraded());
        verify(requestSpec).system(ChatConstants.NICE_MODE_PROMPT);
    }

    @Test
    @DisplayName("Should default to the rude prompt")
    void testReply_DefaultsToRudeMode() {
        passThroughCircuit();
        stubModel(ChatConstants.RUDE_MODE_PROMPT, "I'm feeling tired today.", "Stop crying!");

        ChatResponse response = service.reply(new ChatRequest("I'm feeling tired today.", null, "session-202"));

        assertEquals("Stop crying!", response.response());
        verify(requestSpec).system(ChatConstants.RUDE_MODE_PROMPT);
    }

    @Test
    @DisplayName("Should return a degraded reply when the model is unavailable")
    void testReply_FallbackWhenModelFails() {
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            Function<Throwable, ChatResponse> fallback = invocation.getArgument(1);
            return fallback.apply(new IllegalStateException("ollama down"));
        });

        ChatResponse response = service.reply(new ChatRequest("Hi", "nice", "session-303"));

        assertTrue(response.degraded());
        assertEquals(TrainerChatService.FALLBACK_REPLY, response.response());
    }

    private void passThroughCircuit() {
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            Supplier<ChatResponse> call = invocation.getArgument(0);
            return call.get();
        });
    }

    @SuppressWarnings("unchecked")
    private void stubModel(String prompt, String message, String reply) {
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(prompt)).thenReturn(requestSpec);
        when(requestSpec.user(message)).thenReturn(requestSpec);
        when(requestSpec.advisors(any(Consumer.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn(reply);
    }
}
