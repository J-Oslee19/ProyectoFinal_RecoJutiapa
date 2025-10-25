package com.compufire.recomendacionesdeproductosyservicios.data.local.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Item {
    @PrimaryKey
    public int id;

    public String nombre;
    public String categoria;
    public double precio;
    public double lat;
    public double lng;
    public double rating;
    public String imagenUrl;

    // relación con Vendor
    public int vendorId;
}

