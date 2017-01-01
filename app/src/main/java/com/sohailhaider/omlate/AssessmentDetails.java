package com.sohailhaider.omlate;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AssessmentDetails extends AppCompatActivity {
    TextView GenericTV;
    ProgressBar progressBar;
    Intent intent;
    DownloadManager manager;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);
        progressBar = (ProgressBar) findViewById(R.id.progressBar6);
        intent = new Intent(this, MainMenuActivity.class);

        GenericTV = (TextView) findViewById(R.id.assessmentTitle);
        GenericTV.setText(Variables.getInstance().LastAssessment.getAssessmentTitle());

        GenericTV = (TextView) findViewById(R.id.note);
        GenericTV.setText(Variables.getInstance().LastAssessment.getNote());

        GenericTV = (TextView) findViewById(R.id.courseTitleinDetails);
        GenericTV.setText(Variables.getInstance().LastAssessment.getCourseTitle());

        GenericTV = (TextView) findViewById(R.id.startDateAssessment);
        GenericTV.setText(Variables.getInstance().LastAssessment.getDateTime());

        GenericTV = (TextView) findViewById(R.id.dueDateAssessment);
        GenericTV.setText(Variables.getInstance().LastAssessment.getDueDate());

        GenericTV = (TextView) findViewById(R.id.instructorInAssessment);
        GenericTV.setText(Variables.getInstance().LastAssessment.getOfferedByID());

        GenericTV = (TextView) findViewById(R.id.pointsInAssessment);
        GenericTV.setText(Variables.getInstance().LastAssessment.getPoints());

        Button downloadbutton = (Button) findViewById(R.id.downloadAttachment);
        downloadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = String.format(Config.getWebLink() + "Learner/DownloadAssessment/"+ Variables.getInstance().LastAssessment.getAssessmentID() + "?courseid=1013");

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setDescription(Variables.getInstance().LastAssessment.getCourseCode() + " - " +Variables.getInstance().LastAssessment.getCourseTitle() + " \nBy "+ Variables.getInstance().LastAssessment.getOfferedByID());
                    request.setTitle(Variables.getInstance().LastAssessment.getAssessmentTitle() + " - " + Variables.getInstance().LastAssessment.getAssessmentID());
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Variables.getInstance().LastAssessment.getAssessmentTitle());
                    manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    long rs= manager.enqueue(request);
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(rs);

                    cursor = manager.query(q);

                    Log.i("idDown",rs+"");
                } catch (Exception e) {
                    Log.i("idDown",e.getMessage().toString());
                    Toast.makeText(getApplicationContext(),"Can't Download File: ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(getApplicationContext(),"File Downloaded", Toast.LENGTH_SHORT).show();
        }
    };
}
