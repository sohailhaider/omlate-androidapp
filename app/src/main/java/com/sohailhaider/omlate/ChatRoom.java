package com.sohailhaider.omlate;

import android.app.AlertDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;

public class ChatRoom extends AppCompatActivity {
    private final Context mContext = this;
    private SignalRService mService;
    private TextView msgBox;
    private EditText msg;
    private boolean mBound = false;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        msgBox = (TextView) findViewById(R.id.MessageTextView);
        msg = (EditText) findViewById(R.id.MessageEditText);
        msg.setHint("Message");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(SignalRService.MESSAGE);
                msgBox.append(s + "\n");
            }
        };



        Button sendButton = (Button) findViewById(R.id.SendMessageButton);
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
        Intent intent = new Intent(this, SignalRService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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

    public void sendMessage(View view) {
        if (mBound) {
            // Call a method from the SignalRService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.

//            EditText editText = (EditText) findViewById(R.id.edit_message);
//            if (editText != null && editText.getText().length() > 0) {
//                String message = editText.getText().toString();
//                mService.sendMessage(message);
//            }
        }
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
