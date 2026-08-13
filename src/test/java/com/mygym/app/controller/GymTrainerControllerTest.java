package com.mygym.app.controller;

import com.mygym.app.model.ChatRequest;
import com.mygym.app.model.ChatResponse;
import com.mygym.app.util.ChatConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymTrainerControllerTest {

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

    private GymTrainerController controller;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.defaultAdvisors(any(Advisor[].class))).thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);

        controller = new GymTrainerController(chatClientBuilder, chatMemory);
    }

    @Test
    @DisplayName("Should use NICE_MODE_PROMPT when request mode is 'nice'")
    void testChat_NiceMode_AppliesNicePrompt() {
        ChatRequest request = new ChatRequest();
        request.setMode("nice");
        request.setMessage("How do I fix my squat stance?");
        request.setSessionId("session-101");

        String expectedReply = "Hey bro! Keep your feet shoulder-width apart and push through your heels!";

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(ChatConstants.NICE_MODE_PROMPT)).thenReturn(requestSpec);
        when(requestSpec.user(request.getMessage())).thenReturn(requestSpec);
        when(requestSpec.advisors(any(Consumer.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn(expectedReply);

        ChatResponse response = controller.chat(request);

        assertNotNull(response);
        assertEquals(expectedReply, response.getResponse());

        verify(requestSpec, times(1)).system(ChatConstants.NICE_MODE_PROMPT);
        verify(requestSpec, times(1)).user("How do I fix my squat stance?");
    }

    @Test
    @DisplayName("Should use RUDE_MODE_PROMPT when request mode is 'rude' or default")
    void testChat_RudeMode_AppliesRudePrompt() {
        ChatRequest request = new ChatRequest();
        request.setMode("rude");
        request.setMessage("I'm feeling tired today.");
        request.setSessionId("session-202");

        String expectedReply = "Stop crying! Fatigue is just an excuse leaving your body!";

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(ChatConstants.RUDE_MODE_PROMPT)).thenReturn(requestSpec);
        when(requestSpec.user(request.getMessage())).thenReturn(requestSpec);
        when(requestSpec.advisors(any(Consumer.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn(expectedReply);

        ChatResponse response = controller.chat(request);

        assertNotNull(response);
        assertEquals(expectedReply, response.getResponse());

        verify(requestSpec, times(1)).system(ChatConstants.RUDE_MODE_PROMPT);
        verify(requestSpec, times(1)).user("I'm feeling tired today.");
    }
}