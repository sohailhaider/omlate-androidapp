package com.sohailhaider.omlate;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class NewCourseDetails extends AppCompatActivity {
    TextView GenericTV;
    ProgressBar progressBar;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course_details);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        GenericTV = (TextView) findViewById(R.id.courseTitle);
        intent = new Intent(this, MainMenuActivity.class);
        GenericTV.setText(Variables.getInstance().LastSelectedCourse.getCourseCode() + " - " +Variables.getInstance().LastSelectedCourse.getCourseTitle());

        GenericTV = (TextView) findViewById(R.id.instructorName);
        GenericTV.setText("By " + Variables.getInstance().LastSelectedCourse.getOfferedByID());

        GenericTV = (TextView) findViewById(R.id.startDate);
        GenericTV.setText(Variables.getInstance().LastSelectedCourse.getStartDate());

        GenericTV = (TextView) findViewById(R.id.endDate);
        GenericTV.setText(Variables.getInstance().LastSelectedCourse.getFinishDate());

        GenericTV = (TextView) findViewById(R.id.creditHours);
        GenericTV.setText(Variables.getInstance().LastSelectedCourse.getCreditHours());

        GenericTV = (TextView) findViewById(R.id.courseCode);
        GenericTV.setText(Variables.getInstance().LastSelectedCourse.getCourseCode());

        GenericTV = (TextView) findViewById(R.id.ctitle);
        GenericTV.setText(Variables.getInstance().LastSelectedCourse.getCourseTitle());

        Button b = (Button) findViewById(R.id.enrollCourseButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetAllCoursesTask().execute();
            }
        });
    }


    class GetAllCoursesTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                URL url = new URL( Config.getWebLink() +"webapi/enrollCourse");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                int CONNECTION_TIMEOUT = 10000;
                int READ_TIMEOUT = 15000;
                urlConnection.setReadTimeout(READ_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("LearnerId", Variables.getInstance().Username)
                        .appendQueryParameter("OfferedCourseId", Variables.getInstance().LastSelectedCourse.getOffereCourseID());
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

                    Toast.makeText(getApplicationContext(),result.getString("Message") , Toast.LENGTH_SHORT).show();

                    startActivity(intent);
                } else if (result.getString("Status") == "false") {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewCourseDetails.this);
                    alertDialogBuilder.setMessage(result.getString("Message"));
                    alertDialogBuilder.setPositiveButton("Ok", null);
                    alertDialogBuilder.show();
                }
            } catch (JSONException ex) {
                try {
                    new JSONArray(response);
                } catch (JSONException ex1) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewCourseDetails.this);
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
