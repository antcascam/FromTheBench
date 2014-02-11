package com.fromthebench.prodcon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Notifications {
	public static Dialog customDialog = null;
	public static ProgressDialog progressDialog = null;

	public static void showProgressDialog(Context context, String message) {
		progressDialog = new ProgressDialog(context);
		progressDialog.show();
		progressDialog.setContentView(R.layout.progressdialog);
		progressDialog.setCancelable(true);

		TextView contenido = (TextView) progressDialog
				.findViewById(R.id.loadingText);
		contenido.setText(message);
	}

	public static void cancelProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing())
			progressDialog.cancel();
	}

	public static void showMessage(Context context, String errorMessage) {
		customDialog = new Dialog(context);
		customDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.setCancelable(false);
		customDialog.setContentView(R.layout.error);

		TextView contenido = (TextView) customDialog
				.findViewById(R.id.errorMessage);
		contenido.setText(errorMessage);

		((Button) customDialog.findViewById(R.id.errorButton))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						customDialog.dismiss();
						cancelProgressDialog();
					}
				});
		customDialog.show();
		Notifications.cancelProgressDialog();
	}
}
