package com.compufire.recomendacionesdeproductosyservicios.data.local.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Favorite;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item; // ← IMPORTANTE

import java.util.List;

@Dao
public interface FavoriteDao {

    // Insertar un favorito (si ya existe el mismo itemId lo reemplaza)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Favorite favorite);

    // Eliminar un favorito pasando el objeto
    @Delete
    void delete(Favorite favorite);

    // Eliminar un favorito directamente por itemId
    @Query("DELETE FROM Favorite WHERE itemId = :itemId")
    void deleteByItemId(int itemId);

    // Obtener todos los favoritos en tiempo real (ordenados por fecha descendente)
    @Query("SELECT * FROM Favorite ORDER BY fecha DESC")
    LiveData<List<Favorite>> getAllLive();

    // Obtener todos los favoritos como lista normal (sin LiveData)
    @Query("SELECT * FROM Favorite ORDER BY fecha DESC")
    List<Favorite> getAll();

    // Verificar si un item está marcado como favorito
    @Query("SELECT COUNT(*) FROM Favorite WHERE itemId = :itemId")
    int isFavorite(int itemId);

    // Devolver los Items marcados como favoritos (más recientes primero)
    @Query("SELECT Item.* FROM Item INNER JOIN Favorite ON Favorite.itemId = Item.id ORDER BY Favorite.fecha DESC")
    LiveData<List<Item>> getAllFavoriteItemsLive();
}



