package com.compufire.recomendacionesdeproductosyservicios.data.local.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Vendor {
    @PrimaryKey
    public int id;
    public String nombre;
    public String telefono;
    public double lat;
    public double lng;
}

