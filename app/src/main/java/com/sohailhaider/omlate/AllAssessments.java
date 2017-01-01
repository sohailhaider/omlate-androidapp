package com.sohailhaider.omlate;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class AllAssessments extends AppCompatActivity {
    ArrayList<Assessment> listItems=new ArrayList<Assessment>();
    ArrayAdapter<Assessment> adapter;
    private ListView list;
    Intent intent, HomeIntent;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_assessments);
        intent = new Intent(this, NewCourseDetails.class);
        HomeIntent = new Intent(this, MainMenuActivity.class);
        list = (ListView) findViewById(R.id.listView4);
        progressBar = (ProgressBar) findViewById(R.id.progressBar5);


        adapter=new ArrayAdapter<Assessment>(this, android.R.layout.simple_list_item_2, android.R.id.text1, listItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(listItems.get(position).getAssessmentTitle());
                text2.setText(listItems.get(position).getDueDate());
                return view;
            }
        };
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id)
            {
                Assessment  item = (Assessment) parent.getItemAtPosition(position);
                Variables.getInstance().LastAssessment = item;

                //Toast.makeText(getApplicationContext(), Variables.getInstance().LastSelectedCourse.toString(), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        new GetAssessmentsTask().execute();
    }




    class GetAssessmentsTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                URL url = new URL( Config.getWebLink() +"webapi/getAssignmentsByLearnerId");

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

                        Assessment d1 = new Assessment();
                        d1.setAssessmentID(actor.getString("AssessmentID"));
                        d1.setAssessmentTitle(actor.getString("AssessmentTitle"));
                        d1.setDueDate(actor.getString("DueDate"));
                        d1.setDateTime(actor.getString("DateTime"));
                        d1.setNote(actor.getString("Note"));
                        d1.setPoints(actor.getString("Points"));
                        d1.setSubmissions(actor.getString("Submissions"));
                        d1.setCourseCode(actor.getString("CourseCode"));
                        d1.setCourseTitle(actor.getString("CourseTitle"));
                        d1.setOfferedByID(actor.getString("OfferedByID"));

                        listItems.add(d1);
                        adapter.notifyDataSetChanged();
                    }
                } else if (result.getString("Status") == "false") {
                    Toast.makeText(getApplicationContext(), result.getString("Message"), Toast.LENGTH_SHORT).show();

                    startActivity(HomeIntent);
                }
            } catch (JSONException ex) {
                try {
                    new JSONArray(response);
                } catch (JSONException ex1) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AllAssessments.this);
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

class Assessment {
    String AssessmentID;
    String AssessmentTitle;
    String DueDate;
    String DateTime;
    String Note;
    String Points;
    String Submissions;
    String CourseCode;
    String CourseTitle;
    String OfferedByID;

    public String getAssessmentID() {
        return AssessmentID;
    }

    public void setAssessmentID(String assessmentID) {
        AssessmentID = assessmentID;
    }

    public String getAssessmentTitle() {
        return AssessmentTitle;
    }

    public void setAssessmentTitle(String assessmentTitle) {
        AssessmentTitle = assessmentTitle;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getPoints() {
        return Points;
    }

    public void setPoints(String points) {
        Points = points;
    }

    public String getSubmissions() {
        return Submissions;
    }

    public void setSubmissions(String submissions) {
        Submissions = submissions;
    }

    public String getCourseCode() {
        return CourseCode;
    }

    public void setCourseCode(String courseCode) {
        CourseCode = courseCode;
    }

    public String getCourseTitle() {
        return CourseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        CourseTitle = courseTitle;
    }

    public String getOfferedByID() {
        return OfferedByID;
    }

    public void setOfferedByID(String offeredByID) {
        OfferedByID = offeredByID;
    }
}