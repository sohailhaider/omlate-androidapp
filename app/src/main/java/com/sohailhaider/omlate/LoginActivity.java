package com.sohailhaider.omlate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends Activity {
    EditText et;
    String Email;
    Intent intent;
    String Password;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent(this, MainMenuActivity.class);
        setContentView(R.layout.activity_login);
        Button login = (Button) findViewById(R.id.loginbutton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et = (EditText) findViewById(R.id.email);
                if( et.getText().toString().trim().equals(""))
                {
                    et.setError( "Your email address is required!" );
                    et.setHint("Email Address Here.");
                    et.requestFocus();
                    return;
                }
                if( !Patterns.EMAIL_ADDRESS.matcher(et.getText().toString()).matches())
                {
                    et.setError( "Invalid email address!" );
                    et.setHint("Valid Email Address.");
                    et.requestFocus();
                    return;
                }
                Email = et.getText().toString();

                et = (EditText) findViewById(R.id.password);
                if( et.getText().toString().trim().equals(""))
                {
                    et.setError( "Your password is required!" );
                    et.requestFocus();
                    return;
                }
                Password = et.getText().toString();


                new LoginTask().execute();
                //View overlay = (View) findViewById(R.id.frameLayout);
                //overlay.setVisibility(View.GONE);
                //startActivity(intent);

            }
        });
    }


    class LoginTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                URL url = new URL( Config.getWebLink() +"webapi/login");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                int CONNECTION_TIMEOUT = 10000;
                int READ_TIMEOUT = 15000;
                urlConnection.setReadTimeout(READ_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("Email", Email)
                        .appendQueryParameter("Password", Password);
                String query = builder.build().getEncodedQuery();

                try {
                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();
                    urlConnection.connect();

                    int response_code = urlConnection.getResponseCode();
                    if (response_code != HttpURLConnection.HTTP_OK) {
                        return "No connection to server found!";
                    }

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            progressBar.setVisibility(View.GONE);
            if(response == null) {
                response = "No connection to server found!";
            }
            try {
                JSONObject result = new JSONObject(response);
                if (result.getString("Status") == "true") {
                    String data = result.getString("Data");
                    JSONObject dataObj = new JSONObject(data);
                    String FirstName = dataObj.getString("FirstName");
                    startActivity(intent);
                } else if (result.getString("Status") == "false") {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                    alertDialogBuilder.setMessage(result.getString("Message"));
                    alertDialogBuilder.setPositiveButton("Ok", null);
                    alertDialogBuilder.show();
                }
            } catch (JSONException ex) {
                try {
                    new JSONArray(response);
                } catch (JSONException ex1) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                    alertDialogBuilder.setMessage(response);
                    alertDialogBuilder.setPositiveButton("OK!", null);
                    alertDialogBuilder.show();
                }
            }

            Log.i("INFO", response);




            //responseView.setText(response);
        }
    }
}
