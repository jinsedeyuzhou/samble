package com.github.sample;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.github.jinsedeyuzhou.VPlayPlayer;

public class MainActivity extends FragmentActivity {
    private String url = "http://gslb.miaopai.com/stream/4YUE0MlhLclpX3HIeA273g__.mp4?yx=&refer=weibo_app";
    private FrameLayout content;
    private VPlayPlayer vp;
    private int initHeight;
    private DragScaleView dragview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vp = (VPlayPlayer) findViewById(R.id.fl_content);
        dragview = (DragScaleView) findViewById(R.id.dragview);
//        initHeight = content.getLayoutParams().height;
//        if (vp == null)
//            vp =new VPlayPlayer(this);
        vp.setShowNavIcon(true);
        vp.setTitle(url);
//        if (vp.getParent() != null)
//            ((ViewGroup) vp.getParent()).removeAllViews();

        vp.play(url);
//        content.addView(vp);

//
//        findViewById(R.id.btn_list_video).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(MainActivity.this, ListViewActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        CustomDialog.Builder builder = new CustomDialog.Builder(this);
//        builder.setMessage("这个就是自定义的提示框");
//        builder.setTitle("提示");
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                //设置你的操作事项
//            }
//        });
//
//        builder.setNegativeButton("取消",
//                new android.content.DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//        builder.create().show();
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
//        if (newConfig.orientation==Configuration.ORIENTATION_PORTRAIT)
//        {
//            ViewGroup.LayoutParams params = content.getLayoutParams();
//            int widthPixels = getResources().getDisplayMetrics().widthPixels;
//            params.height = initHeight;
//            content.setLayoutParams(params);
//        }else
//        {
//            ViewGroup.LayoutParams params = content.getLayoutParams();
//            int heightPixels =getResources().getDisplayMetrics().heightPixels;
//            int widthPixels = getResources().getDisplayMetrics().widthPixels;
//            params.height=widthPixels;
////            params.width=widthPixels;
//            content.setLayoutParams(params);
//        }

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
        if (vp != null)
            vp.onDestory();
    }
}
