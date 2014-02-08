package zrox.zrox.login;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginzroxActivity extends Activity implements OnClickListener {
	EditText txtUsuario, txtPassword;
	Button connectar;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        connectar = (Button)findViewById(R.id.btIngresar);
        txtUsuario = (EditText) findViewById(R.id.etUsuario); 
        txtPassword = (EditText) findViewById(R.id.etContrasena);
        
        connectar.setOnClickListener(this);
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btIngresar:
			if (txtUsuario.getText().toString().equals("")
					|| txtPassword.getText().toString().equals("")) {
				Toast toast = Toast.makeText(this, "Verifica tus datos",
						Toast.LENGTH_LONG);
				toast.show();
			} else {
				ArrayList parametros = new ArrayList();
				parametros.add("Usuario");
				parametros.add(txtUsuario.getText().toString());
				parametros.add("Password");
				parametros.add(txtPassword.getText().toString());
				// parametros.add("basedat");
				// parametros.add("dsc");

				// Llamada al servidor web php
				try {
					Post post = new Post();
					String server_ip = "http://192.168.1.74//";

					JSONArray datos = post.getServerData(parametros, server_ip
							+ "Proyecto_socialITAAndroid/hacerLogin.php");
					if (datos != null && datos.length() > 0) {
						JSONObject json_data = datos.getJSONObject(0);
						int numdevueltos = json_data.getInt("cve_usuario");
						if (numdevueltos > 0) {
							Toast.makeText(getBaseContext(), "Conectando...",
									Toast.LENGTH_SHORT).show();

							parametros.clear();
							parametros.add("Usuario");
							parametros.add(txtUsuario.getText().toString());

							datos = post
									.getServerData(
											parametros,
											server_ip
													+ "Proyecto_socialITAAndroid/hacerLogin2.php");
							json_data = datos.getJSONObject(0);
							numdevueltos = 0;
							numdevueltos = json_data.getInt("id");

							if (numdevueltos > 0) {

							}
						}
					} else {
						Toast.makeText(getBaseContext(),
								"Usuario o Contraseña inválida",
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					Toast.makeText(getBaseContext(),
							"Error al conectar con el servidor. ",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
	}
}