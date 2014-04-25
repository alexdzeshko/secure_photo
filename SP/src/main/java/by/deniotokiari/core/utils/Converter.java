package by.deniotokiari.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.content.res.Resources;
import android.util.TypedValue;

public class Converter {

	private Converter() {
	}

	public static long dateTimeToUnix(String dateTime) {
		try {
			String format = "yyyy-MM-dd'T'HH:mm:ssZ";
			SimpleDateFormat sdf = new SimpleDateFormat(format,
					Locale.getDefault());
			long unix = sdf.parse(dateTime).getTime();
			unix /= 1000;
			return unix;
		} catch (ParseException ignored) {

		}
		return -1;
	}

	public static String secondsToTime(long lengthInSeconds) {
		int hours = (int) ((lengthInSeconds / (60 * 60)) % 24);
		int minutes = (int) ((lengthInSeconds / 60) % 60);
		int seconds = (int) (lengthInSeconds % 60);

		String result = hours > 0 ? String.valueOf(hours) + ":" : "";
		result += minutes > 0 ? (hours > 0 ? (minutes < 10 ? "0"
				+ String.valueOf(minutes) + ":" : String.valueOf(minutes) + ":")
				: String.valueOf(minutes) + ":")
				: "0:";
		result += seconds < 10 ? "0" + String.valueOf(seconds) : String
				.valueOf(seconds);
		return result;
	}

	public static String unixTimeToDateTimeString(long unixTime) {
		DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
		GregorianCalendar calendar = new GregorianCalendar(
				TimeZone.getDefault());
		calendar.setTimeInMillis(unixTime * 1000);
		return dateFormat.format(calendar.getTime());
	}

	public static String stringToMD5(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	public static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte aByte : bytes) {
			String hex = Integer.toHexString(0xFF & aByte);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	public static float dpToPx(Resources r, int dp) {
		if (r == null) {
			return 0f;
		}
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				r.getDisplayMetrics());
	}

}
