/**
 * 2012 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.sckftr.android.securephoto.utils.net;


import android.content.Context;

import com.sckftr.android.securephoto.utils.Executor;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class gives the user an API to easily call a webservice and return the received response.
 *
 * @author Foxykeep
 */
public final class NetworkConnection {

    private static final String LOG_TAG = NetworkConnection.class.getSimpleName();

    private final Context mContext;
    private final String mUrl;
    private Network.Method mMethod = Network.Method.GET;
    private ArrayList<BasicNameValuePair> mParameterList = null;
    private HashMap<String, String> mHeaderMap = null;
    private boolean mIsGzipEnabled = true;
    private String mUserAgent = null;
    private String mPostText = null;
    private UsernamePasswordCredentials mCredentials = null;
    private boolean mIsSslValidationEnabled = true;

    /**
     * Create a {@link NetworkConnection}.
     * <p>
     * The Method to use is {@link com.sta.android.rest.net.Network.Method#GET} by default.
     *
     * @param context The context used by the {@link NetworkConnection}. Used to instance the
     *            User-Agent.
     * @param url The URL to call.
     */
    NetworkConnection(Context context, String url) {
        if (url == null) {
            //Log.e(LOG_TAG, "NetworkConnection.NetworkConnection - request URL cannot be null.");
            throw new NullPointerException("Request URL has not been set.");
        }
        mContext = context;
        mUrl = url;
    }

    /**
     * Set the method to use. Default is {@link com.sta.android.rest.net.Network.Method#GET}.
     * <p>
     * If set to another value than {@link com.sta.android.rest.net.Network.Method#POST}, the POSTDATA text will be reset as it can
     * only be used with a POST request.
     *
     * @param method The method to use.
     * @return The networkConnection.
     */
    public NetworkConnection setMethod(Network.Method method) {
        mMethod = method;
        if (method != Network.Method.POST) {
            mPostText = null;
        }
        return this;
    }

    /**
     * Set the parameters to add to the request. This is meant to be a "key" => "value" Map.
     * <p>
     * The POSTDATA text will be reset as they cannot be used at the same time.
     *
     * @see #setPostText(String)
     * @see #setParameters(java.util.ArrayList)
     * @param parameterMap The parameters to add to the request.
     * @return The networkConnection.
     */
    public NetworkConnection setParameters(HashMap<String, String> parameterMap) {
        ArrayList<BasicNameValuePair> parameterList = new ArrayList<BasicNameValuePair>();
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
          parameterList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return setParameters(parameterList);
    }


    /**
     * Set the parameters to add to the request. This is meant to be a "key" => "value" Map.
     * <p>
     * The POSTDATA text will be reset as they cannot be used at the same time.
     * <p>
     * This method allows you to have multiple values for a single key in contrary to the HashMap
     * version of the method ({@link #setParameters(java.util.HashMap)})
     *
     * @see #setPostText(String)
     * @see #setParameters(java.util.HashMap)
     * @param parameterList The parameters to add to the request.
     * @return The networkConnection.
     */
    public NetworkConnection setParameters(ArrayList<BasicNameValuePair> parameterList) {
      mParameterList = parameterList;
      mPostText = null;
      return this;
    }

    /**
     * Set the headers to add to the request.
     *
     * @param headerMap The headers to add to the request.
     * @return The networkConnection.
     */
    public NetworkConnection setHeaderList(HashMap<String, String> headerMap) {
        mHeaderMap = headerMap;
        return this;
    }

    /**
     * Set whether the request will use gzip compression if available on the server. Default is
     * true.
     *
     * @param isGzipEnabled Whether the request will user gzip compression if available on the
     *            server.
     * @return The networkConnection.
     */
    public NetworkConnection setGzipEnabled(boolean isGzipEnabled) {
        mIsGzipEnabled = isGzipEnabled;
        return this;
    }

    /**
     * Set the user agent to set in the request. Otherwise a default Android one will be used.
     *
     * @param userAgent The user agent.
     * @return The networkConnection.
     */
    public NetworkConnection setUserAgent(String userAgent) {
        mUserAgent = userAgent;
        return this;
    }

    /**
     * Set the POSTDATA text that will be added in the request. Also automatically set the
     * {@link com.sta.android.rest.net.Network.Method} to {@link com.sta.android.rest.net.Network.Method#POST} to be able to use it.
     * <p>
     * The parameters will be reset as they cannot be used at the same time.
     *
     * @param postText The POSTDATA text that will be added in the request.
     * @return The networkConnection.
     * @see #setParameters(java.util.HashMap)
     * @see #setPostText(String)
     */
    public NetworkConnection setPostText(String postText) {
        return setPostText(postText, Network.Method.POST);
    }

    /**
     * Set the POSTDATA text that will be added in the request and also set the {@link com.sta.android.rest.net.Network.Method}
     * to use. The Method can only be {@link com.sta.android.rest.net.Network.Method#POST} or {@link com.sta.android.rest.net.Network.Method#PUT}.
     * <p>
     * The parameters will be reset as they cannot be used at the same time.
     *
     * @param postText The POSTDATA text that will be added in the request.
     * @param method The method to use.
     * @return The networkConnection.
     * @see #setParameters(java.util.HashMap)
     * @see #setPostText(String)
     */
    public NetworkConnection setPostText(String postText, Network.Method method) {
        if (method != Network.Method.POST && method != Network.Method.PUT) {
            throw new IllegalArgumentException("Method must be POST or PUT");
        }
        mPostText = postText;
        mMethod = method;
        mParameterList = null;
        return this;
    }

    /**
     * Set the credentials to use for authentication.
     *
     * @param credentials The credentials to use for authentication.
     * @return The networkConnection.
     */
    public NetworkConnection setCredentials(UsernamePasswordCredentials credentials) {
        mCredentials = credentials;
        return this;
    }

    /**
     * Set whether the SSL certificates validation are enabled. Default is true.
     *
     * @param enabled Whether the SSL certificates validation are enabled.
     * @return The networkConnection.
     */
    public NetworkConnection setSslValidationEnabled(boolean enabled) {
        mIsSslValidationEnabled = enabled;        
        return this;
    }

    /**
     * Execute the webservice call .
     *
     * @return The result of the webservice call.
     */
    public <Out> Out execute(Executor<BufferedReader, Out> handler) throws NetworkConnectionException {
        return Network.execute(mContext, mUrl, mMethod, handler, mParameterList,
                mHeaderMap, mIsGzipEnabled, mUserAgent, mPostText, mCredentials,
                mIsSslValidationEnabled);
    }

}
