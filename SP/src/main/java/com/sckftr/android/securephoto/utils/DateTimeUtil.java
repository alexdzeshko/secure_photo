package com.sckftr.android.securephoto.utils;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtil {

    private static final String Log_TAG = "DateTimeUtil";
	public static final int TODAY = -1;
	public static final int YESTERDAY = 0;
	public static final int BEFORE_YESTERDAY = 1;

	private static final long MS_IN_ONE_DAY = 24 * 60 * 60 * 1000;

	private static final ThreadLocal<SimpleDateFormat> ISO8601_GMT_DATE_FORMATTER = new ThreadLocal<SimpleDateFormat>();

	static {
		SimpleDateFormat iso8601DateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'Z'");
		iso8601DateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		ISO8601_GMT_DATE_FORMATTER.set(iso8601DateFormat);
	}

    public static final String MM_DD_YYYY = "MM/dd/yyyy";

    private static final long MILLIS_OF_DAY = 24 * 60 * 60 * 1000;

    /**
	 * Calculate the number of days between dateA and dateB
	 *
	 * @return the number of days between 0:00:00 on dateA and 0:00:00 on dateB,
	 * or -1 if an error occurred.
	 */

    public static int getNumDaysBetween(Object o, Object o1) {

        int dateTime = totalDays(o);
        int dateTime1 = totalDays(o1);
        return dateTime!=0 && dateTime1!=0 ? (dateTime1 - dateTime) : -1;
    }

	public static int daysInMonth(int day, int month, int year) {

		return new GregorianCalendar(year, month, day).getActualMaximum(Calendar.DAY_OF_MONTH);

	}

	public static long currentSeconds() {

		Calendar c = Calendar.getInstance();
		long mseconds = c.getTimeInMillis();
		return mseconds / 1000;
	}

    public static int daysInMonth(Calendar c) {

        return daysInMonth(c.get(Calendar.DATE), c.get(Calendar.MONTH), c.get(Calendar.YEAR));
    }

    public static String formatSystem(Date date) {

        SimpleDateFormat mDateFormat = new SimpleDateFormat(MM_DD_YYYY);

        return date==null ? Strings.EMPTY : mDateFormat.format(date);
    }

    public static DateTime parse(String s) {

        if (Strings.isEmpty(s)){

            return null;
        }

        try {

            SimpleDateFormat mDateFormat = new SimpleDateFormat(MM_DD_YYYY, Locale.getDefault());
            mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            return new DateTime(mDateFormat.parse(s));
        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    public static boolean isInThePastOrToday(DateTime dateTime) {
        return getNumDaysBetween(DateTime.now(), dateTime)<=0;
    }

    public static boolean isToday(Object dateTime) {
        return totalDays(dateTime) == todayTotalDay();
    }

    public static boolean isTonight(Object arrival, Object departure) {

        int todayTotalDay = todayTotalDay();
        return totalDays(arrival) == todayTotalDay && totalDays(departure) == todayTotalDay +1;
    }

    public static int todayTotalDay() {
        return totalDays(Calendar.getInstance());
    }

    public static int totalDays(Object o) {
        DateTime dateTime = narrowDateTime(o);
        if (dateTime==null) {
            return 0;
        }
        long millis = dateTime.getMillis();
        return (int) ((millis - millis % MILLIS_OF_DAY) / MILLIS_OF_DAY);
    }
    /**
     * Useful routine to add given a number of units to specifed part of given time.
     *
     * @param field part of date
     * @param time given date
     * @param val value to add
     *
     * @return instance of <code>Calendar</code> containing resulting date
     */
    public static Calendar add(int field, long time, int val) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.add(field, val);
        return c;
    }

    public static DateTime narrowDateTime(Object o) {

        if (o instanceof DateTime) {
            return (DateTime)o;
        }

        if (o instanceof Calendar) {
            return new DateTime(((Calendar) o).getTimeInMillis());
        }

        if (o instanceof String) {
            return parse((String) o);
        }

        if (o instanceof Date) {
            return new DateTime(((Date)o).getTime());
        }

        return null;
    }

    public static String shortMonthName(DateTime dateTime) {

        return dateTime.monthOfYear().getAsShortText(Locale.getDefault());
    }

    public static String formatSystem(DateTime dateTime) {
        return formatSystem(dateTime.toDate());

    }

}
