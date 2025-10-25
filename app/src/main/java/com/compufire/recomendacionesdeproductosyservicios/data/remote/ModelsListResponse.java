package com.compufire.recomendacionesdeproductosyservicios.data.remote;

import java.util.List;

public class ModelsListResponse {
    public List<Model> models;

    public static class Model {
        // Ejemplos de name: "models/gemini-1.5-flash-latest"
        public String name;
        // La API devuelve algo como ["generateContent", ...]
        public List<String> supportedGenerationMethods;
        public String displayName;
        public String description;
    }
}
