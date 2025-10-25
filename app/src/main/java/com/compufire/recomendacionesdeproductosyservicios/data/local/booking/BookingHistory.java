package com.compufire.recomendacionesdeproductosyservicios.data.local.booking;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "booking_history")
public class BookingHistory {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String requestId;
    public String businessId;
    public String businessName;
    public String type; // RESERVA | CONSULTA
    public String datetime; // ISO-8601
    public String status; // CONFIRMADA | PENDIENTE | RECHAZADA
    public String code;
    public long createdAt;

    public BookingHistory() {}

    public BookingHistory(String requestId, String businessId, String businessName, String type, String datetime, String status, String code, long createdAt) {
        this.requestId = requestId;
        this.businessId = businessId;
        this.businessName = businessName;
        this.type = type;
        this.datetime = datetime;
        this.status = status;
        this.code = code;
        this.createdAt = createdAt;
    }
}

