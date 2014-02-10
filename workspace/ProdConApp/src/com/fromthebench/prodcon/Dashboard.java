package com.fromthebench.prodcon;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.nineoldandroids.animation.ObjectAnimator;

public class Dashboard extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);

		View logoView = findViewById(R.id.dashboardLayoutLogo);
		ObjectAnimator logoAnimation = ObjectAnimator.ofFloat(logoView,
				"translationY", -700, 0).setDuration(600);

		View leftLights = findViewById(R.id.leftLight);
		ObjectAnimator leftAnimation = ObjectAnimator.ofFloat(leftLights,
				"translationX", 0, 400).setDuration(500);
		leftAnimation.setStartDelay(500);

		View rightLights = findViewById(R.id.rightLight);
		ObjectAnimator rightAnimation = ObjectAnimator.ofFloat(rightLights,
				"translationX", 0, -400).setDuration(500);
		rightAnimation.setStartDelay(500);

		logoAnimation.start();
		leftAnimation.start();
		rightAnimation.start();

		Button bAbout = (Button) findViewById(R.id.dashBoardAboutBtn);

		bAbout.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				aboutMe();
			}
		});

		Button bProdCon = (Button) findViewById(R.id.dashBoardProdConBtn);

		bProdCon.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), ProCon.class);
				startActivity(intent);
			}
		});

		Button bCache = (Button) findViewById(R.id.dashBoardCacheBtn);

		bCache.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				flushCache();
			}
		});

		Button bLogOut = (Button) findViewById(R.id.dashBoardLogoutBtn);

		bLogOut.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				logOut();
			}
		});
		Notifications.cancelProgressDialog();
	}

	public void flushCache() {
		Notifications.showProgressDialog(this,
				"Eliminando imágenes guardadas...");
		SharedPreferences pref = getSharedPreferences(
				DownloadService.PREFS_IMAGES_NAME, MODE_PRIVATE);
		if (pref.getAll().isEmpty()) {
			Notifications.showMessage(this, "No hay más imágenes que borrar.");
		} else {
			ContextWrapper cw = new ContextWrapper(getBaseContext());
			File dirImages = cw.getDir("fromthebench_images",
					Context.MODE_PRIVATE);
			if (dirImages.exists())
				dirImages.delete();
			pref.edit().clear().commit();
			Notifications
					.showMessage(this,
							"Se han eliminado correctamente todos los datos de la caché.");
		}
	}

	public void logOut() {
		SharedPreferences pref = getSharedPreferences(Login.PREFS_LOGIN_NAME,
				MODE_PRIVATE);
		pref.edit().clear().commit();
		finish();
	}

	public void aboutMe() {
		Notifications
				.showAboutMeMessage(this, "Antonio J Castaño - Developer.");
	}
}
