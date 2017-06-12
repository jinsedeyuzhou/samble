package com.github.sample;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.github.jinsedeyuzhou.VPlayPlayer;

public class MainActivity extends FragmentActivity {
    private String url = "http://gslb.miaopai.com/stream/4YUE0MlhLclpX3HIeA273g__.mp4?yx=&refer=weibo_app";
    private VPlayPlayer vPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vPlayer = (VPlayPlayer) findViewById(R.id.fl_content);
        vPlayer.setShowNavIcon(true);
        vPlayer.setShowContoller(true);
        vPlayer.setTitle(url);
        vPlayer.play(url);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (vPlayer != null) {
            if (vPlayer.handleVolumeKey(keyCode)) {
                return true;
            } else if (vPlayer.onKeyDown(keyCode, event)) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (vPlayer != null)
            vPlayer.onChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vPlayer != null)
            vPlayer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (vPlayer != null)
            vPlayer.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vPlayer != null) {
            vPlayer.onDestory();
            vPlayer = null;
        }

    }
}
