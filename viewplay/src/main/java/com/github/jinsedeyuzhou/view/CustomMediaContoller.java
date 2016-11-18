package com.github.jinsedeyuzhou.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jinsedeyuzhou.R;
import com.github.jinsedeyuzhou.media.IMediaController;
import com.github.jinsedeyuzhou.media.IjkVideoView;
import com.github.jinsedeyuzhou.utils.NetworkUtils;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Berkeley on 11/2/16.
 */
public class CustomMediaContoller implements IMediaController {

    private static final String TAG = "CustomMediaContoller";
    private Context mContext;
    private View view;
    private View contollerbar;
    private IjkVideoView mVideoView;


//    private static final int SET_VIEW_HIDE = 1;
//    private static final int TIME_OUT = 5000;
//    private static final int MESSAGE_SHOW_PROGRESS = 2;
//    private static final int PAUSE_IMAGE_HIDE = 3;
//    private static final int MESSAGE_SEEK_NEW_POSITION = 4;
//    private static final int MESSAGE_HIDE_CONTOLL = 5;

    //是否展示
    private boolean isShow;
    //是否拖动
    private boolean isDragging;
    //是否显示控制bar
    private boolean isShowContoller=true;

    private boolean isSound;
    private final AudioManager audioManager;
    //默认超时时间
    private int defaultTimeout = 3000;
    //是否可以使用移动网络播放
    private boolean mobile;


    //初始化view
    private ProgressBar progressBar;
    private SeekBar seekBar;
    private TextView allTime;
    private TextView time;
    private ImageView full;
    private ImageView sound;
    private ImageView play;
    private ImageView pauseImage;
    private LinearLayout brightness_layout;
    private VSeekBar brightness_seek;
    private LinearLayout sound_layout;
    private VSeekBar sound_seek;
    private RelativeLayout show;
    private TextView seekTxt;
    private Bitmap bitmap;
    private GestureDetector detector;
    private final View toolbar;
    private LinearLayout top_box;


    private int volume = -1;
    private float brightness = -1;
    private long newPosition = 1;
    private int mMaxVolume;
    private long duration;
    private boolean isPlay;
    private ConnectionChangeReceiver changeReceiver;


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handle");
            switch (msg.what) {
                case PlayStateParams.SET_VIEW_HIDE:
                    isShow = false;
                    contollerbar.setVisibility(View.GONE);
                    toolbar.setVisibility(View.GONE);
                    Log.d(TAG, "handleMessage1");
                    hide();
                    break;
                case PlayStateParams.MESSAGE_SHOW_PROGRESS:
                    Log.d(TAG, "handleMessage2");
                    setProgress();
                    if (!isDragging && isShow) {
                        msg = obtainMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
                    }
                    break;
                case PlayStateParams.PAUSE_IMAGE_HIDE:
                    Log.d(TAG, "handleMessage3");
                    pauseImage.setVisibility(View.GONE);
                    break;
                case PlayStateParams.MESSAGE_SEEK_NEW_POSITION:
                    if (newPosition >= 0) {
                        mVideoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;
                case PlayStateParams.MESSAGE_HIDE_CONTOLL:
                    Log.d(TAG, "handleMessage4");
                    seekTxt.setVisibility(View.GONE);
                    brightness_layout.setVisibility(View.GONE);
                    sound_layout.setVisibility(View.GONE);
                    break;
            }
        }
    };


    public CustomMediaContoller(Context context, View view) {

        this.view = view;
        contollerbar = view.findViewById(R.id.media_contoller);
        this.mVideoView = (IjkVideoView) view.findViewById(R.id.main_video);
        toolbar = view.findViewById(R.id.player_toolbar);
        toolbar.setVisibility(View.GONE);
        contollerbar.setVisibility(View.GONE);
        isShow = false;
        isDragging = false;
        isShowContoller = true;
        this.mContext = context;
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        changeReceiver=new ConnectionChangeReceiver();
        mContext.registerReceiver(changeReceiver,intentFilter);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        initView();
        initAction();

    }

    private void initView() {

        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        seekBar = (SeekBar) contollerbar.findViewById(R.id.seekbar);
        allTime = (TextView) contollerbar.findViewById(R.id.all_time);
        time = (TextView) contollerbar.findViewById(R.id.time);
        full = (ImageView) contollerbar.findViewById(R.id.full);
        sound = (ImageView) contollerbar.findViewById(R.id.sound);
        play = (ImageView) contollerbar.findViewById(R.id.player_btn);
        pauseImage = (ImageView) view.findViewById(R.id.pause_image);

        brightness_layout = (LinearLayout) view.findViewById(R.id.brightness_layout);
        brightness_seek = (VSeekBar) view.findViewById(R.id.brightness_seek);
        sound_layout = (LinearLayout) view.findViewById(R.id.sound_layout);
        sound_seek = (VSeekBar) view.findViewById(R.id.sound_seek);
        show = (RelativeLayout) view.findViewById(R.id.show);
        seekTxt = (TextView) view.findViewById(R.id.seekTxt);


        top_box = (LinearLayout) toolbar.findViewById(R.id.app_video_top_box);
    }

    private void initAction() {

        sound_seek.setEnabled(true);
        brightness_seek.setEnabled(true);
        isSound = true;
        detector = new GestureDetector(mContext, new PlayGestureListener());
        mMaxVolume = ((AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String string = generateTime((long) (duration * progress * 1.0f / 100));
                time.setText(string);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setProgress();
                isDragging = true;
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
                show();
                handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;
                mVideoView.seekTo((int) (duration * seekBar.getProgress() * 1.0f / 100));
                handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                isDragging = false;
                handler.sendEmptyMessageDelayed(PlayStateParams.MESSAGE_SHOW_PROGRESS, 1000);
                show();
            }
        });
        //可以设置是否
