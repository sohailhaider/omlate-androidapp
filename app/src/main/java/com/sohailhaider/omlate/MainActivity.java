package com.sohailhaider.omlate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    VideoView mVideoView;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LibsChecker.checkVitamioLibs(this))  //Important!
            return;

        setContentView(R.layout.activity_main);
        mVideoView = (VideoView) findViewById(R.id.vitamio_videoView);
        path = "rtmp://184.72.239.149/vod/BigBuckBunny_115k.mov";
//        path = "rtmp://192.168.10.2/omlate";
        mVideoView.setVideoPath(path);
        mVideoView.requestFocus();
    }
}
