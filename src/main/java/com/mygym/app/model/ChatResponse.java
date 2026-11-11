package com.mygym.app.model;

public record ChatResponse(String response, boolean degraded) {

    public static ChatResponse of(String response) {
        return new ChatResponse(response, false);
    }

    public static ChatResponse fallback(String response) {
        return new ChatResponse(response, true);
    }
}