//        view.setClickable(true);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (detector.onTouchEvent(event))
                    return true;

                // 处理手势结束
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }
                return false;
            }
        });


//        contollerbar.setClickable(true);
//        contollerbar.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.e(TAG, "event");
//
//                Rect seekRect = new Rect();
//                seekBar.getHitRect(seekRect);
//
//
//                if ((event.getY() >= (seekRect.top - 50)) && (event.getY() <= (seekRect.bottom + 50))) {
//
//                    float y = seekRect.top + seekRect.height() / 2;
//                    //seekBar only accept relative x
//                    float x = event.getX() - seekRect.left;
//                    if (x < 0) {
//                        x = 0;
//                    } else if (x > seekRect.width()) {
//                        x = seekRect.width();
//                    }
//                    MotionEvent me = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),
//                            event.getAction(), x, y, event.getMetaState());
//                    return seekBar.onTouchEvent(me);
//
//                }
//                return false;
//            }
//        });

        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {

                Log.e("setOnInfoListener", what + "");
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //开始缓冲
                        if (progressBar.getVisibility() == View.GONE)
                            progressBar.setVisibility(View.VISIBLE);
                        play.setVisibility(View.GONE);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //开始播放
                        progressBar.setVisibility(View.GONE);
                        play.setVisibility(View.VISIBLE);
                        break;

                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
//                        statusChange(STATUS_PLAYING);
                        progressBar.setVisibility(View.GONE);
                        break;

                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        progressBar.setVisibility(View.GONE);
                        break;
                }
                onInfoListener.onInfo(what,extra);
                return false;
            }
        });
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {

                onErrorListener.onError(i, i1);
                return true;
            }
        });

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSound) {
                    //静音
                    sound.setImageResource(R.mipmap.sound_mult_icon);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                } else {
                    //取消静音
                    sound.setImageResource(R.mipmap.sound_open_icon);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                }
                isSound = !isSound;
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable(mContext)||mobile)
                {
                    if (mVideoView.isPlaying()) {
                        pause();
//                    isPlay = false;
                        handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
                    } else {
                        reStart();
//                    isPlay = true;
                        handler.sendMessageDelayed(handler.obtainMessage(PlayStateParams.SET_VIEW_HIDE), PlayStateParams.TIME_OUT);
                    }
                }
                else if (!NetworkUtils.isNetworkAvailable(mContext)&&!mobile)
                {
                   showDialog();
                }
                else
                    return ;

            }
        });

        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("full", "full");
                if (getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                updateFullScreenButton();
            }

        });

    }

    public void showDialog()
    {

    }

    /**
     * 更新全屏按钮
     */
    public void updateFullScreenButton() {
        if (getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            full.setImageResource(R.mipmap.full_screen_icon);
        } else {
            full.setImageResource(R.mipmap.full_screen_icon);
        }
    }


    public void start() {
        pauseImage.setVisibility(View.GONE);
        contollerbar.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        play.setImageResource(R.mipmap.video_stop_btn);
        progressBar.setVisibility(View.VISIBLE);

    }

    public void pause() {
        play.setImageResource(R.mipmap.video_play_btn);
        mVideoView.pause();
        bitmap = mVideoView.getBitmap();
        if (bitmap != null) {
            pauseImage.setImageBitmap(bitmap);
            pauseImage.setVisibility(View.VISIBLE);
        }
    }

    public void onDestory()
    {
        mContext.unregisterReceiver(changeReceiver);
    }
    public void reStart() {
        play.setImageResource(R.mipmap.video_stop_btn);
        mVideoView.start();
        if (bitmap != null) {
            handler.sendEmptyMessageDelayed(PlayStateParams.PAUSE_IMAGE_HIDE, 100);
            bitmap.recycle();
            bitmap = null;
//                        pauseImage.setVisibility(View.GONE);
        }
    }

    public void setShowContoller(boolean isShowContoller) {
        this.isShowContoller = isShowContoller;
        handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
        contollerbar.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
    }

    public int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }


    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    public void setVisiable() {
        show();
    }

    private long setProgress() {
        if (isDragging) {
            return 0;
        }

        long position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        this.duration = duration;
        if (!generateTime(duration).equals(allTime.getText().toString()))
            allTime.setText(generateTime(duration));
        if (seekBar != null) {
            if (duration > 0) {
                long pos = 100L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = mVideoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent);
        }
        String string = generateTime((long) (duration * seekBar.getProgress() * 1.0f / 100));
        time.setText(string);
        return position;
    }


    private VedioIsPause vedioIsPause;

    public interface VedioIsPause {
        void pause(boolean pause);
    }

    public void setPauseImageHide() {
        pauseImage.setVisibility(View.GONE);
    }

    public class PlayGestureListener extends GestureDetector.SimpleOnGestureListener {

        private boolean firstTouch;
        private boolean volumeControl;
        private boolean seek;

        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;
//            handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
            //横屏下拦截事件
            if (getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                return true;
            } else {
                return super.onDown(e);
            }
//            return super.onDown(e);
        }
