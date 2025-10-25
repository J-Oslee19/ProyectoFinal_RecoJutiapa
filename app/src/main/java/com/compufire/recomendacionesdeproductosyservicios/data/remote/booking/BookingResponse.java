package com.compufire.recomendacionesdeproductosyservicios.data.remote.booking;

public class BookingResponse {
    public String status; // "CONFIRMADA" | "PENDIENTE" | "RECHAZADA"
    public String code;
    public String message;
    public String ticketUrl; // optional

    public BookingResponse() {}

    public BookingResponse(String status, String code, String message, String ticketUrl) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.ticketUrl = ticketUrl;
    }
}

