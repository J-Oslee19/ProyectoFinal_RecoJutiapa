package com.compufire.recomendacionesdeproductosyservicios.data.remote;

import android.util.Log;

import androidx.annotation.Nullable;

import com.compufire.recomendacionesdeproductosyservicios.BuildConfig;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.UserPrefs;
import com.compufire.recomendacionesdeproductosyservicios.util.RankingUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Repositorio SÍNCRONO (usa call.execute()) porque el Fragment ya usa new Thread(...)
 * Devuelve el texto de Gemini o un fallback local si algo falla.
 */
public class GeminiRepository {

    private static final String TAG = "GeminiRepository";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/";
    // Modelo recomendado (rápido, estable):
    private static final String MODEL = "gemini-1.5-flash";

    private final GeminiApiService api;

    public GeminiRepository() {
        // Logging de red (BODY para ver petición/respuesta en Logcat con tag "OkHttp")
        HttpLoggingInterceptor log = new HttpLoggingInterceptor(m -> Log.d("OkHttp", m));
        log.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .addInterceptor(log)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // debe terminar con /
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        api = retrofit.create(GeminiApiService.class);
    }

    /**
     * Ejecutar SIEMPRE FUERA del hilo principal.
     * @return texto de Gemini o fallback local si hay error.
     */
    public String explicar(@Nullable Item item, @Nullable UserPrefs prefs) {
        final String apiKey = BuildConfig.GEMINI_API_KEY != null ? BuildConfig.GEMINI_API_KEY.trim() : "";
        if (apiKey.isEmpty()) {
            Log.w(TAG, "BuildConfig.GEMINI_API_KEY vacío");
            return fallback(item, prefs);
        }

        final String prompt = buildPrompt(item, prefs);

        try {
            // Tu clase GeminiRequest ya acepta el prompt directamente
            GeminiRequest req = new GeminiRequest(prompt);

            // Llamada SÍNCRONA (bloquea este hilo, NO el UI)
            Response<GeminiResponse> resp = api.generateContent(MODEL, apiKey, req).execute();

            if (!resp.isSuccessful()) {
                String eb = resp.errorBody() != null ? resp.errorBody().string() : null;
                Log.e(TAG, "HTTP " + resp.code() + (eb != null ? (": " + eb) : ""));
                return fallback(item, prefs);
            }

            GeminiResponse body = resp.body();
            String texto = extractFirstText(body);
            if (texto == null || texto.trim().isEmpty()) {
                Log.w(TAG, "Respuesta sin texto en candidates");
                return fallback(item, prefs);
            }

            return texto.trim();

        } catch (IOException e) {
            Log.e(TAG, "Error de red/timeout", e);
            return fallback(item, prefs);
        } catch (Exception e) {
            Log.e(TAG, "Error inesperado", e);
            return fallback(item, prefs);
        }
    }

    // -------- helpers --------

    private String buildPrompt(@Nullable Item item, @Nullable UserPrefs prefs) {
        String nombre = item != null && item.nombre != null ? item.nombre : "—";
        String categoria = item != null && item.categoria != null ? item.categoria : "—";
        double precio = item != null ? item.precio : 0.0;
        double rating = item != null ? item.rating : 0.0;

        StringBuilder sb = new StringBuilder();
        sb.append("Eres un asistente que explica recomendaciones para un estudiante en Jutiapa. ")
                .append("Devuelve 2-4 oraciones claras, sin inventar datos.\n")
                .append("Nombre: ").append(nombre).append("\n")
                .append("Categoría: ").append(categoria).append("\n")
                .append("Precio: Q").append(precio).append("\n")
                .append("Rating: ").append(rating).append("\n");

        if (prefs != null && prefs.categorias != null && !prefs.categorias.isEmpty()) {
            sb.append("Preferencias del usuario: ").append(prefs.categorias.toString()).append("\n");
        }
        return sb.toString();
    }

    private String extractFirstText(@Nullable GeminiResponse body) {
        if (body == null || body.candidates == null || body.candidates.isEmpty()) return null;
        if (body.candidates.get(0) == null) return null;
        if (body.candidates.get(0).content == null) return null;
        if (body.candidates.get(0).content.parts == null || body.candidates.get(0).content.parts.isEmpty()) return null;
        if (body.candidates.get(0).content.parts.get(0) == null) return null;
        return body.candidates.get(0).content.parts.get(0).text;
    }

    /** Fallback local (texto breve de tu RankingUtils). */
    private String fallback(@Nullable Item item, @Nullable UserPrefs prefs) {
        return RankingUtils.explicarCorto(item, prefs, 0);
    }
}
