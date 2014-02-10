package com.fromthebench.prodcon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Producer extends Thread {
	private BlockingQueue<Integer> generatedNumbers;
	private static final int MAX_NUM = 2000;
	private static final int MIN_NUM = 750;
	private String jsonResult;
	private Handler handler;
	private boolean run;
	private Context context;

	public Producer(String name) {
		super(name);
	}

	public Producer(String name, BlockingQueue<Integer> generatedNumbers,
			Context context) {
		super(name);
		this.generatedNumbers = generatedNumbers;
		this.context = context;
		run = true;
	}

	public void run() {
		Bundle bundle = null;
		Message msg = null;
		int newNumber = 0;
		while (run) {
			newNumber = getNewNumber();
			bundle = new Bundle();
			msg = new Message();
			bundle.putInt("newNumber", newNumber);
			msg.setData(bundle);
			handler.sendMessage(msg);
			try {
				generatedNumbers.put(newNumber);
				sleep((long) Math.floor(Math.random() * (MIN_NUM - MAX_NUM + 1)
						+ MAX_NUM));
			} catch (InterruptedException e) {
				Log.e("log_tag",
						"Exception in generation thread: " + e.toString());
			}
		}
	}

	private int getNewNumber() {
		checkConnection();
		String url = "http://ftbsports.com/android/api/get_rand.php";
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		try {
			HttpResponse response = httpclient.execute(httpget);
			jsonResult = inputStreamToString(response.getEntity().getContent())
					.toString();
		} catch (ClientProtocolException e) {
			Log.e("log_tag", "Error en HttpResponse: " + e.toString());
		} catch (IOException e) {
			Log.e("log_tag", "Error en BufferedReader: " + e.toString());
		}
		return readResponse();
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
			Log.e("log_tag", "Error leyendo InputStream: " + e.toString());
		}
		return answer;
	}

	public int readResponse() {
		int numberFromServer = 0;
		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);
			if (jsonResponse.has("num")) {
				numberFromServer = jsonResponse.getInt("num");
			} else {
				Log.e("log_tag", "Error, algo va mal en la respuesta");
			}
		} catch (JSONException e) {
			Log.e("log_tag", "Error leyendo InputStream: " + e.toString());
		}
		return numberFromServer;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void finish() {
		run = false;
	}

	private void checkConnection() {
		ConnectivityManager conMngr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwkInfo = conMngr.getActiveNetworkInfo();
		Bundle bundle = new Bundle();
		Message msg = new Message();
		if (!(nwkInfo != null && nwkInfo.isConnected() && nwkInfo.isAvailable())) {
			bundle.putBoolean("internetConnection", false);
			msg.setData(bundle);
			handler.sendMessage(msg);
		} else {
			bundle.putBoolean("internetConnection", true);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}
}
