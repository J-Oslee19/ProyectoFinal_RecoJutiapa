package com.compufire.recomendacionesdeproductosyservicios.data.remote.booking;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface N8nApi {
    @POST("/webhook/recojutiapa/booking")
    Call<BookingResponse> createBooking(@Body BookingRequest body, @Header("X-Signature") String signature);
}

