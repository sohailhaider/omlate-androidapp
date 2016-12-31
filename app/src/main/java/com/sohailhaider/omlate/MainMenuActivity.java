package com.sohailhaider.omlate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;

public class MainMenuActivity extends AppCompatActivity {
    ArrayList<Data> listItems=new ArrayList<Data>();
    ArrayAdapter<Data> adapter;
    private ListView list;
    Intent intent;
    ProgressBar progressBar;
    private TextView tv;
    String[] data=new String[]{"1", "2", "3"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        intent = new Intent(this, ChatRoom.class);
        list = (ListView) findViewById(R.id.listView);
        tv = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        //tv.setText(Variables.getInstance().FirstName + " " + Variables.getInstance().LastName);
        adapter=new ArrayAdapter<Data>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listItems);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id)
            {
                Data  item = (Data) parent.getItemAtPosition(position);
                Variables.getInstance().LastClassID = item.link;
                startActivity(intent);
            }
        });

        new ClassesTask().execute();
    }


    class ClassesTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                URL url = new URL( Config.getWebLink() +"webapi/GetSchedulesByLearner");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                int CONNECTION_TIMEOUT = 10000;
                int READ_TIMEOUT = 15000;
                urlConnection.setReadTimeout(READ_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("Username", Variables.getInstance().Username);
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
                    JSONArray data = result.getJSONArray("Data");
                    for (int i=0; i<data.length(); i++) {
                        JSONObject actor = data.getJSONObject(i);
                        String Hours = actor.getString("Hours");
                        String Minutes = actor.getString("Minutes");
                        String Seconds = actor.getString("Seconds");
                        int hours = Integer.parseInt(Hours);
                        String AM_PM = "am";
                        String name;
                        if(hours>12) {
                            hours -=12;
                            AM_PM = "pm";
                            name = actor.getString("CourseTitle") + " - " + actor.getString("Date") + "("+ ((Integer.toString(hours).length()>1)? hours: "0" + hours )+ ":" + ((Minutes.length()>1)? Minutes: "0" + Minutes ) + ":" + ((Seconds.length()>1)? Seconds: "0" + Seconds ) +" pm )" ;
                        } else {
                            name = actor.getString("CourseTitle") + " - " + actor.getString("Date") + "()" ;
                        }

                        String link = actor.getString("OfferedCourseID");

                        Data d1 = new Data();
                        d1.name = name;
                        d1.link = link;
                        listItems.add(d1);
                        adapter.notifyDataSetChanged();
                    }

                    //startActivity(intent);
                } else if (result.getString("Status") == "false") {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainMenuActivity.this);
                    alertDialogBuilder.setMessage(result.getString("Message"));
                    alertDialogBuilder.setPositiveButton("Ok", null);
                    alertDialogBuilder.show();
                }
            } catch (JSONException ex) {
                try {
                    new JSONArray(response);
                } catch (JSONException ex1) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainMenuActivity.this);
                    alertDialogBuilder.setMessage(response);
                    alertDialogBuilder.setPositiveButton("Ok", null);
                    alertDialogBuilder.show();
                }
            }

            Log.i("INFO", response);




            //responseView.setText(response);
        }
    }

}

class Data {
    String name;
    String link;

    @Override
    public String toString() {
        return name;
    }
}
