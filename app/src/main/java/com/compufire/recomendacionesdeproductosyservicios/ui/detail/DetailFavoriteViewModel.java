package com.compufire.recomendacionesdeproductosyservicios.ui.detail;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.compufire.recomendacionesdeproductosyservicios.data.local.db.AppDatabase;
import com.compufire.recomendacionesdeproductosyservicios.data.local.db.FavoriteDao;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Favorite;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Vendor;
import com.compufire.recomendacionesdeproductosyservicios.data.remote.webhook.FavoritePayload;
import com.compufire.recomendacionesdeproductosyservicios.data.remote.webhook.WebhookClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel para manejar favoritos desde DetailFragment.
 */
public class DetailFavoriteViewModel extends AndroidViewModel {

    private static final String TAG = "DetailFavVM";

    private final FavoriteDao dao;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    // Estado observable: ¿este item ya es favorito?
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);

    public DetailFavoriteViewModel(@NonNull Application application) {
        super(application);
        dao = AppDatabase.getInstance(application).favoriteDao();
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    /** Verifica si el item ya está en favoritos */
    public void checkIfFavorite(int itemId) {
        ioExecutor.execute(() -> {
            boolean exists = dao.isFavorite(itemId) > 0;
            isFavorite.postValue(exists);
        });
    }

    /** Agrega un favorito nuevo (sin nombre) */
    public void addFavorite(int itemId) {
        addFavorite(itemId, null);
    }

    /** Agrega un favorito nuevo y notifica webhook con itemName (opcional) */
    public void addFavorite(int itemId, String itemName) {
        ioExecutor.execute(() -> {
            Favorite fav = new Favorite();
            fav.itemId = itemId;
            fav.fecha = System.currentTimeMillis();
            dao.insert(fav);
            isFavorite.postValue(true);

            // Obtener item y vendor desde la BD
            Item item = null;
            Vendor vendor = null;
            try {
                item = AppDatabase.getInstance(getApplication()).itemDao().getByIdSync(itemId);
                if (item != null) {
                    vendor = AppDatabase.getInstance(getApplication()).vendorDao().getById(item.vendorId);
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to fetch item/vendor from DB", e);
            }

            // Construir y enviar payload
            try {
                FavoritePayload payload = FavoritePayload.fromModels(
                    FavoritePayload.Event.AGREGADO_A_FAVORITOS,
                    item,
                    vendor
                );

                WebhookClient client = new WebhookClient();
                client.sendFavoriteEvent(payload);
            } catch (Exception e) {
                Log.w(TAG, "Failed to send favorite added webhook", e);
            }
        });
    }

    /** Elimina un favorito existente (sin nombre) */
    public void removeFavorite(int itemId) {
        removeFavorite(itemId, null);
    }

    /** Elimina favorito y notifica webhook */
    public void removeFavorite(int itemId, String itemName) {
        ioExecutor.execute(() -> {
            dao.deleteByItemId(itemId);
            isFavorite.postValue(false);

            // Obtener item para enviar datos descriptivos
            Item item = null;
            try {
                item = AppDatabase.getInstance(getApplication()).itemDao().getByIdSync(itemId);
            } catch (Exception e) {
                Log.w(TAG, "Failed to fetch item from DB", e);
            }

            try {
                FavoritePayload payload = FavoritePayload.fromModels(
                    FavoritePayload.Event.ELIMINADO_DE_FAVORITOS,
                    item,
                    null
                );

                WebhookClient client = new WebhookClient();
                client.sendFavoriteEvent(payload);
            } catch (Exception e) {
                Log.w(TAG, "Failed to send favorite removed webhook", e);
            }
        });
    }
}
