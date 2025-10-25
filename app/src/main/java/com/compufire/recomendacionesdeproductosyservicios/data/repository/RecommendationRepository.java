package com.compufire.recomendacionesdeproductosyservicios.data.repository;

import com.compufire.recomendacionesdeproductosyservicios.data.local.db.ItemDao;
import com.compufire.recomendacionesdeproductosyservicios.data.local.db.VendorDao;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item;
import java.util.List;

public class RecommendationRepository {
    private final ItemDao itemDao;
    private final VendorDao vendorDao;

    public RecommendationRepository(ItemDao itemDao, VendorDao vendorDao) {
        this.itemDao = itemDao;
        this.vendorDao = vendorDao;
    }

    public List<Item> getByCategoria(String categoria) {
        return itemDao.getByCategoria(categoria);
    }

    public List<Item> getByPrecio(double min, double max) {
        return itemDao.getByPrecio(min, max);
    }

    public List<Item> searchByNombre(String texto) {
        return itemDao.searchByNombre(texto);
    }

    public List<Item> getAll() {
        return itemDao.getByCategoria("%"); // Devuelve todos si la consulta está implementada así
    }
}

