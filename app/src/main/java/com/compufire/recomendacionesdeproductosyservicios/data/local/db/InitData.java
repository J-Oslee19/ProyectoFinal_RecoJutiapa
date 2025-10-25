package com.compufire.recomendacionesdeproductosyservicios.data.local.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Vendor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InitData extends RoomDatabase.Callback {

    private final Context appContext;
    private static final ExecutorService IO = Executors.newSingleThreadExecutor();

    public InitData(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        super.onCreate(db);
        seedAsync();
    }

    @Override
    public void onOpen(@NonNull SupportSQLiteDatabase db) {
        super.onOpen(db);
        seedAsync();
    }

    private void seedAsync() {
        IO.execute(() -> {
            AppDatabase database = AppDatabase.getInstance(appContext);
            ItemDao itemDao = database.itemDao();
            VendorDao vendorDao = database.vendorDao();
            try {
                if (safeCount(vendorDao) == 0) vendorDao.insertAll(seedVendors());
                if (safeCount(itemDao) == 0) itemDao.insertAll(seedItems());
            } catch (Exception ignored) {}
        });
    }

    private int safeCount(ItemDao dao) { try { return dao.count(); } catch (Exception e) { return 0; } }
    private int safeCount(VendorDao dao) { try { return dao.count(); } catch (Exception e) { return 0; } }

    private List<Vendor> seedVendors() {
        List<Vendor> vs = new ArrayList<>();
        vs.add(vendor(1, "Tacos El Buen Sabor", "Comida"));
        vs.add(vendor(2, "TecnoJalpatagua", "Tecnología"));
        vs.add(vendor(3, "Servicios Don Chepe", "Servicios"));
        vs.add(vendor(4, "Cafetería Las Delicias", "Comida"));
        vs.add(vendor(5, "ElectroJutiapa", "Tecnología"));
        return vs;
    }

    private List<Item> seedItems() {
        List<Item> list = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {
            Item it = new Item();
            it.id = i;
            it.nombre = "Negocio " + i;
            it.categoria = (i % 3 == 0) ? "Tecnología" : (i % 2 == 0 ? "Servicios" : "Comida");
            it.precio = 10 + i;
            try { it.lat = 14.29; } catch (Throwable ignored) {}
            try { it.lng = -89.90; } catch (Throwable ignored) {}
            try { it.rating = 3.5 + (i % 5) * 0.2; } catch (Throwable ignored) {}
            try { it.imagenUrl = "https://picsum.photos/seed/" + i + "/400/300"; } catch (Throwable ignored) {}
            it.vendorId = (i % 5) + 1;
            list.add(it);
        }
        return list;
    }

    /** ✅ Usa reflexión para asignar campos que sí existan (nombre, tipo, categoria, etc.) */
    private static Vendor vendor(int id, String nombre, String categoria) {
        Vendor v = new Vendor();
        setField(v, "id", id);
        setField(v, "vendorId", id);

        // nombre
        if (!setField(v, "nombre", nombre))
            setField(v, "name", nombre);

        // categoria (si no existe, se ignora sin error)
        if (!setField(v, "categoria", categoria))
            if (!setField(v, "category", categoria))
                if (!setField(v, "tipo", categoria))
                    setField(v, "rubro", categoria);

        return v;
    }

    /** Helper genérico: asigna el campo si existe */
    private static boolean setField(Object target, String field, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}



