package com.compufire.recomendacionesdeproductosyservicios.data.remote.webhook;

import android.util.Log;

import com.compufire.recomendacionesdeproductosyservicios.BuildConfig;
import com.compufire.recomendacionesdeproductosyservicios.data.remote.booking.HmacUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class WebhookClient {
    private static final String TAG = "WebhookClient";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson = new Gson();
    private final String webhookUrl;
    private final String sharedSecret;

    public WebhookClient() {
        HttpLoggingInterceptor log = new HttpLoggingInterceptor(m -> Log.d("OkHttp", m));
        log.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client = new OkHttpClient.Builder()
                .connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(log)
                .build();

        String url = null;
        try {
            url = BuildConfig.FAVORITES_WEBHOOK_URL;
        } catch (Exception ignored) {}
        // Fallback: usar la URL que nos indicaste (Recobot)
        if (url == null || url.trim().isEmpty()) {
            url = "https://oslee987lo0ai0j7afc.app.n8n.cloud/webhook-test/Recobot";
        }
        this.webhookUrl = url;

        String secret = "";
        try { secret = BuildConfig.SHARED_SECRET; } catch (Exception ignored) {}
        this.sharedSecret = secret != null ? secret : "";
    }

    /**
     * Envía el payload como POST JSON y añade X-Signature si se dispone de secret.
     */
    public void sendFavoriteEvent(FavoritePayload payload) throws IOException {
        String bodyJson = gson.toJson(payload);
        String signature = null;
        if (sharedSecret != null && !sharedSecret.isEmpty()) {
            signature = HmacUtil.hmacSha256(sharedSecret, bodyJson);
        }

        RequestBody rb = RequestBody.create(bodyJson, JSON);
        Request.Builder reqb = new Request.Builder()
                .url(webhookUrl)
                .post(rb)
                .addHeader("Content-Type", "application/json");
        if (signature != null) reqb.addHeader("X-Signature", signature);

        Request req = reqb.build();

        try (Response resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                String eb = resp.body() != null ? resp.body().string() : null;
                throw new IOException("Webhook HTTP " + resp.code() + (eb != null ? ": " + eb : ""));
            }
        }
    }
}
