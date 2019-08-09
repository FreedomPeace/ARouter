package com.example.arouter;

import android.app.Application;

import com.example.arouter_api.launcher.ARouter;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.init(this);
    }
}
