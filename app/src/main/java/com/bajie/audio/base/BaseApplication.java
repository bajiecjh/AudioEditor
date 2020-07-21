package com.bajie.audio.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by cj on 2017/8/6.
 *
 */

public class BaseApplication extends Application {
    private static Context mContext;



    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Log.e("thread","  线程值  "+ Thread.currentThread());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    public static Context getContext() {
        return mContext;
    }
}
