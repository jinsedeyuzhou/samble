package com.github.sample;

import android.app.Application;

import com.github.jinsedeyuzhou.PlayerApplication;

/**
 * Created by Berkeley on 2/13/17.
 */

public class VIdeoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PlayerApplication.initApp(this);
    }
}
