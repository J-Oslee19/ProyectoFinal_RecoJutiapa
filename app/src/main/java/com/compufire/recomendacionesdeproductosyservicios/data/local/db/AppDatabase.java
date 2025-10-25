package com.compufire.recomendacionesdeproductosyservicios.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.compufire.recomendacionesdeproductosyservicios.data.local.model.*;

@Database(
        entities = {
                UserPrefs.class,
                Item.class,
                Vendor.class,
                Favorite.class,
                Rating.class,
                com.compufire.recomendacionesdeproductosyservicios.data.local.booking.BookingHistory.class
        },
        version = 2,
        exportSchema = false // evita el warning de schemas
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract ItemDao itemDao();
    public abstract VendorDao vendorDao();
    public abstract FavoriteDao favoriteDao();
    public abstract UserPrefsDao userPrefsDao();
    public abstract RatingDao ratingDao();
    public abstract com.compufire.recomendacionesdeproductosyservicios.data.local.booking.BookingHistoryDao bookingHistoryDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "recojutiapa.db"
                            )
                            .addCallback(new InitData(context)) // siembra Vendors + 24 Items al crear la BD
                            .fallbackToDestructiveMigration()    // simplifica desarrollo
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

