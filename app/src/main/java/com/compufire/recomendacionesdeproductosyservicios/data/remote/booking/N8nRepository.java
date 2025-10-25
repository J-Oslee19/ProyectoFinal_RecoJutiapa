package com.compufire.recomendacionesdeproductosyservicios.data.remote.booking;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Response;

public class N8nRepository {
    private static final String TAG = "N8nRepository";

    private final N8nApi api;
    private final String secret;
    private final String baseUrl;
    private final Gson gson = new Gson();

    public N8nRepository() {
        // Acceso seguro a BuildConfig via reflection para evitar errores si no se ha generado todavía
        String s = "";
        String b = "";
        try {
            Class<?> bc = Class.forName("com.compufire.recomendacionesdeproductosyservicios.BuildConfig");
            try {
                Object val = bc.getField("SHARED_SECRET").get(null);
                s = val != null ? val.toString() : "";
            } catch (Exception ignored) {
            }
            try {
                Object val2 = bc.getField("N8N_BASE_URL").get(null);
                b = val2 != null ? val2.toString() : "";
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }
        this.secret = s;
        this.baseUrl = (b != null && !b.isEmpty()) ? b : "https://"; // fallback mínimo

        HttpLoggingInterceptor log = new HttpLoggingInterceptor(m -> Log.d("OkHttp", m));
        log.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(log)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        api = retrofit.create(N8nApi.class);
    }

    /**
     * Envía la petición SÍNCRONA. Ejecutar fuera del hilo principal.
     */
    public BookingResponse send(BookingRequest req) throws IOException {
        String payload = gson.toJson(req);
        String signature = HmacUtil.hmacSha256(secret, payload);

        Log.d(TAG, "payload=" + payload);
        Log.d(TAG, "signature=" + signature);

        Response<BookingResponse> resp = api.createBooking(req, signature).execute();

        if (!resp.isSuccessful()) {
            String errBody = null;
            okhttp3.ResponseBody eb = resp.errorBody();
            if (eb != null) {
                try (okhttp3.ResponseBody closeable = eb) {
                    errBody = closeable.string();
                } catch (Exception ignored) {
                }
            }
            throw new IOException("HTTP " + resp.code() + (errBody != null ? (": " + errBody) : ""));
        }

        BookingResponse body = resp.body();
        if (body == null) {
            throw new IOException("Empty response body from n8n");
        }

        return body;
    }
}
