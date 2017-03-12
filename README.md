## 视频播放器
##introduction##
 
该视频播放器基于Bilibili的开源项目ijkplayer做的，是基于其demo做的。主要做了视频播放器的界面的定制，基于ijkplayer项目进行的播放器界面 UI 封装。

目前主要集成了一下主要功能：

* 触屏控制，竖直方向左边控制亮度，右边控制声音，水平方向控制播放进度；
* 竖屏和横屏的切换，可以手动或者重力感应来切换竖横屏功能；
* 沉浸式全屏播放，隐藏状态栏。
* 视频宽高比例设置，包括16:9、4:3、视频内嵌填充界面和填充屏幕等4种，
* 截屏，跑马灯标题，锁屏，并对用户体验效果做了部分优化。
* 解决了demo中部分问题。

##Screenshot##

![player](./player.png)   
 
![player](./playerone.png)  
	
##Usage##

 **step1**    

	将库导入到项目中 build 

		dependencies {
		compile fileTree(dir: 'libs', include: ['*.jar'])
		testCompile 'junit:junit:4.12'
		compile 'com.android.support:appcompat-v7:24.1.0'
		compile project(":videoplayer")
		}
	
	setting.gradle

		include ':videoplayer'
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
		
		public class VIdeoApplication extends Application {
		    @Override
		    public void onCreate() {
		        super.onCreate();
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
		    };
4. 配置生命周期方法,为了让播放器同步Activity生命周期


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

##Proguard

根据你的混淆器配置和使用，您可能需要在你的 proguard 文件内配置以下内容：

		-keep com.dou361.ijkplayer.** {
		*;
		-dontwarn com.dou361.ijkplayer.**;
		}

##Thanks
[GSYVideoPlayer](https://github.com/CarGuo/GSYVideoPlayer)  
[ijkplayer](https://github.com/Bilibili/ijkplayer)  
[GiraffePlayer](https://github.com/tcking/GiraffePlayer)  
and so and
