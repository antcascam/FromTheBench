package com.fromthebench.prodcon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity implements OnClickListener {
	private String jsonResult;
	private EditText txtUsuario, txtPassword;
	private Button connectar;
	private String username;
	private String password;
	private final static int IMGLOADER = 0;
	public static final String PREFS_LOGIN_NAME = "LoginPrefsFile";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_PASSWORD = "password";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		connectar = (Button) findViewById(R.id.loginButton);
		txtUsuario = (EditText) findViewById(R.id.userText);
		txtPassword = (EditText) findViewById(R.id.passwordText);
		connectar.setOnClickListener(this);

		SharedPreferences pref = getSharedPreferences(PREFS_LOGIN_NAME,
				MODE_PRIVATE);
		username = pref.getString(PREF_USERNAME, null);
		password = pref.getString(PREF_PASSWORD, null);

		if (username != null) {
			txtUsuario.setText(username, TextView.BufferType.EDITABLE);
		}
		if (password != null) {
			txtPassword.setText(password, TextView.BufferType.EDITABLE);
		}
		tryToStartConnection();
	}

	private void tryToStartConnection() {
		if (!checkConnection(this)) {
			Notifications.showProgressDialog(this, "Iniciando sesión...");
			((Button) Notifications.customDialog.findViewById(R.id.errorButton))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							Notifications.customDialog.dismiss();
							Notifications.cancelProgressDialog();
							finish();
						}
					});
			Notifications
					.showConnectionError(
							this,
							"Por favor, compruebe su conexión a internet y vuelva a iniciar la aplicación, gracias.");
		} else {
			Intent intent = new Intent(this, DownloadService.class);
			startActivityForResult(intent, IMGLOADER);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (username != null && password != null) {
			loginProcess(username, password);
		}
	}

	public void onClick(View v) {
		Notifications.showProgressDialog(this, "Iniciando sesión...");
		if (txtUsuario.getText().toString().equals("")
				|| txtPassword.getText().toString().equals("")) {
			Notifications.showMessage(this,
					"Por favor, rellene los campos usuario y contraseña.");
		} else {
			String user = txtUsuario.getText().toString();
			String password = txtPassword.getText().toString();
			getSharedPreferences(PREFS_LOGIN_NAME, MODE_PRIVATE).edit()
					.putString(PREF_USERNAME, user)
					.putString(PREF_PASSWORD, password).commit();
			loginProcess(user, password);
		}
	}

	private void loginProcess(String user, String password) {
		JSONReaderTask jsonReader = new JSONReaderTask();
		String url = "http://ftbsports.com/android/api/login.php?user=" + user
				+ "&password=" + password;
		jsonReader.execute(new String[] { url });
	}

	public void readResponse() {
		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);
			int value = jsonResponse.getInt("status");
			if (value == 0) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), Dashboard.class);
				startActivity(intent);
			} else {
				if (value == -1) {
					Notifications.showMessage(this,
							jsonResponse.getString("message"));
				} else {
					Notifications.showMessage(this,
							"Error en el servidor, inténtelo de nuevo.");
				}
			}
		} catch (JSONException e) {
			Log.e("log_tag", "Error leyendo InputStream" + e.toString());
			Notifications
					.showMessage(this,
							"Ha ocurrido un error en la respuesta del servidor, inténtelo de nuevo.");
		}
	}

	private class JSONReaderTask extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(params[0]);
			try {
				HttpResponse response = httpclient.execute(httpget);
				jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
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
				Log.e("log_tag", "Error leyendo InputStream" + e.toString());
				Notifications.showMessage(getApplicationContext(),
						"Error leyendo los datos, inténtelo de nuevo.");
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String result) {
			readResponse();
		}
	}

	public static boolean checkConnection(Context context) {
		ConnectivityManager conMngr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwkInfo = conMngr.getActiveNetworkInfo();
		return nwkInfo != null && nwkInfo.isConnected()
				&& nwkInfo.isAvailable();
	}
}