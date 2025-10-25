package com.compufire.recomendacionesdeproductosyservicios.data.local.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Vendor;

import java.util.List;

@Dao
public interface VendorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Vendor> vendors);

    @Query("SELECT * FROM Vendor WHERE id = :id")
    Vendor getById(int id);

    // Conteo para seed (evitar duplicados)
    @Query("SELECT COUNT(*) FROM Vendor")
    int count();
}


