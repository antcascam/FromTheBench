package com.fromthebench.prodcon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
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

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class DownloadService extends Activity {
	public static final String PREFS_IMAGES_NAME = "ImagesPrefsFile";
	private static final String PREF_URL = "url";
	private static final String PREF_NAME = "nombre";
	private static Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downloading);
		activity = this;
		Intent intent = new Intent(this, DownloaderService.class);
		this.startService(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent intent = getIntent();
		setResult(RESULT_OK, intent);
	}

	public static class DownloaderService extends IntentService {

		public DownloaderService() {
			super("DownloaderService");
		}

		@Override
		protected void onHandleIntent(Intent intent) {
			try {
				URL url = new URL(
						"http://ftbsports.com/android/api/get_images_cache.php");
				URI uri = new URI(url.getProtocol(), url.getHost(),
						url.getPath(), url.getQuery(), null);

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(uri);

				String dataParameters = checkImagesStored();

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("data", dataParameters));

				httpPost.setEntity(new UrlEncodedFormEntity(params));
				HttpResponse response = httpclient.execute(httpPost);

				String jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();
				readResponse(jsonResult);
			} catch (ClientProtocolException e) {
				Log.e("log_tag",
						"Error ejecutando HttpResponse." + e.toString());
			} catch (UnsupportedEncodingException e) {
				Log.e("log_tag", "Error codificando la url." + e.toString());
			} catch (IOException e) {
				Log.e("log_tag",
						"Error obteniendo el InputStream." + e.toString());
			} catch (URISyntaxException e) {
				Log.e("log_tag", "Error codificando la url." + e.toString());
			}
		}

		private String checkImagesStored() {
			String signalsNames = "";
			SharedPreferences pref = getSharedPreferences(PREFS_IMAGES_NAME,
					MODE_PRIVATE);
			Map<String, ?> shpfMap = pref.getAll();
			int count = 0;

			Iterator<?> entries = shpfMap.entrySet().iterator();
			while (entries.hasNext()) {
				Entry<?, ?> thisEntry = (Entry<?, ?>) entries.next();
				Object key = thisEntry.getKey();
				if (key.toString().contains("nombre")) {
					Object value = thisEntry.getValue();
					signalsNames = signalsNames + "\"" + value + "\", ";
					count++;
				}
			}
			if (signalsNames.length() > 3) {
				signalsNames = signalsNames.substring(0,
						signalsNames.length() - 2);
			}
			String dataParameters = "{\"count\":" + count + ", \"list\":["
					+ signalsNames + "]}";
			return dataParameters;
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
					int size = jsonList.length();
					if (size > 0) {
						for (int i = 0; i < size; i++) {
							JSONObject jsonImage = jsonList.getJSONObject(i);
							String urlName = jsonImage.getString("url");
							String imageName = jsonImage.getString("nombre");
							Log.e("log_tag", "Imagen: " + imageName
									+ " , url: " + urlName);
							downloadImage(imageName, urlName);
							getSharedPreferences(PREFS_IMAGES_NAME,
									MODE_PRIVATE).edit()
									.putString(PREF_URL + i, urlName)
									.putString(PREF_NAME + i, imageName)
									.commit();
						}
					} else {
						activity.finish();
					}
				}
			} catch (JSONException e) {
				Log.e("log_tag", "Error leyendo InputStream en readResponse: "
						+ e.toString());
			} catch (NullPointerException e) {
				Log.e("log_tag", "Error leyendo JSONObject: " + e.toString());
			}
		}

		private void downloadImage(String imageName, String imageHttpAddress) {
			CargaImagenes nuevaTarea = new CargaImagenes();
			nuevaTarea.execute(new String[] { imageName, imageHttpAddress });
		}

		private class CargaImagenes extends AsyncTask<String, Void, Bitmap> {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Bitmap doInBackground(String... params) {
				Bitmap imagen = descargarImagen(params[0], params[1]);
				return imagen;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				activity.finish();
			}
		}

		private Bitmap descargarImagen(String imageName, String imageHttpAddress) {
			URL imageUrl = null;
			Bitmap imagen = null;
			FileOutputStream fos = null;
			try {
				imageUrl = new URL(imageHttpAddress);
				HttpURLConnection conn = (HttpURLConnection) imageUrl
						.openConnection();
				conn.connect();
				imagen = BitmapFactory.decodeStream(conn.getInputStream());

				ContextWrapper cw = new ContextWrapper(getBaseContext());
				File dirImages = cw.getDir("fromthebench_images",
						Context.MODE_PRIVATE);
				File myPath = new File(dirImages, imageName);
				fos = new FileOutputStream(myPath);
				imagen.compress(Bitmap.CompressFormat.PNG, 10, fos);
				fos.flush();

			} catch (FileNotFoundException e) {
				Log.e("log_tag",
						"Error al no encontrar archivo: " + e.toString());
			} catch (IOException e) {
				Log.e("log_tag", "Error descargando imagen: " + e.toString());
			}
			return imagen;
		}
	}

}
