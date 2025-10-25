package com.compufire.recomendacionesdeproductosyservicios.data.repository;

import com.compufire.recomendacionesdeproductosyservicios.data.local.db.RatingDao;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Rating;
import java.util.List;

public class RatingRepository {
    private final RatingDao ratingDao;

    public RatingRepository(RatingDao ratingDao) {
        this.ratingDao = ratingDao;
    }

    public void add(Rating r) {
        ratingDao.insert(r);
    }

    public List<Rating> getByItemId(int itemId) {
        return ratingDao.getByItemId(itemId);
    }
}

