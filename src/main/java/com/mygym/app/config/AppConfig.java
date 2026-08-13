package com.mygym.app.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    
    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public OllamaApi ollamaApi() {
        return new OllamaApi(ollamaBaseUrl);
    }

    @Bean
    public OllamaChatModel chatModel(OllamaApi ollamaApi) {
        return OllamaChatModel.builder()
                .withOllamaApi(ollamaApi)
                .withDefaultOptions(OllamaOptions.builder()
                        .withModel("llama3.2")
                        .build())
                .build();
    }
}
