package com.github.sample;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.github.jinsedeyuzhou.VPlayPlayer;

public class MainActivity extends FragmentActivity {
    private String url = "http://gslb.miaopai.com/stream/4YUE0MlhLclpX3HIeA273g__.mp4?yx=&refer=weibo_app";
    private VPlayPlayer vp;
    private DragScaleView dragview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vp = (VPlayPlayer) findViewById(R.id.fl_content);
        dragview = (DragScaleView) findViewById(R.id.dragview);
        vp.setShowNavIcon(true);
        vp.setTitle(url);
        vp.play(url);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (vp!=null&& vp.handleVolumeKey(keyCode))
            return true;
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        if (vp.onBackPressed())
            return ;
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (vp != null)
            vp.onChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vp != null)
            vp.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (vp != null)
            vp.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vp != null) {
            vp.onDestory();
            vp=null;
        }

    }
}
