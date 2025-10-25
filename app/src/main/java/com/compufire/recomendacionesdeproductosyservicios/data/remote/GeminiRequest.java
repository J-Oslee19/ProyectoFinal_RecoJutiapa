package com.compufire.recomendacionesdeproductosyservicios.data.remote;


import java.util.Collections;
import java.util.List;

public class GeminiRequest {
    public List<Content> contents;

    // Constructor para armar la request con un texto de usuario
    public GeminiRequest(String userText) {
        Part part = new Part(userText);
        Content content = new Content("user", Collections.singletonList(part));
        this.contents = Collections.singletonList(content);
    }

    public static class Content {
        public String role;
        public List<Part> parts;

        public Content(String role, List<Part> parts) {
            this.role = role;
            this.parts = parts;
        }
    }

    public static class Part {
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }
}