//        /**
//         * 单击
//         *
//         * @param e
//         * @return
//         */
//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
//            Log.d(TAG,"onSingleTapUp"+isShow);
//            if (isShow) {
//                hide();
//            } else {
//                show(PlayStateParams.TIME_OUT);
//            }
//            return true;
//        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float x = e1.getX() - e2.getX();
            float y = e1.getY() - e2.getY();
            if (firstTouch) {
                seek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl = e1.getX() < view.getMeasuredWidth() * 0.5;
                firstTouch = false;
            }

            if (seek) {
                onProgressSlide(-x / view.getWidth(), e1.getX() / view.getWidth());
            } else {
                float percent = y / view.getHeight();
                if (volumeControl) {
                    onVolumeSlide(percent);
                } else {
                    onBrightnessSlide(percent);
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);


        }


    }


    @Override
    public void hide() {
        Log.d(TAG, "hide");
        if (isShow) {
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            isShow = false;
            handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
            contollerbar.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
        }
    }

    //    public void hide(boolean force) {
//
//        if (force||isShow) {
//            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
//            isShow = false;
//            handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
//            contollerbar.setVisibility(View.GONE);
//            toolbar.setVisibility(View.GONE);
//        }
//
//    }
    @Override
    public boolean isShowing() {
        return isShow;
    }

    @Override
    public void setAnchorView(View view) {

    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {

    }

    @Override
    public void show(int timeout) {
        Log.d(TAG, "show timeout");
//        handler.sendEmptyMessageDelayed(PlayStateParams.SET_VIEW_HIDE, timeout);
        handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
        if (timeout != 0 && mVideoView.isPlaying()) {
            Log.d(TAG, "isplay");
            handler.sendMessageDelayed(handler.obtainMessage(PlayStateParams.SET_VIEW_HIDE), timeout);
        }
    }

    @Override
    public void show() {
        Log.d(TAG, "show");
        if (!isShowContoller)
            return;
        if (!isShow)
            isShow = true;
        progressBar.setVisibility(View.GONE);
        contollerbar.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
        show(PlayStateParams.TIME_OUT);

    }

    @Override
    public void showOnce(View view) {

    }


    /**
     * 手势结束
     */
    private void endGesture() {
        volume = -1;
        brightness = -1f;
        if (newPosition >= 0) {
            handler.removeMessages(PlayStateParams.MESSAGE_SEEK_NEW_POSITION);
            handler.sendEmptyMessage(PlayStateParams.MESSAGE_SEEK_NEW_POSITION);
        }
        handler.removeMessages(PlayStateParams.MESSAGE_HIDE_CONTOLL);
        handler.sendEmptyMessageDelayed(PlayStateParams.MESSAGE_HIDE_CONTOLL, 500);

    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (volume == -1) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0)
                volume = 0;
        }
