// app/src/main/java/com/compufire/recomendacionesdeproductosyservicios/ui/home/HomeRoomViewModel.java
package com.compufire.recomendacionesdeproductosyservicios.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.compufire.recomendacionesdeproductosyservicios.data.local.db.AppDatabase;
import com.compufire.recomendacionesdeproductosyservicios.data.local.db.ItemDao;
import com.compufire.recomendacionesdeproductosyservicios.data.local.db.UserPrefsDao;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.UserPrefs;
import com.compufire.recomendacionesdeproductosyservicios.util.RankingUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeRoomViewModel extends AndroidViewModel {

    private final LiveData<List<Item>> roomItems;      // items crudos desde Room
    private final LiveData<UserPrefs> prefsLive;       // preferencias (id=1)
    private final MediatorLiveData<List<Item>> uiItems = new MediatorLiveData<>();

    // copias en memoria para recomputar
    private List<Item> lastItems = null;
    private UserPrefs lastPrefs = null;

    public HomeRoomViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        ItemDao itemDao = db.itemDao();
        UserPrefsDao prefsDao = db.userPrefsDao();

        roomItems = itemDao.getAllLive();
        prefsLive = prefsDao.getLive();

        uiItems.addSource(roomItems, items -> {
            lastItems = items;
            recompute();
        });
        uiItems.addSource(prefsLive, prefs -> {
            lastPrefs = prefs;
            recompute();
        });
    }

    /** Lista final (filtrada/ordenada) para la UI */
    public LiveData<List<Item>> getItems() {
        return uiItems;
    }

    /** Recalcula cuando cambian items o prefs */
    private void recompute() {
        if (lastItems == null) {
            uiItems.setValue(new ArrayList<>());
            return;
        }
        // Si aún no hay prefs guardadas, mostramos tal cual
        if (lastPrefs == null) {
            uiItems.setValue(new ArrayList<>(lastItems));
            return;
        }

        List<Item> out = new ArrayList<>();
        for (Item it : lastItems) {
            // Filtro por presupuesto
            boolean dentroPresupuesto =
                    it.precio >= lastPrefs.presupuestoMin &&
                            it.precio <= lastPrefs.presupuestoMax;

            // Filtro por categorías seleccionadas (si hay)
            boolean categoriaOk = true;
            if (lastPrefs.categorias != null && !lastPrefs.categorias.isEmpty()) {
                categoriaOk = false;
                for (String pref : lastPrefs.categorias) {
                    if (pref != null && pref.equalsIgnoreCase(it.categoria)) {
                        categoriaOk = true;
                        break;
                    }
                }
            }

            if (dentroPresupuesto && categoriaOk) {
                out.add(it);
            }
        }

        // Si el filtro dejó todo vacío, usa la lista original (para no “dejar sin nada”)
        if (out.isEmpty()) out.addAll(lastItems);

        // Ordenar por score descendente (usamos distancia 0 como placeholder)
        final double distanciaKm = 0.0;
        Collections.sort(out, new Comparator<Item>() {
            @Override
            public int compare(Item a, Item b) {
                double sa = RankingUtils.score(a, lastPrefs, distanciaKm);
                double sb = RankingUtils.score(b, lastPrefs, distanciaKm);
                return Double.compare(sb, sa);
            }
        });

        uiItems.setValue(out);
    }
}


