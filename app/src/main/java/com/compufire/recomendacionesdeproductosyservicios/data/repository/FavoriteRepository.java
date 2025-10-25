package com.compufire.recomendacionesdeproductosyservicios.data.repository;

import com.compufire.recomendacionesdeproductosyservicios.data.local.db.FavoriteDao;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Favorite;
import java.util.List;

public class FavoriteRepository {
    private final FavoriteDao favoriteDao;

    public FavoriteRepository(FavoriteDao favoriteDao) {
        this.favoriteDao = favoriteDao;
    }

    public void add(Favorite f) {
        favoriteDao.insert(f);
    }

    public void removeByItemId(int id) {
        favoriteDao.deleteByItemId(id);
    }

    public List<Favorite> getAll() {
        return favoriteDao.getAll();
    }
}

