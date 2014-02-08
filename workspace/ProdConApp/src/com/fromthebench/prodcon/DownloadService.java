package com.fromthebench.prodcon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class DownloadService extends IntentService {
	public static final String PREFS_NAME = "ImagesPrefsFile";
	private static final String PREF_URL = "url";
	private static final String PREF_NAME = "nombre";

	public DownloadService() {
		super("DownloadService");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		try {
			URL url = new URL(
					"http://ftbsports.com/android/api/get_images_cache.php");
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(),
					url.getQuery(), null);

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(uri);

			String signalsNames = "";
			SharedPreferences pref = getSharedPreferences(PREFS_NAME,
					MODE_PRIVATE);
			Map<String, ?> shpfMap = pref.getAll();
			int count = shpfMap.values().size();

			Iterator<?> entries = shpfMap.entrySet().iterator();
			while (entries.hasNext()) {
				Entry<?, ?> thisEntry = (Entry<?, ?>) entries.next();
				Object key = thisEntry.getKey();
				if (key.toString().contains("nombre")) {
					Object value = thisEntry.getValue();
					signalsNames = signalsNames + "\"" + value + "\", ";
				}
			}
			signalsNames = signalsNames.substring(0, signalsNames.length() - 2);
			String dataParameters = "{\"count\":" + count + ", \"list\":["
					+ signalsNames + "]}";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("data", dataParameters));

			httpPost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse response = httpclient.execute(httpPost);

			String jsonResult = inputStreamToString(
					response.getEntity().getContent()).toString();
			readResponse(jsonResult);
		} catch (ClientProtocolException e) {
			Log.e("log_tag", "Error ejecutando HttpResponse." + e.toString());
		} catch (UnsupportedEncodingException e) {
			Log.e("log_tag", "Error codificando la url." + e.toString());
		} catch (IOException e) {
			Log.e("log_tag", "Error obteniendo el InputStream." + e.toString());
		} catch (URISyntaxException e) {
			Log.e("log_tag", "Error codificando la url." + e.toString());
		}
	}

	private StringBuilder inputStreamToString(InputStream is) {
		String rLine = "";
		StringBuilder answer = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try {
			while ((rLine = rd.readLine()) != null) {
				answer.append(rLine);
			}
		} catch (IOException e) {
			Log.e("log_tag",
					"Error leyendo InputStream en inputStreamToString: "
							+ e.toString());
		}
		return answer;
	}

	public void readResponse(String jsonResult) {
		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);
			if (jsonResponse.has("list")) {
				JSONArray jsonList = jsonResponse.getJSONArray("list");
				for (int i = 0; i < jsonList.length(); i++) {
					JSONObject jsonImage = jsonList.getJSONObject(i);
					String urlName = jsonImage.getString("url");
					String imageNAme = jsonImage.getString("nombre");
					getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
							.putString(PREF_URL + i, urlName)
							.putString(PREF_NAME + i, imageNAme).commit();
				}
				// downloadImage(imageNAme,urlName);
			}
		} catch (JSONException e) {
			Log.e("log_tag",
					"Error leyendo InputStream en readResponse: "
							+ e.toString());
		} catch (NullPointerException e) {
			Log.e("log_tag", "Error leyendo JSONObject: " + e.toString());
		}
	}

	// TODO: descarga de imágenes
//	private void downloadImage(String imageNAme, String urlName) {
//
//	}

}
