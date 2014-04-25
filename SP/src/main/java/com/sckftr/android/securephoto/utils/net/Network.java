/**
 * 2011 Foxykeep (http://.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.sckftr.android.securephoto.utils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.utils.Base64;
import com.sckftr.android.securephoto.utils.Executor;

import org.apache.http.HttpStatus;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Implementation of the network connection.
 *
 * @author Foxykeep
 */
public final class Network implements AppConst {

    private static final String TAG = Network.class.getSimpleName();

    private static final String ACCEPT_CHARSET_HEADER = "Accept-Charset";
    private static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String LOCATION_HEADER = "Location";

    private static final String UTF8_CHARSET = "UTF-8";

    // Default connection and socket timeout of 60 seconds. Tweak to taste.
    private static final int OPERATION_TIMEOUT = 60 * 1000;

    private Network() {
        // No public constructor
    }

    /**
     * Call the webservice using the given parameters to construct the request and return the
     * result.
     *
     *
     * @param context The context to use for this operation. Used to generate the user agent if
     *            needed.
     * @param urlValue The webservice URL.
     * @param method The request method to use.
     * @param handler
     *@param parameterList The parameters to add to the request.
     * @param headerMap The headers to add to the request.
     * @param isGzipEnabled Whether the request will use gzip compression if available on the
*            server.
     * @param userAgent The user agent to set in the request. If null, a default Android one will be
*            created.
     * @param postText The POSTDATA text to add in the request.
     * @param credentials The credentials to use for authentication.
     * @param isSslValidationEnabled Whether the request will validate the SSL certificates.        @return The result of the webservice call.
     */
    static <Out> Out execute(Context context, String urlValue, Method method,
                                    Executor<BufferedReader, Out> handler, ArrayList<BasicNameValuePair> parameterList, HashMap<String, String> headerMap,
                                    boolean isGzipEnabled, String userAgent, String postText,
                                    UsernamePasswordCredentials credentials, boolean isSslValidationEnabled) throws
            NetworkConnectionException {
        HttpURLConnection connection = null;
        try {
            // Prepare the request information
            if (userAgent == null) {
                userAgent = UserAgentUtils.get(context);
            }
            if (headerMap == null) {
                headerMap = new HashMap<String, String>();
            }
            headerMap.put(HTTP.USER_AGENT, userAgent);
            if (isGzipEnabled) {
                headerMap.put(ACCEPT_ENCODING_HEADER, "gzip");
            }
            headerMap.put(ACCEPT_CHARSET_HEADER, UTF8_CHARSET);
            if (credentials != null) {
                headerMap.put(AUTHORIZATION_HEADER, createAuthenticationHeader(credentials));
            }

            StringBuilder paramBuilder = new StringBuilder();
            if (parameterList != null && !parameterList.isEmpty()) {
                for (int i = 0, size = parameterList.size(); i < size; i++) {
                    BasicNameValuePair parameter = parameterList.get(i);
                    String name = parameter.getName();
                    String value = parameter.getValue();
                    if (TextUtils.isEmpty(name)) {
                        // Empty parameter name. Check the next one.
                        continue;
                    }
                    if (value == null) {
                        value = "";
                    }
                    paramBuilder.append(URLEncoder.encode(name, UTF8_CHARSET));
                    paramBuilder.append("=");
                    paramBuilder.append(URLEncoder.encode(value, UTF8_CHARSET));
                    paramBuilder.append("&");
                }
            }

            // Log the request
            //if (Log.canLog(Log.DEBUG)) {
                Log.d(TAG, method.toString()+": "+ urlValue);

                if (parameterList != null && !parameterList.isEmpty()) {
                    //Log.d(TAG, "Parameters:");
                    for (int i = 0, size = parameterList.size(); i < size; i++) {
                        BasicNameValuePair parameter = parameterList.get(i);
                        String message = "- \"" + parameter.getName() + "\" = \""
                                + parameter.getValue() + "\"";
                        Log.d(TAG, message);
                    }

                    //Log.d(TAG, "Parameters String: \"" + paramBuilder.toString() + "\"");
                }

                if (postText != null) {
                    Log.d(TAG, "Post data: " + postText);
                }

                if (headerMap != null && !headerMap.isEmpty()) {
                    //Log.d(TAG, "Headers:");
                    for (Entry<String, String> header : headerMap.entrySet()) {
                        //Log.d(TAG, "- " + header.getKey() + " = " + header.getValue());
                    }
                }
            //}

            // Create the connection object
            URL url = null;
            String outputText = null;
            switch (method) {
                case GET:
                case DELETE:
                    String fullUrlValue = urlValue;
                    if (paramBuilder.length() > 0) {
                        fullUrlValue += "?" + paramBuilder.toString();
                    }
                    url = new URL(fullUrlValue);
                    connection = (HttpURLConnection) url.openConnection();
                    break;
                case PUT:
                case POST:
                    url = new URL(urlValue);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);

                    if (paramBuilder.length() > 0) {
                        outputText = paramBuilder.toString();
                        headerMap.put(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
                        headerMap.put(HTTP.CONTENT_LEN,
                                String.valueOf(outputText.getBytes().length));
                    } else if (postText != null) {
                        outputText = postText;
                    }
                    break;
            }

            // Set the request method
            connection.setRequestMethod(method.toString());

            // If it's an HTTPS request and the SSL Validation is disabled
            if (url.getProtocol().equals("https")
                    && !isSslValidationEnabled) {
                HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                httpsConnection.setSSLSocketFactory(getAllHostsValidSocketFactory());
                httpsConnection.setHostnameVerifier(getAllHostsValidVerifier());
            }

            // Add the headers
            if (!headerMap.isEmpty()) {
                for (Entry<String, String> header : headerMap.entrySet()) {
                    connection.addRequestProperty(header.getKey(), header.getValue());
                }
            }

            // Set the connection and read timeout
            connection.setConnectTimeout(OPERATION_TIMEOUT);
            connection.setReadTimeout(OPERATION_TIMEOUT);

            // Set the outputStream content for POST and PUT requests
            if ((method == Method.POST || method == Method.PUT) && outputText != null) {
                OutputStream output = null;
                try {
                    output = connection.getOutputStream();
                    output.write(outputText.getBytes());
                } finally {
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            // Already catching the first IOException so nothing to do here.
                        }
                    }
                }
            }

