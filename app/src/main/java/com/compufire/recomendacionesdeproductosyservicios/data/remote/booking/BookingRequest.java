package com.compufire.recomendacionesdeproductosyservicios.data.remote.booking;

public class BookingRequest {
    public String requestId;
    public User user;
    public Business business;
    public String type; // "RESERVA" | "CONSULTA"
    public String datetime; // ISO-8601 with offset
    public String notes;

    public static class User {
        public String id;
        public String name;
        public String email;

        public User() {}

        public User(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }

    public static class Business {
        public String id;
        public String name;

        public Business() {}

        public Business(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public BookingRequest() {}

    public BookingRequest(String requestId, User user, Business business, String type, String datetime, String notes) {
        this.requestId = requestId;
        this.user = user;
        this.business = business;
        this.type = type;
        this.datetime = datetime;
        this.notes = notes;
    }
}

