package com.compufire.recomendacionesdeproductosyservicios.ui.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.compufire.recomendacionesdeproductosyservicios.data.local.db.AppDatabase;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item;

public class DetailViewModel extends AndroidViewModel {

    public DetailViewModel(@NonNull Application app) {
        super(app);
    }

    // Expone el Item por id como LiveData desde Room
    public LiveData<Item> load(int itemId) {
        return AppDatabase.getInstance(getApplication())
                .itemDao()
                .getById(itemId);
    }
}
