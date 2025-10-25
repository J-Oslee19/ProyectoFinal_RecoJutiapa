package com.compufire.recomendacionesdeproductosyservicios.data.local.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Rating;
import java.util.List;

@Dao
public interface RatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Rating rating);

    @Query("SELECT * FROM Rating WHERE itemId = :itemId")
    List<Rating> getByItemId(int itemId);
}

