package by.deniotokiari.core.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import by.deniotokiari.core.context.ContextHolder;
import by.deniotokiari.core.exception.BadRequestException;
import by.deniotokiari.core.helpers.CoreHelper;
import by.deniotokiari.core.helpers.CoreHelper.IAppServiceKey;
import by.deniotokiari.core.utils.AppUtils;
import by.deniotokiari.core.utils.IOUtils;
import android.content.Context;
import android.net.ConnectivityManager;

public class HttpManager implements IAppServiceKey {

	public static final String SYSTEM_SERVICE_KEY = "framework:httpmanager";

	private static final String UTF_8 = "UTF_8";

	private HttpClient mClient;

	private static HttpManager instance;

	private static final int SO_TIMEOUT = 26000;

	private static final String ILLEGAL_REQUEST_TYPE = "Illegal request type. Use HttpManager's RequestType.";

	private static final int IO_BUFFER_SIZE = 8 * 1024;

	private ConnectivityManager mConnectivityManager;

	public static enum RequestType {

		GET("GET"), POST("POST"), DELETE("DELETE");

		private RequestType(String type) {

		}

    }

	private HttpManager() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, UTF_8);
		params.setBooleanParameter("http.protocol.expect-continue", false);
		HttpConnectionParams.setConnectionTimeout(params, SO_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);

		// REGISTERS SCHEMES FOR BOTH HTTP AND HTTPS
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		final SSLSocketFactory sslSocketFactory = SSLSocketFactory
				.getSocketFactory();
		sslSocketFactory
				.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		registry.register(new Scheme("https", sslSocketFactory, 443));
		ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(
				params, registry);
		mClient = new DefaultHttpClient(manager, params);
		mConnectivityManager = (ConnectivityManager) ContextHolder
				.getInstance().getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	@Override
	public String getKey() {
		return SYSTEM_SERVICE_KEY;
	}

	public static HttpManager get(Context context) {
		if (instance == null) {
			CoreHelper.get(context).registerAppService(new HttpManager());
			instance = (HttpManager) AppUtils.get(context, SYSTEM_SERVICE_KEY);
		}
		return instance;
	}

	public String postRequest(String url, ArrayList<BasicNameValuePair> params)
			throws ClientProtocolException, IOException, JSONException,
			BadRequestException {
		HttpPost post = new HttpPost(url);
		UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		post.setEntity(ent);
		return getStringResponse(post);
	}

	public String postRequst(String url) throws ClientProtocolException,
			IOException, BadRequestException {
		return getStringResponse(new HttpPost(url));
	}

	public String deleteRequst(String url) throws ClientProtocolException,
			IOException, BadRequestException {
		return getStringResponse(new HttpDelete(url));
	}

	public String loadAsString(HttpRequestBase request)
			throws ClientProtocolException, IOException, BadRequestException {
		return getStringResponse(request);
	}

	public String loadAsString(String url, RequestType requestType)
			throws ClientProtocolException, IOException, JSONException,
			BadRequestException {
		switch (requestType) {
		case GET:
			return getStringResponse(new HttpGet(url));
		case POST:
			return getStringResponse(new HttpPost(url));
		case DELETE:
			return getStringResponse(new HttpDelete(url));
		default:
			throw new IllegalArgumentException(ILLEGAL_REQUEST_TYPE);
		}

	}

	public JSONArray loadAsJsonArray(String url, RequestType requestType)
			throws ClientProtocolException, JSONException, IOException,
			BadRequestException {
		return new JSONArray(loadAsString(url, requestType));
	}

	public JSONObject loadAsJSONObject(String url, RequestType requestType)
			throws ClientProtocolException, JSONException, IOException,
			BadRequestException {
		return new JSONObject(loadAsString(url, requestType));
	}

	private String getStringResponse(HttpRequestBase request)
			throws ClientProtocolException, IOException, BadRequestException {
		final InputStream is = loadInputStream(request);
		BufferedReader rd = null;
		try {
			rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			final String jsonText = readAll(rd);
			return jsonText;
		} finally {
			IOUtils.closeStream(is);
			IOUtils.closeStream(rd);
		}
	}

	public InputStream loadInputStream(HttpRequestBase request)
			throws ParseException, IOException, BadRequestException {
		HttpResponse response = mClient.execute(request);
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			String entityValue = null;
			entityValue = EntityUtils.toString(response.getEntity());
			throw new BadRequestException(response.getStatusLine()
					.getReasonPhrase()
					+ " "
					+ entityValue
					+ " "
					+ response.getStatusLine().getStatusCode());

		}
		final HttpEntity entity = response.getEntity();
		final BufferedHttpEntity httpEntity = new BufferedHttpEntity(entity);
		try {
			InputStream inputStream = httpEntity.getContent();
			return inputStream;
		} finally {
			httpEntity.consumeContent();
			entity.consumeContent();
		}
	}

	public void downloadImage(String urlString, OutputStream destination)
			throws IOException {
		final BufferedOutputStream out = new BufferedOutputStream(destination,
				IO_BUFFER_SIZE);
		final HttpGet get = new HttpGet(urlString);

		HttpEntity entity = null;
		try {
			final HttpResponse response = mClient.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				entity = response.getEntity();
				entity.writeTo(out);
				out.flush();
			}
		} finally {
			if (entity != null) {
				entity.consumeContent();
			}
		}
	}

	private static String readAll(final Reader rd) throws IOException {
		final StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public boolean isAvalibleInetConnection() {
		return mConnectivityManager.getActiveNetworkInfo() != null;
	}

}
