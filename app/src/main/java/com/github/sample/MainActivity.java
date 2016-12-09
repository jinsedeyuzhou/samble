package com.github.sample;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.github.jinsedeyuzhou.view.CustomDialog;
import com.github.jinsedeyuzhou.view.VPlayerView;

public class MainActivity extends AppCompatActivity {
    private String url="http://gslb.miaopai.com/stream/4YUE0MlhLclpX3HIeA273g__.mp4?yx=&refer=weibo_app";
    private  int mporit;
    private FrameLayout content;
    private VPlayerView vp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        vp = new VPlayerView(this);

        vp.setShowNavIcon(true);
        vp.setTitle(url);
        vp.start(url);

//        vp.setTitle(url);
        content = (FrameLayout) findViewById(R.id.fl_content);
        mporit= content.getLayoutParams().height;
        content.addView(vp);
        findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),VideoListActivity.class);
                startActivity(intent);


            }
        });

        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("这个就是自定义的提示框");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //设置你的操作事项
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
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
        vp.onChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        vp.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vp.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vp.onDestroy();
    }
}
