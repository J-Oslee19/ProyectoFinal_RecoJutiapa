package com.compufire.recomendacionesdeproductosyservicios.data.local.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Favorite {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int itemId;   // referencia al Item
    public long fecha;   // timestamp en milisegundos (System.currentTimeMillis())
}

