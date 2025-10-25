package com.compufire.recomendacionesdeproductosyservicios.data.repository;

import com.compufire.recomendacionesdeproductosyservicios.data.local.db.UserPrefsDao;
import com.compufire.recomendacionesdeproductosyservicios.data.local.model.UserPrefs;

public class UserPrefsRepository {
    private final UserPrefsDao userPrefsDao;

    public UserPrefsRepository(UserPrefsDao userPrefsDao) {
        this.userPrefsDao = userPrefsDao;
    }

    public UserPrefs get() {
        return userPrefsDao.get();
    }

    public void save(UserPrefs prefs) {
        // usamos upsert porque en el Dao está definido así
        userPrefsDao.upsert(prefs);
    }
}


