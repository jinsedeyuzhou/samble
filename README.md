## 视频播放器
## introduction ##
 
该视频播放器基于Bilibili的开源项目ijkplayer做的，是基于其demo做的。主要做了视频播放器的界面的定制，基于ijkplayer项目进行的播放器界面 UI 封装。

目前主要集成了一下主要功能：

* 触屏控制，竖直方向左边控制亮度，右边控制声音，水平方向控制播放进度；
* 竖屏和横屏的切换，可以手动或者重力感应来切换竖横屏功能；
* 沉浸式全屏播放，隐藏状态栏。
* 视频宽高比例设置，包括16:9、4:3、视频内嵌填充界面和填充屏幕等4种，
* 截屏，跑马灯标题，锁屏，并对用户体验效果做了部分优化。
* 解决了demo中部分问题。

## Screenshot

![player](./player.png)   
 
![player](./playerone.png)  
	
## Usage

 **step1**    

	将库导入到项目中 build 

		dependencies {
		compile fileTree(dir: 'libs', include: ['*.jar'])
		testCompile 'junit:junit:4.12'
		compile 'com.android.support:appcompat-v7:24.1.0'
		compile project(":viewplay")
		}
	
	setting.gradle

		include ':viewplay'
**step2**
	 
1. Androidmanifest.xml  

		  <application
		        android:name=".VIdeoApplication"
		        android:allowBackup="true"
		        android:icon="@mipmap/ic_launcher"
		        android:label="@string/app_name"
		        android:supportsRtl="true"
		        android:theme="@style/AppTheme">
		        <activity android:name=".MainActivity"
				//配置 configChanges
		            android:configChanges="keyboardHidden|orientation|screenSize"
		            android:screenOrientation="sensor"
		            android:theme="@style/Theme.AppCompat.NoActionBar"
		            >
		            <intent-filter>
		                <action android:name="android.intent.action.MAIN" />
		
		                <category android:name="android.intent.category.LAUNCHER" />
		            </intent-filter>
		        </activity>
		        <activity android:name=".list.ListViewActivity"/>
		    </application>
		//在自定义application
		public class VIdeoApplication extends Application {
		    @Override
		    public void onCreate() {
		        super.onCreate();
			//初始化player application
		        PlayerApplication.initApp(this);
		    }
		}

2. 布局  

	    <com.github.jinsedeyuzhou.VPlayPlayer
	        android:id="@+id/fl_content"
	        android:layout_width="match_parent"
	        android:layout_height="210dp"/>
3. MainActivity.java

		  private String url = "http://gslb.miaopai.com/stream/4YUE0MlhLclpX3HIeA273g__.mp4?yx=&refer=weibo_app";
		    private VPlayPlayer vp;
		
		
		    @Override
		    protected void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.activity_main);
		        vPlayer = (VPlayPlayer) findViewById(R.id.fl_content)；
		        vPlayer.setShowNavIcon(true);
		        vPlayer.setTitle(url);
		        vPlayer.play(url);
		    };
4. 配置生命周期方法,为了让播放器同步Activity生命周期

	
	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    //系统音量键控制，如果不写会调用系统的音量键，在视频列表的话尽量不要写
	        if (vPlayPlayer != null) {
                if (vPlayPlayer.onKeyDown(keyCode, event))
                    return true;
          	  }
	        return super.onKeyDown(keyCode, event);
	    }
	
	
	    @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
		//横竖屏切换不改变Activity 生命周期
	        if (vPlayer != null)
	            vPlayer.onChanged(newConfig);
	    }
	
	    @Override
	    protected void onResume() {
	        super.onResume();
		//可见时再次播放，根据需要是否开启暂停后重新进入自动播放
	        if (vPlayer != null)
	            vPlayer.onResume();
	    }
	
	    @Override
	    protected void onPause() {
	        super.onPause();
		//切换后台时暂停播放，
	        if (vPlayer != null)
	            vPlayer.onPause();
	
	    }
	
	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        if (vPlayer != null) {
	            if (vPlayer.getParent() != null)
               		 ((ViewGroup) vPlayer.getParent()).removeAllViews();
           		 vPlayer.onDestory();
            		 vPlayer = null;
	        }
	       
	    }

## Proguard

根据你的混淆器配置和使用，您可能需要在你的 proguard 文件内配置以下内容：

	-keep class tv.danmaku.ijk.media.** { *; }
	-dontwarn tv.danmaku.ijk.**

## Thanks
[GSYVideoPlayer](https://github.com/CarGuo/GSYVideoPlayer)  
[ijkplayer](https://github.com/Bilibili/ijkplayer)  
[GiraffePlayer](https://github.com/tcking/GiraffePlayer)  
and so and
## ISSUE
**FFMPEG bug：**  
1. IJKPLAY有一个问题，有人已经提过，不过目前还未解决，就是某些短小的视频会无法seekTo，说是FFMEPG的问题  
2. 快进到某个位置会回退几个关键帧。
3. 别开硬解码

## About Author
qq:2315813288
email:jinsedeyuzhou@sina.com

## License

**Copyright (C) dou361, The Framework Open Source Project**

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

(Frequently Asked Questions)FAQ
##Bugs Report and Help

If you find any bug when using project, please report here. Thanks for helping us building a better one.
