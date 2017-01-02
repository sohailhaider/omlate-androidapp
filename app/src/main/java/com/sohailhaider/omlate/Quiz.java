package com.sohailhaider.omlate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
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
import java.util.Timer;
import java.util.TimerTask;

public class Quiz extends AppCompatActivity {
    Intent intent, HomeIntent, classIntent;
    ProgressBar progressBar;
    int Duration;
    int QuizID;
    int Count;
    ArrayList<Question> Questions;
    ArrayList<Integer> OptionList;
    ArrayList<Integer> IDList;
    LayoutInflater inflater;
    View v;
    TextView remainingTime;
    String answers;
    ScrollView sv;
    LinearLayout ll;
    int ids;
    Quiz CurrentRefference;
    Timer timer;
    CountDownTimer cdt;
    Button submitButton;
    int ticks;
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        CurrentRefference = this;
        HomeIntent = new Intent(this, MainMenuActivity.class);
        classIntent = new Intent(this, MainActivity.class);
        progressBar = (ProgressBar) findViewById(R.id.progressBar7);
        Questions = new ArrayList<Question>();
        OptionList = new ArrayList<Integer>();
        IDList = new ArrayList<Integer>();
        ids = 1;

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.activity_quiz, null);


        sv = (ScrollView) v.findViewById(R.id.quizScrollBar);
        remainingTime = (TextView) v.findViewById(R.id.remaingTime);
        // Create a LinearLayout element
        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);



        submitButton = (Button) v.findViewById(R.id.qzbutton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answers = "";
                for(Question q: Questions) {
                    RadioGroup r = (RadioGroup) findViewById(Integer.parseInt(q.getQuestionId()));
                    int ID = r.getCheckedRadioButtonId();
                    int index = 0;
                    index = IDList.indexOf(ID);
                    if(index>-1)
                        answers+="!#!#!"+ Integer.toString(OptionList.get(index));
                    else
                        answers+="!#!#!"+ Integer.toString(index);
                }
                Log.i("ANSWERS", answers);
                new SaveQuizAttemptTask().execute();
            }
        });
        new GetQuizTask().execute();
    }


    class GetQuizTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                URL url = new URL( Config.getWebLink() +"webapi/getQuizById");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                int CONNECTION_TIMEOUT = 10000;
                int READ_TIMEOUT = 15000;
                urlConnection.setReadTimeout(READ_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("Username", Variables.getInstance().Username)
                        .appendQueryParameter("id", Integer.toString(Variables.getInstance().LastQuizID));
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
                    JSONObject Data = result.getJSONObject("Data");
                    Count = Integer.parseInt(Data.getString("Count"));
                    Duration = Integer.parseInt(Data.getString("Duration"));
                    ticks = Duration*60*1000;
//                    timer = new Timer();
//                    timer.schedule(new TimerTask() {
//                        public void run() {
//                            ticks -=1000;
//                            Log.i("INFo",Integer.toString(ticks/1000));
//                        }
//                    }, 1000);

                    if(cdt != null) {
                        cdt.cancel();
                    }

                    cdt = new CountDownTimer(ticks, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long seconds = (millisUntilFinished%60000)/1000;
                            long mints = millisUntilFinished/60000;

                            Log.i("INFO", "Countdown seconds remaining: " + mints + ":" + seconds);

                            remainingTime.setText("Remaining: " + ((Long.toString(mints).length()<2)? "0":"") + mints + ":" + ((Long.toString(seconds).length()<2)? "0":"") + seconds);
                        }

                        @Override
                        public void onFinish() {
                            finishActivity(0);
//                            HomeIntent.setFlags(HomeIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
//                            startActivity(HomeIntent);

                            submitButton.callOnClick();
                            Log.i("INFO", "Timer finished");
                        }
                    };
                    cdt.start();
                    JSONArray questions = Data.getJSONArray("Questions");

                    for (int i=0; i<questions.length(); i++) {
                        JSONObject actor = questions.getJSONObject(i);
                        Question q = new Question();
                        q.setQuestionId(actor.getString("ID"));
                        q.setQuestionStatement(actor.getString("QuestionStatement"));
                        q.setOptions(actor.getString("Options"));
                        q.setAnswer(actor.getString("Answer"));
                        Questions.add(q);
                    }

                    for(Question qes: Questions) {
                        Log.i(qes.getQuestionId(), qes.getQuestionStatement());
                        TextView tv = new TextView(CurrentRefference);
                        tv.setText(qes.getQuestionStatement());
                        //tv.setId(Integer.parseInt(qes.getQuestionId()));
                        ll.addView(tv);

                        RadioGroup radioGroup = new RadioGroup(CurrentRefference);
                        radioGroup.setOrientation(RadioGroup.HORIZONTAL);
                        radioGroup.setId(Integer.parseInt(qes.getQuestionId()));
                        String []options = qes.getOptions().split("!#!#!");
                        boolean firstDone = false;
                        int co = 1;
                        if(options.length > 1) {
                            for(String s:options) {
                                if(!firstDone)
                                    firstDone = true;
                                else {
                                    RadioButton radioButton = new RadioButton(CurrentRefference);
                                    radioButton.setText(s);
                                    radioButton.setId((Integer.parseInt(qes.getQuestionId())*25) +ids);
                                    IDList.add(radioButton.getId());
                                    OptionList.add(co);
                                    ids++;
                                    co++;
                                    radioGroup.addView(radioButton);
                                }
                            }
                        }

                        ll.addView(radioGroup);
                    }
        sv.addView(ll);

        // Display the view
        setContentView(v);
                } else if (result.getString("Status") == "false") {
                    Toast.makeText(getApplicationContext(), result.getString("Message"), Toast.LENGTH_SHORT).show();
                    startActivity(HomeIntent);
                }
            } catch (JSONException ex) {
                try {
                    new JSONArray(response);
                } catch (JSONException ex1) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Quiz.this);
                    alertDialogBuilder.setMessage(response);
                    alertDialogBuilder.setPositiveButton("Ok", null);
                    alertDialogBuilder.show();
                }
            }

            Log.i("INFO", response);




            //responseView.setText(response);
        }
    }



    class SaveQuizAttemptTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            try {
                URL url = new URL( Config.getWebLink() +"webapi/SaveQuizAttempt");

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
                        .appendQueryParameter("QuizId", Integer.toString(Variables.getInstance().LastQuizID))
                        .appendQueryParameter("Answers", answers);
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
                    Toast.makeText(getApplicationContext(), result.getString("Message"), Toast.LENGTH_SHORT).show();
                    startActivity(classIntent);
                } else if (result.getString("Status") == "false") {
                    Toast.makeText(getApplicationContext(), result.getString("Message"), Toast.LENGTH_SHORT).show();
                    startActivity(HomeIntent);
                }
            } catch (JSONException ex) {
                try {
                    new JSONArray(response);
                } catch (JSONException ex1) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Quiz.this);
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
class Question {
    String QuestionStatement;
    String Answer;
    String Options;
    String QuestionId;
    ArrayList<String> OptionList;
    ArrayList<String> IDList;

    public String getQuestionStatement() {
        return QuestionStatement;
    }

    public void setQuestionStatement(String questionStatement) {
        QuestionStatement = questionStatement;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public String getOptions() {
        return Options;
    }

    public void setOptions(String options) {
        Options = options;
    }

    public String getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(String questionId) {
        QuestionId = questionId;
    }
}