            String contentEncoding = connection.getHeaderField(HTTP.CONTENT_ENCODING);

            int responseCode = connection.getResponseCode();
            boolean isGzip = contentEncoding != null
                    && contentEncoding.equalsIgnoreCase("gzip");
            Log.d(TAG, "Response code: " + responseCode);

            if (responseCode == HttpStatus.SC_MOVED_PERMANENTLY) {
                String redirectionUrl = connection.getHeaderField(LOCATION_HEADER);
                throw new NetworkConnectionException("New location : " + redirectionUrl,
                        redirectionUrl);
            }

            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                String err =  evaluateStream(errorStream, new StringReaderHandler(), isGzip);
                throw new NetworkConnectionException(err, responseCode);
            }

            return evaluateStream(connection.getInputStream(), handler, isGzip);

        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
            throw new NetworkConnectionException(e);
        } catch (KeyManagementException e) {
            Log.e(TAG, "KeyManagementException", e);
            throw new NetworkConnectionException(e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "NoSuchAlgorithmException", e);
            throw new NetworkConnectionException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String createAuthenticationHeader(UsernamePasswordCredentials credentials) {
        StringBuilder sb = new StringBuilder();
        sb.append(credentials.getUserName()).append(":").append(credentials.getPassword());
        return "Basic " + Base64.encodeToString(sb.toString().getBytes(), Base64.NO_WRAP);
    }

    private static SSLSocketFactory sAllHostsValidSocketFactory;

    private static SSLSocketFactory getAllHostsValidSocketFactory()
            throws NoSuchAlgorithmException, KeyManagementException {
        if (sAllHostsValidSocketFactory == null) {
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            sAllHostsValidSocketFactory = sc.getSocketFactory();
        }

        return sAllHostsValidSocketFactory;
    }

    private static HostnameVerifier sAllHostsValidVerifier;

    private static HostnameVerifier getAllHostsValidVerifier() {
        if (sAllHostsValidVerifier == null) {
            sAllHostsValidVerifier = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
        }

        return sAllHostsValidVerifier;
    }
    /**
     * Check network connected.
     *
     * @throws NetworkConnectionException
     *             the network error exception
     * @param context

     */
    public static boolean checkConnected(Context context) {


        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = connManager.getActiveNetworkInfo();

        if ((netInfo == null) || !netInfo.isConnected()) {
            return false;
            //throw new NetworkConnectionException();
        }
        return true;

        //  if ( !connManager.requestRouteToHost(netInfo.getType(),
        // lookupHost(host)) ) {
        // throw new RemoteException();
        // }
		/*
		 * try { if
		 * (InetAddress.getByName(host).isReachable(HOST_REACHABILITY_TIMEOUT)){
		 * throw new RemoteException(); } } catch (UnknownHostException e) {
		 * throw new RemoteException(); } catch (IOException e) { throw new
		 * RemoteException(); }
		 */

    }

    private static <Out> Out evaluateStream(InputStream is, Executor<BufferedReader, Out> handler, boolean isGzipEnabled)
            throws IOException {

        InputStream cleanedIs = is;
        if (isGzipEnabled) {
            cleanedIs = new GZIPInputStream(is);
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(cleanedIs, UTF8_CHARSET));
            handler.perform(reader);

        } finally {
            if (reader != null) {
                reader.close();
            }

            cleanedIs.close();

            if (isGzipEnabled) {
                is.close();
            }
        }
        return handler.getResult();
    }

    public static NetworkConnection newConnection(Context context, String url) {
        return new NetworkConnection(context,  url);
    }


    public static enum Method {
        GET, POST, PUT, DELETE
    }

    public static class StringReaderHandler implements Executor<BufferedReader, String> {

        private StringBuilder sb = new StringBuilder();

        @Override
        public void perform(BufferedReader input, Object... params) {
            try {
                for (String line; (line = input.readLine()) != null;) {
                    sb.append(line);
                    sb.append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public String getResult() {
            return sb.toString();
        }
    }

    /**
     * Get IP address from first non-localhost interface
     * @return  address or empty string
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i("NET", "***** IP=" + ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("NET", ex.toString());
        }
        return null;
    }
}
