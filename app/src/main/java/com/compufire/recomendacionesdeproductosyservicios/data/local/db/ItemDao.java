package com.compufire.recomendacionesdeproductosyservicios.data.local.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item;

import java.util.List;

@Dao
public interface ItemDao {

    // Lista completa para UI
    @Query("SELECT * FROM Item ORDER BY id ASC")
    LiveData<List<Item>> getAllLive();

    // Para DetailFragment
    @Query("SELECT * FROM Item WHERE id = :id")
    LiveData<Item> getById(int id);

    // Método sincrónico para obtener Item en background thread
    @Query("SELECT * FROM Item WHERE id = :id")
    Item getByIdSync(int id);

    // Conteo para seed
    @Query("SELECT COUNT(*) FROM Item")
    int count();

    // Búsquedas/filtros existentes
    @Query("SELECT * FROM Item WHERE categoria = :categoria")
    List<Item> getByCategoria(String categoria);

    @Query("SELECT * FROM Item WHERE precio BETWEEN :min AND :max")
    List<Item> getByPrecio(double min, double max);

    @Query("SELECT * FROM Item WHERE nombre LIKE '%' || :texto || '%'")
    List<Item> searchByNombre(String texto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Item> items);
}
