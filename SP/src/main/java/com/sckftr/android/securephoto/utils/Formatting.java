package com.sckftr.android.securephoto.utils;

import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;

import com.sckftr.android.securephoto.AppConst;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * @author Aliaksandr_Litskevic
 * @created 1/27/14.
 */
public class Formatting implements AppConst {

    public static String distance(Float value, String unit) {
        return value == null ? EMPTY : Strings.getNotEmpty(unit) + " " + Math.round(value * 10) / 10f;
    }

    public static Spanned fromHtml(String s) {

        return s==null?new SpannedString(EMPTY):Html.fromHtml(s);
    }

    public static String datePeriod(Object date, Object date1, boolean withYears, boolean withNights) {

        String s = EMPTY;

        DateTime arrival = DateTimeUtil.narrowDateTime(date);
        DateTime departure = DateTimeUtil.narrowDateTime(date1);

        if (arrival == null || departure == null) {
            return s;
        }

        if (withYears) {
            s = String.format("%02d%n %s %d - %02d%n %s %d",
                    arrival.getDayOfMonth(), DateTimeUtil.shortMonthName(arrival), arrival.year().get(),
                    departure.getDayOfMonth(), DateTimeUtil.shortMonthName(departure), departure.year().get()
            );
        } else {
            s = String.format("%02d%n %s - %02d%n %s",
                    arrival.getDayOfMonth(), DateTimeUtil.shortMonthName(arrival),
                    departure.getDayOfMonth(), DateTimeUtil.shortMonthName(departure)
            );
        }

        return s;

    }

    public static String dateFull(long dateInMillis) {

        DateTimeFormatter format = DateTimeFormat.forPattern("dd MMMM yyyy hh:mm a");

        return format.withLocale(Locale.getDefault()).print(new DateTime(dateInMillis));
    }

    public static String rating(float rating) {
        String r=EMPTY;
        for(float i=0f; i<rating;i++){
            r+="\u2605";
        }
        return r;
    }
}
