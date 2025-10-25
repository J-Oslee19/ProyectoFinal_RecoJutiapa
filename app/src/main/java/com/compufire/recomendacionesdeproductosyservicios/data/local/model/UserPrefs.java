package com.compufire.recomendacionesdeproductosyservicios.data.local.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.compufire.recomendacionesdeproductosyservicios.data.local.db.Converters;

import java.util.ArrayList;
import java.util.List;

@Entity
@TypeConverters(Converters.class)
public class UserPrefs {

    // Siempre será el registro único de preferencias
    @PrimaryKey
    public int id = 1;

    // Categorías preferidas del usuario (ejemplo: "Comida", "Papelería", "Transporte")
    public List<String> categorias = new ArrayList<>();

    // Rango de presupuesto preferido
    public double presupuestoMin = 0;
    public double presupuestoMax = 0;

    // Si desea usar ubicación real para recomendaciones
    public boolean usaUbicacion = false;
}

