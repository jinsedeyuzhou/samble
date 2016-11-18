package com.github.jinsedeyuzhou.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jinsedeyuzhou.R;
import com.github.jinsedeyuzhou.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Berkeley on 11/2/16.
 */
public class VPlayerView extends RelativeLayout implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {


    private View rootView;
    private View controlbar;
    private IjkVideoView mVideoView;
    private CustomMediaContoller mediaController;
    private Context mContext;
    private Handler handler=new Handler();
    private boolean isPause;
    private boolean portrait;
    private View toolbar;
    private ImageView finish;
    private TextView mTitle;
//    private ConnectionChangeReceiver changeReceiver;

    public VPlayerView(Context context) {
        super(context);
        mContext = context;
        initData();
        initView();
        initAction();

    }

    private void initAction() {


    }

    private void initView() {

        rootView = LayoutInflater.from(mContext).inflate(R.layout.video_player, this, true);
        controlbar = findViewById(R.id.media_contoller);
        /**
         * toolsbar
         */
        toolbar = findViewById(R.id.player_toolbar);
        mTitle = (TextView) findViewById(R.id.tv_video_title);
        finish = (ImageView) findViewById(R.id.iv_video_finish);


//        IntentFilter intentFilter=new IntentFilter();
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        changeReceiver=new ConnectionChangeReceiver();
//        mContext.registerReceiver(changeReceiver,intentFilter);

        mVideoView = (IjkVideoView) findViewById(R.id.main_video);
        mediaController = new CustomMediaContoller(mContext, rootView);
        mVideoView.setMediaController(mediaController);

        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                controlbar.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                if (mediaController.getScreenOrientation((Activity) mContext)
                        == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    //横屏播放完毕，重置
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    setLayoutParams(layoutParams);
                }
                if (completionListener != null)
                    completionListener.completion(mp);
            }
        });


    }

    private void initData() {

    }

    public VPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isPlay() {
        return mVideoView.isPlaying();
    }

    public void pause() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        } else {
            mVideoView.start();
        }
    }

    public void start(String path) {
        Uri uri = Uri.parse(path);
        if (mediaController != null)
            mediaController.start();

        if (!mVideoView.isPlaying()) {
            mVideoView.setVideoURI(uri);
//            mVideoView.start();
        } else {
            mVideoView.stopPlayback();
            mVideoView.setVideoURI(uri);
//            mVideoView.start();
        }
    }

    public void start(){
        if (mVideoView.isPlaying()){
            mVideoView.start();
        }
    }

    public void onPause()
    {
        pause();
    }
    public void onResume()
    {
        start();
    }

    public void setContorllerVisiable(){
        mediaController.setVisiable();
    }

    public void seekTo(int msec){
        mVideoView.seekTo(msec);
    }

    public void onChanged(Configuration configuration) {
        portrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(portrait);
    }

    public void doOnConfigurationChanged(final boolean portrait) {
        if (mVideoView != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setFullScreen(!portrait);
                    if (portrait) {
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        Log.e("handler", "400");
                        setLayoutParams(layoutParams);
                        requestLayout();
                    } else {
                        int heightPixels = ((Activity) mContext).getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = ((Activity) mContext).getResources().getDisplayMetrics().widthPixels;
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = heightPixels;
                        layoutParams.width = widthPixels;
                        Log.e("handler", "height==" + heightPixels + "\nwidth==" + widthPixels);
                        setLayoutParams(layoutParams);
                    }

                    mediaController.updateFullScreenButton();
                }
            });
//            orientationEventListener.enable();
        }
    }

    public void stop() {
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
    }

    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
//        orientationEventListener.disable();
//        mContext.unregisterReceiver(changeReceiver);
    }

    private void setFullScreen(boolean fullScreen) {
        if (mContext != null && mContext instanceof Activity) {
            WindowManager.LayoutParams attrs = ((Activity) mContext).getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                ((Activity) mContext).getWindow().setAttributes(attrs);
                ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ((Activity) mContext).getWindow().setAttributes(attrs);
                ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }

    }

    public void setTitle(String str)
    {
        if (mVideoView==null)
            return ;
        mTitle.setText(str);

    }

    public void setShowContoller(boolean isShowContoller) {
        mediaController.setShowContoller(isShowContoller);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public long getPalyPostion() {
        return mVideoView.getCurrentPosition();
    }

    public void release() {
        mVideoView.release(true);
    }

    public int VideoStatus() {
        return mVideoView.getCurrentStatue();
    }




    private CompletionListener completionListener;

    public void setCompletionListener(CompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    public interface CompletionListener {
        void completion(IMediaPlayer mp);
    }

//    class ConnectionChangeReceiver extends BroadcastReceiver {
//        private  final String TAG = ConnectionChangeReceiver.class.getSimpleName();
//
//        private boolean isWifi;
//        private boolean isMobile;
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.e(TAG, "网络状态改变");
//            //获得网络连接服务
//            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
//            //获取wifi连接状态
//            NetworkInfo.State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//            //判断是否正在使用wifi网络
//            if (wifi == NetworkInfo.State.CONNECTED) {
//                isWifi = true;
//            }
//            else
//                isWifi=false;
//            //获取GPRS状态
//            NetworkInfo.State  state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
//            //判断是否在使用GPRS网络
//            if (state == NetworkInfo.State.CONNECTED) {
//                isMobile = true;
//                mVideoView.pause();
//            }
//            else
//                isMobile=false;
//            //如果没有连接成功
//            if(!isWifi){
//                Toast.makeText(context,"当前网络无连接",Toast.LENGTH_SHORT).show();
//                mediaController.pause();
//            }
//            else if (!isMobile)
//            {
//                mediaController.pause();
//                mediaController.show();
//
//            }
//            else
//            {
//                mediaController.pause();
//            }
//
//        }
//
//    }

}
