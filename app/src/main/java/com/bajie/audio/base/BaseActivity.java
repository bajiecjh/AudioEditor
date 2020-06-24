package com.bajie.audio.base;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;


import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("bybajie BaseActivity onCreate");
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT);
    }

    @Override
    public void setContentView(int layoutResID) {

        System.out.println("bybajie BaseActivity setContentView");
        Toast.makeText(this, "setContentView", Toast.LENGTH_SHORT);
        super.setContentView(layoutResID);
        mUnbinder = ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        if(mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroy();
    }

    protected abstract void initView();
}
