package com.compufire.recomendacionesdeproductosyservicios.data.local.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Rating {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int itemId;
    public double score;
    public String comentario;
    public long fecha;
}

