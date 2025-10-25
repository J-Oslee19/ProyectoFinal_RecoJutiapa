package com.compufire.recomendacionesdeproductosyservicios.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Helper para generar timestamps ISO-8601 con offset.
 * Implementado en Java para APIs <26 usando SimpleDateFormat.
 */
public class Iso8601Utils {

    public static String toIsoOffsetNowPlusHours(int hours) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY, hours);
        return toIsoOffsetFromMillis(c.getTimeInMillis());
    }

    public static String toIsoOffsetFromMillis(long millis) {
        Date d = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(d);
    }

    public static String toIsoOffset(int year, int monthZeroBased, int day, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthZeroBased);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return toIsoOffsetFromMillis(c.getTimeInMillis());
    }
}

