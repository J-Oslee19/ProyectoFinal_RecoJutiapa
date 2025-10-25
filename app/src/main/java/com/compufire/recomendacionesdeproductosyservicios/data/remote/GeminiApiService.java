package com.compufire.recomendacionesdeproductosyservicios.data.remote;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GeminiApiService {

    // v1: generateContent
    @POST("v1/models/{model}:generateContent")
    Call<GeminiResponse> generateContent(
            @Path("model") String model,
            @Query("key") String apiKey,
            @Body GeminiRequest body
    );

    // v1: listModels -> nos devuelve los nombres reales disponibles para tu key
    @GET("v1/models")
    Call<ModelsListResponse> listModels(@Query("key") String apiKey);
}