//        hide();

        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        if (sound_layout.getVisibility() == View.GONE)
            sound_layout.setVisibility(View.VISIBLE);
        // 变更进度条
        int i = (int) (index * 1.0f / mMaxVolume * 100);
        if (i == 0) {
            sound.setImageResource(R.mipmap.sound_mult_icon);
        } else {
            sound.setImageResource(R.mipmap.sound_open_icon);
        }
        sound_seek.setProgress(i);
    }

    /**
     * 滑动跳转
     *
     * @param percent 移动比例
     * @param downPer 按下比例
     */
    private void onProgressSlide(float percent, float downPer) {
        long position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);

        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition = 0;
            delta = -position;
        }
        int showDelta = (int) delta / 1000;
        Log.e("showdelta", ((downPer + percent) * 100) + "");
        if (showDelta != 0) {
            if (seekTxt.getVisibility() == View.GONE)
                seekTxt.setVisibility(View.VISIBLE);
            String current = generateTime(newPosition);
            seekTxt.setText(current + "/" + allTime.getText());
        }
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (brightness < 0) {
            brightness = ((Activity) mContext).getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        Log.d(this.getClass().getSimpleName(), "brightness:" + brightness + ",percent:" + percent);
        WindowManager.LayoutParams lpa = ((Activity) mContext).getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }

        if (brightness_layout.getVisibility() == View.GONE)
            brightness_layout.setVisibility(View.VISIBLE);

        brightness_seek.setProgress((int) (lpa.screenBrightness * 100));
        ((Activity) mContext).getWindow().setAttributes(lpa);

    }


    class ConnectionChangeReceiver extends BroadcastReceiver {
        private  final String TAG = ConnectionChangeReceiver.class.getSimpleName();
        private boolean isWifi;
        private boolean isMobile;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "网络状态改变");
            //获得网络连接服务
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            //获取wifi连接状态
            NetworkInfo.State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            //判断是否正在使用wifi网络
            if (wifi == NetworkInfo.State.CONNECTED) {
                isWifi = true;
            }
            else
                isWifi=false;
            //获取GPRS状态
            NetworkInfo.State  state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            //判断是否在使用GPRS网络
            if (state == NetworkInfo.State.CONNECTED) {
                isMobile = true;
            }
            else
                isMobile=false;
            //如果没有连接成功
            if(!isWifi){

                pause();
                show();
                showDialog();
            }
            else if (!isMobile)
            {
                pause();
                show();

            }
            else
            {
                pause();
                show();
                Toast.makeText(context,"当前网络无连接", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public interface OnErrorListener {
        void onError(int what, int extra);
    }

    private OnErrorListener onErrorListener = new OnErrorListener() {
        @Override
        public void onError(int what, int extra) {
        }
    };


    public interface OnInfoListener {
        void onInfo(int what, int extra);
    }
    private OnInfoListener onInfoListener = new OnInfoListener() {
        @Override
        public void onInfo(int what, int extra) {

        }
    };
}
