// app/src/main/java/com/compufire/recomendacionesdeproductosyservicios/data/local/db/UserPrefsDao.java
package com.compufire.recomendacionesdeproductosyservicios.data.local.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.compufire.recomendacionesdeproductosyservicios.data.local.model.UserPrefs;

@Dao
public interface UserPrefsDao {

    // 🔹 Devuelve siempre la fila con id = 1 como LiveData (para observar en la UI)
    @Query("SELECT * FROM UserPrefs WHERE id = 1 LIMIT 1")
    LiveData<UserPrefs> getLive();

    // 🔹 Devuelve la fila con id = 1 directamente (para background threads)
    @Query("SELECT * FROM UserPrefs WHERE id = 1 LIMIT 1")
    UserPrefs get();

    // 🔹 Inserta o reemplaza la fila única (upsert)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(UserPrefs prefs);
}


