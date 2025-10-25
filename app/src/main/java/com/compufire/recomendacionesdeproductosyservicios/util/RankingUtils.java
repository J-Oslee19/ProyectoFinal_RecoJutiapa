package com.compufire.recomendacionesdeproductosyservicios.util;

import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.UserPrefs;
import java.util.List;

public class RankingUtils {
    public static double precioScore(double precio, double min, double max) {
        if (precio >= min && precio <= max) return 1.0;
        double rango = (max - min) * 0.2;
        if (precio < min) return Math.max(0, 1.0 - (min - precio) / rango);
        if (precio > max) return Math.max(0, 1.0 - (precio - max) / rango);
        return 0;
    }

    public static double distanciaScore(double km) {
        if (km < 0) return 0.5;
        if (km <= 2) return 1.0;
        if (km <= 5) return 0.6;
        return 0.3;
    }

    public static double ratingScore(double rating) {
        return Math.max(0, Math.min(1.0, rating / 5.0));
    }

    public static double preferenciaScore(String categoria, List<String> preferencias) {
        if (preferencias == null) return 0;
        for (String pref : preferencias) {
            if (pref.equalsIgnoreCase(categoria)) return 1.0;
        }
        return 0;
    }

    public static double score(Item item, UserPrefs prefs, double distanciaKm) {
        double pScore = precioScore(item.precio, prefs.presupuestoMin, prefs.presupuestoMax);
        double dScore = distanciaScore(distanciaKm);
        double rScore = ratingScore(item.rating);
        double prefScore = preferenciaScore(item.categoria, prefs.categorias);
        return 0.35 * pScore + 0.30 * dScore + 0.20 * rScore + 0.15 * prefScore;
    }

    public static String explicarCorto(Item item, UserPrefs prefs, double distanciaKm) {
        StringBuilder sb = new StringBuilder();
        sb.append("Te recomendamos ").append(item.nombre);
        if (distanciaKm > 0) {
            sb.append(" porque está a ").append((int) Math.round(distanciaKm)).append(" min");
        }
        sb.append(", cuesta Q").append((int) item.precio);
        sb.append(" (dentro de tu presupuesto Q").append((int) prefs.presupuestoMin).append("–").append((int) prefs.presupuestoMax).append(")");
        sb.append(" y tiene ").append(item.rating).append("⭐.");
        return sb.toString();
    }
}

