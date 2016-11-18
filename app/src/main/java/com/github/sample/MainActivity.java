package com.github.sample;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.github.jinsedeyuzhou.view.VPlayerView;

public class MainActivity extends AppCompatActivity {
    private String url="http://119.90.25.48/record2.a8.com/mp4/1477100428026014.mp4";
    private  int mporit;
    private FrameLayout content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        VPlayerView vp=new VPlayerView(this);

        vp.start(url);
//        vp.setTitle(url);
        content = (FrameLayout) findViewById(R.id.fl_content);
        mporit= content.getLayoutParams().height;
        content.addView(vp);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation== Configuration.ORIENTATION_PORTRAIT)
            {
                content.getLayoutParams().height=mporit;
            }
            else
            {
                content.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
            }
    }
}
