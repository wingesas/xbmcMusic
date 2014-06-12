package de.wingesas.android.xbmcMusic.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import de.wingesas.android.xbmcMusic.MainApplication;

public abstract class JsonMethod<T> {

	public static final String ARTIST_ID = "artistid";
	public static final String ALBUM_ID = "albumid";
	public static final String SONG_ID = "songid";
	public static final String GENRE_ID = "genreid";
	public static final String FILE = "file";
	public static final String DIRECTORY = "directory";
	public static final String PATH = "path";

	public static final String KEY_ID_LABEL = "KEY_ID_LABEL";
	public static final String KEY_ID = "KEY_ID";

	private static final String TAG = JsonMethod.class.getSimpleName();

	protected abstract String getMethod();

	protected abstract JSONObject getParams() throws Exception;

	public abstract T handleResult(String result) throws JSONException;

	public final T execute() throws Exception {

		HttpURLConnection connection = null;
		T result = null;
		URL url;

		try {
			String address = MainApplication.getHostAddress();
			String port = MainApplication.getHostPort();
			url = new URL(String.format("http://%s:%s/jsonrpc", address, port));

			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(30000);
			connection.setRequestProperty("Authorization", MainApplication.getAuthorization());
			connection.setRequestProperty("Content-Type", "application/json");			

			JSONObject jsonInput = new JSONObject();
			jsonInput.put("jsonrpc", "2.0");
			jsonInput.put("method", getMethod());
			jsonInput.put("id", 1);

			JSONObject params = getParams();

			if (params != null)
				jsonInput.put("params", params);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
			writer.write(jsonInput.toString());
			writer.flush();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			reader.close();

			result = handleResult(builder.toString());

			/*JSONObject jsonObject = new JSONObject(builder.toString());
			if (jsonObject.has("result")) {
				Object o = jsonObject.get("result");
				if (o instanceof JSONObject) {
					JSONObject jsonResult = jsonObject.getJSONObject("result");
					if (jsonResult.has("limits")) {
						int total = jsonResult.getJSONObject("limits").getInt("total");
						Log.d(getClass().getSimpleName(), "total " + String.valueOf(total));
					} else
						Log.d(getClass().getSimpleName(), jsonResult.toString());
				} else
					Log.d(getClass().getSimpleName(), jsonObject.toString());
			} else
				Log.d(getClass().getSimpleName(), jsonObject.toString());*/

		} catch (MalformedURLException e) { // new URL
			Log.e(TAG, "execute()", e);
			throw e;
		} catch (IOException e) { // url.openConnection()
			Log.e(TAG, "execute()", e);
			int responseCode = -1;
			try {
				responseCode = ((HttpURLConnection) connection).getResponseCode();
			} catch (IOException e1) {
			}
			if (connection != null && responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
				throw new HttpException(Integer.toString(HttpURLConnection.HTTP_UNAUTHORIZED));
			} else {
				throw e;
			}
		} catch (JSONException e) { // handleJsonResult
			Log.e(TAG, "execute()", e);
			throw e;
		} catch (NullPointerException e) {
			Log.e(TAG, "execute()", e);
			throw new IOException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

		return result;
	}
}
