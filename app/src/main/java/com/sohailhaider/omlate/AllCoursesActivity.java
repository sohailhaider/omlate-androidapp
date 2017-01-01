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
import java.util.List;

public class AllCoursesActivity extends AppCompatActivity {
    ArrayList<CoursesListViewData> listItems=new ArrayList<CoursesListViewData>();
    ArrayAdapter<CoursesListViewData> adapter;
    private ListView list;
    Intent intent, HomeIntent;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_courses);
        intent = new Intent(this, NewCourseDetails.class);
        HomeIntent = new Intent(this, MainMenuActivity.class);
        list = (ListView) findViewById(R.id.listView2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);

        adapter=new ArrayAdapter<CoursesListViewData>(this, android.R.layout.simple_list_item_2, android.R.id.text1, listItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(listItems.get(position).toString());
                text2.setText("By " + listItems.get(position).getOfferedByID() + "(" + listItems.get(position).getStartDate() + "-" +  listItems.get(position).getFinishDate() + ")");
                return view;
            }
        };
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id)
            {
                CoursesListViewData  item = (CoursesListViewData) parent.getItemAtPosition(position);
                Variables.getInstance().LastSelectedCourse = item;

                //Toast.makeText(getApplicationContext(), Variables.getInstance().LastSelectedCourse.toString(), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });


        new GetAllCoursesTask().execute();
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
                URL url = new URL( Config.getWebLink() +"webapi/getAllCoursesWithLearnerId");

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

                        CoursesListViewData d1 = new CoursesListViewData();
                        d1.setOfferedByID(actor.getString("OfferedByID"));
                        d1.setOffereCourseID(actor.getString("OfferedCourseID"));
                        d1.setCourseTitle(actor.getString("CourseTitle"));
                        d1.setCourseCode(actor.getString("CourseCode"));
                        d1.setStartDate(actor.getString("StartDate"));
                        d1.setFinishDate(actor.getString("FinishDate"));
                        d1.setCourseImage(actor.getString("CourseImage"));
                        d1.setCreditHours(actor.getString("CreditHours"));

                        listItems.add(d1);
                        adapter.notifyDataSetChanged();
                    }

                    //startActivity(intent);
                } else if (result.getString("Status") == "false") {
                    Toast.makeText(getApplicationContext(), result.getString("Message"), Toast.LENGTH_SHORT).show();

                    startActivity(HomeIntent);
                }
            } catch (JSONException ex) {
                try {
                    new JSONArray(response);
                } catch (JSONException ex1) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AllCoursesActivity.this);
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
class CoursesListViewData {
    String CourseTitle;
    String CourseCode;
    String CreditHours;
    String OffereCourseID;
    String OfferedByID;
    String StartDate;
    String FinishDate;
    String CourseImage;

    public String getCreditHours() {
        return CreditHours;
    }

    public void setCreditHours(String creditHours) {
        CreditHours = creditHours;
    }

    @Override
    public String toString() {
        return CourseCode + " - " + CourseTitle.toString();
    }

    public String getCourseTitle() {
        return CourseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        CourseTitle = courseTitle;
    }

    public String getCourseCode() {
        return CourseCode;
    }

    public void setCourseCode(String courseCode) {
        CourseCode = courseCode;
    }

    public String getOffereCourseID() {
        return OffereCourseID;
    }

    public void setOffereCourseID(String offereCourseID) {
        OffereCourseID = offereCourseID;
    }

    public String getOfferedByID() {
        return OfferedByID;
    }

    public void setOfferedByID(String offeredByID) {
        OfferedByID = offeredByID;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getFinishDate() {
        return FinishDate;
    }

    public void setFinishDate(String finishDate) {
        FinishDate = finishDate;
    }

    public String getCourseImage() {
        return CourseImage;
    }

    public void setCourseImage(String courseImage) {
        CourseImage = courseImage;
    }
}