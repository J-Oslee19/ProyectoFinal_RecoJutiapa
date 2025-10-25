package com.compufire.recomendacionesdeproductosyservicios.data.local.booking;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BookingHistoryDao {

    @Insert
    long insert(BookingHistory bh);

    @Query("SELECT * FROM booking_history ORDER BY createdAt DESC")
    List<BookingHistory> getAll();
}

