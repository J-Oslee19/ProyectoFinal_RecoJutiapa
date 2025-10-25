package com.compufire.recomendacionesdeproductosyservicios.ui.favorites;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.compufire.recomendacionesdeproductosyservicios.data.local.db.AppDatabase;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item;

import java.util.List;

/** ViewModel que expone la lista de Items marcados como favoritos. */
public class FavoritesViewModel extends AndroidViewModel {

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Item>> getFavorites() {
        return AppDatabase.getInstance(getApplication())
                .favoriteDao()
                .getAllFavoriteItemsLive(); // <- requiere este método en FavoriteDao
    }
}

