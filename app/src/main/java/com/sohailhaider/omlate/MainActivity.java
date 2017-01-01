package com.sohailhaider.omlate;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    VideoView mVideoView;
    String path;
    private final Context mContext = this;
    private SignalRService mService;
    private TextView msgBox;
    private EditText msg;
    private boolean mBound = false;
    private BroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LibsChecker.checkVitamioLibs(this))  //Important!
            return;

        setContentView(R.layout.activity_main);
        msgBox = (TextView) findViewById(R.id.msgtextView);
        msg = (EditText) findViewById(R.id.MsgeditText);
        msg.setHint("Message");
        mVideoView = (VideoView) findViewById(R.id.vitamio_videoView);
        //path = "rtmp://184.72.239.149/vod/BigBuckBunny_115k.mov";
        path = "rtmp://"+Config.Red5ServerIP+"/"+Config.Red5PublicSub+"/"+Variables.getInstance().LastClassID;
        mVideoView.setVideoPath(path);
        mVideoView.requestFocus();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(SignalRService.MESSAGE);
                msgBox.append(s + "\n");
            }
        };

        Button sendButton = (Button) findViewById(R.id.msgsendbutton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( msg.getText().toString().trim().equals(""))
                {
                    msg.setError( "Message can't be empty!");
                    msg.requestFocus();
                    return;
                }

                mService.sendMessage(msg.getText().toString());
                msg.setText("");
                Toast.makeText(getApplicationContext(), "Message Sent!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent2 = new Intent(this, SignalRService.class);
        bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(SignalRService.RESULT)
        );
    }

    @Override
    protected void onStop() {
        // Unbind from the service
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }





    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
