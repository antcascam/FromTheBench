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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity implements OnClickListener {
	private String jsonResult;
	private Dialog customDialog = null;
	private EditText txtUsuario, txtPassword;
	private Button connectar;
	private ProgressDialog progressDialog = null;
	public static final String PREFS_NAME = "LoginPrefsFile";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_PASSWORD = "password";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		Intent intent = new Intent(this, DownloadService.class);
		startService(intent);

		connectar = (Button) findViewById(R.id.loginButton);
		txtUsuario = (EditText) findViewById(R.id.userText);
		txtPassword = (EditText) findViewById(R.id.passwordText);

		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String username = pref.getString(PREF_USERNAME, null);
		String password = pref.getString(PREF_PASSWORD, null);

		if (username != null) {
			txtUsuario.setText(username, TextView.BufferType.EDITABLE);
		}
		if (password != null) {
			txtPassword.setText(password, TextView.BufferType.EDITABLE);
		}
		connectar.setOnClickListener(this);

		if (username != null && password != null) {
			loginProcess(username, password);
		}
	}

	public void showProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.show();
		progressDialog.setContentView(R.layout.progressdialog);
		// se ppdrá cerrar simplemente pulsando back
		progressDialog.setCancelable(true);
	}

	public void onClick(View v) {
		if (txtUsuario.getText().toString().equals("")
				|| txtPassword.getText().toString().equals("")) {
			showError("Por favor, rellene los campos usuario y contraseña.");
		} else {
			String user = txtUsuario.getText().toString();
			String password = txtPassword.getText().toString();
			getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
					.putString(PREF_USERNAME, user)
					.putString(PREF_PASSWORD, password).commit();
			loginProcess(user, password);
		}
	}

	private void loginProcess(String user, String password) {
		showProgressDialog();
		JSONReaderTask jsonReader = new JSONReaderTask();
		String url = "http://ftbsports.com/android/api/login.php?user=" + user
				+ "&password=" + password;
		// passes values for the urls string array
		jsonReader.execute(new String[] { url });
	}

	public void showError(String errorMessage) {
		customDialog = new Dialog(this);
		customDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.setCancelable(false);
		customDialog.setContentView(R.layout.error);

		TextView contenido = (TextView) customDialog
				.findViewById(R.id.errorMessage);
		contenido.setText(errorMessage);

		((Button) customDialog.findViewById(R.id.errorMessage))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						customDialog.dismiss();
					}
				});
		customDialog.show();
		progressDialog.cancel();
	}

	public void readResponse() {
		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);
			int value = jsonResponse.getInt("status");
			if (value == 0) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), Dashboard.class);
				progressDialog.cancel();
				startActivity(intent);
			} else {
				if (value == -1) {
					showError(jsonResponse.getString("message"));
				} else {
					showError("Error desconocido, inténtelo de nuevo.");
				}
			}
		} catch (JSONException e) {
			Log.e("log_tag", "Error leyendo InputStream" + e.toString());
			showError("Ha ocurrido un error en la respuesta del servidor, inténtelo de nuevo.");
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
				showError("Error leyendo los datos, inténtelo de nuevo.");
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String result) {
			readResponse();
		}
	}
}