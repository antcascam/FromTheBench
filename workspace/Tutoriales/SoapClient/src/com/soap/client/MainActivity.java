package com.soap.client;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
 
public class MainActivity extends Activity {
 private TextView textView;
 
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);
  textView = (TextView) findViewById(R.id.textView1);
  this.accessWebService(textView);
 }
 
 @Override
 public boolean onCreateOptionsMenu(Menu menu) {
  // Inflate the menu; this adds items to the action bar if it is present.
  getMenuInflater().inflate(R.menu.main, menu);
  return true;
 }
  
 //starting asynchronus task
 private class SoapAccessTask extends AsyncTask<String, Void, String> {
      
     @Override
     protected void onPreExecute() {
          //if you want, start progress dialog here
     }
          
     @Override
     protected String doInBackground(String... urls) {
         String webResponse = "";
        try{
          final String NAMESPACE = "http://www.webserviceX.NET/";
          final String URL = "http://www.webservicex.net/CurrencyConvertor.asmx";
          final String SOAP_ACTION = "http://www.webserviceX.NET/ConversionRate";
          final String METHOD_NAME = "ConversionRate";
           
          SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
          PropertyInfo fromProp =new PropertyInfo();
          fromProp.setName("FromCurrency");
          //gets the first element from urls array
          fromProp.setValue(urls[0]);
          fromProp.setType(String.class);
          request.addProperty(fromProp);
             
          PropertyInfo toProp =new PropertyInfo();
          toProp.setName("ToCurrency");
          //second element of the urls array
          toProp.setValue(urls[1]);
          toProp.setType(String.class);
          request.addProperty(toProp);
            
          SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
          envelope.dotNet = true;
          envelope.setOutputSoapObject(request);
          HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
           
          androidHttpTransport.call(SOAP_ACTION, envelope);
          SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
          webResponse = response.toString();
       }
       catch(Exception e){
          Toast.makeText(getApplicationContext(),"Cannot access the web service"+e.toString(), Toast.LENGTH_LONG).show();
        }
         return webResponse;
    }
   
    @Override
    protected void onPostExecute(String result) {
            //if you started progress dialog dismiss it here
            textView.setText(result);
            Toast.makeText(getApplicationContext(),"Completed...", Toast.LENGTH_LONG).show();
         }
     }
  
   public void accessWebService(View view) {
   SoapAccessTask task = new SoapAccessTask();
      //passes values for the urls string array
      task.execute(new String[] { "USD","LKR"});
     }  
    
}
