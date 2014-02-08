package com.fromthebench.prodcon;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.nineoldandroids.animation.ObjectAnimator;

public class Dashboard extends Activity {

	/** Called when the activity is first created. */
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

			}
		});

		Button bProdCon = (Button) findViewById(R.id.dashBoardProdConBtn);

		bAbout.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

			}
		});

		Button bCache = (Button) findViewById(R.id.dashBoardCacheBtn);

		bAbout.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

			}
		});

		Button bLogOut = (Button) findViewById(R.id.dashBoardLogoutBtn);

		bAbout.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

			}
		});
	}

}
