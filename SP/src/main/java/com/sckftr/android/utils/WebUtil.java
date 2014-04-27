package com.sckftr.android.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class supporting low-level web interactions.
 * 
 */
public final class WebUtil {

	/** The Constant TWO_HYPHEN. */
	private static final String TWO_HYPHEN = "--";

	/** The Constant LINE_END. */
	private static final String LINE_END = "\r\n";

	/** The Constant MULTIPART_BOUNDARY. */
	private static final String MULTIPART_BOUNDARY = "3i2ndDfv2rTHiLitAleXNdArYfORhtTPEefj3q2f";

	public static final String MIME_TEXT_HTML = "text/html";

	/**
	 * Creates the http params.
	 * 
	 * @param params
	 *            the params
	 * @return the http params
	 */
	public static HttpParams createHttpParams(Bundle params) {

		HttpParams httpParams = new BasicHttpParams();
		for (String key : params.keySet()) {
			String value = params.getString(key);
			if (value == null)
				continue;
			httpParams.setParameter(key, value);
		}
		return httpParams;
	}

	/**
	 * To name value pairs list.
	 * 
	 * @param parameters
	 *            the parameters
	 * @return the list
	 */
	public static List<NameValuePair> toNameValuePairsList(Bundle parameters) {

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (parameters != null) {
			for (String key : parameters.keySet()) {
				String value = parameters.getString(key);
				if (value == null)
					continue;
				nvps.add(new BasicNameValuePair(key, value));
			}
		}
		return nvps;
	}

	/**
	 * Encode url params.
	 * 
	 * @param parameters
	 *            the parameters
	 * @return the string
	 */
	public static String encodeUrlParams(Bundle parameters) {

		String format = URLEncodedUtils.format(toNameValuePairsList(parameters), HTTP.UTF_8);
		// Log.d("WebUtil","encodeUrlParams: "+format);
		return format;
	}

	/**
	 * Decode url.
	 * 
	 * @param s
	 *            the s
	 * @return the bundle
	 */
	public static Bundle decodeUrl(String s) {

		Bundle params = new Bundle();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				if (v.length > 1) {
					params.putString(v[0], Uri.decode(v[1]));
				}
			}
		}
		return params;
	}

	/**
	 * Creates the http entity.
	 * 
	 * @param json
	 *            the json
	 * @return the string entity
	 */
	public final static StringEntity createHttpEntity(final JSONObject json) {

		StringEntity se = null;
		try {
			se = new StringEntity(json.toString());
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return se;
	}

	/**
	 * Creates the http entity.
	 * 
	 * @param parameters
	 *            the parameters
	 * @return the string entity
	 */
	public final static StringEntity createHttpEntity(Bundle parameters) {

		try {
			return new UrlEncodedFormEntity(toNameValuePairsList(parameters), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates the multipart http entity.
	 * 
	 * @param params
	 *            the params
	 * @param binaries
	 *            the binaries
	 * @return the http entity
	 */
	public static HttpEntity createMultipartHttpEntity(Bundle params, Bundle binaries) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		OutputStream os = new BufferedOutputStream(byteArrayOutputStream);
		try {
			if (params != null) {
				for (String key : params.keySet()) {
					os.write((TWO_HYPHEN + MULTIPART_BOUNDARY).getBytes());
					os.write((LINE_END + "Content-Disposition: form-data; name=\"" + key + "\"")
							.getBytes());
					os.write((LINE_END + LINE_END).getBytes());
					os.write(params.getString(key).getBytes());
					os.write((LINE_END).getBytes());
				}
			}
			if (binaries != null) {
				for (String key : binaries.keySet()) {
					os.write((TWO_HYPHEN + MULTIPART_BOUNDARY).getBytes());
					os.write((LINE_END + "Content-Disposition: form-data; name=\"" + key + "\"")
							.getBytes());
					os.write(("; filename=\"" + key + "\"").getBytes());
					os.write((LINE_END + "Content-Type: content/unknown").getBytes());
					os.write((LINE_END + LINE_END).getBytes());
					os.write(binaries.getByteArray(key));
					os.write((LINE_END).getBytes());
				}
			}
			// final part
			os.write((TWO_HYPHEN + MULTIPART_BOUNDARY + TWO_HYPHEN).getBytes());

			os.flush();
			ByteArrayEntity se = new ByteArrayEntity(byteArrayOutputStream.toByteArray());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "multipart/form-data;boundary="
					+ MULTIPART_BOUNDARY));

			return se;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Clear cookies.
	 * 
	 * @param context
	 *            the context
	 */
	public static void clearCookies(Context context) {

		// Edge case: an illegal state exception is thrown if an instance of
		// CookieSyncManager has not be created. CookieSyncManager is normally
		// created by a WebKit view, but this might happen if you start the
		// app, restore saved state, and click logout before running a UI
		// dialog in a WebView -- in which case the app crashes
		CookieSyncManager.createInstance(context);

		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}


    public static void loadData(WebView webView, CharSequence body) {

        webView.loadData(envelopeHtml(body), WebUtil.MIME_TEXT_HTML, HTTP.UTF_8);

    }

    /**
     * Envelopes given markup with html body and some standard meta tags.
     *
     * @param html
     *            the html
     * @return the string enveloped
     */
    public static String envelopeHtml(CharSequence html) {

        /**
         * , initial-scale=1.0, user-scalable=no, maximum-scale=1.0,
         * target-densitydpi=device-dpi
         */
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
                + "<head>"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
                + "<meta name=\"viewport\" content=\"width=device-width\"/>"
                + "</head>"
                + "<body margin=\"0\" padding=\"0\" style=\"margin: 0px; padding: 0px; font: 0.9em sans-serif;\">"
                + html + "</body>" + "</html>";
    }
}
