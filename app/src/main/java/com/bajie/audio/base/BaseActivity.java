package com.bajie.audio.base;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;


import com.bajie.audio.view.widget.dialog.LoaddingDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder mUnbinder;

    LoaddingDialog loaddingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("bybajie BaseActivity onCreate");
//        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT);
    }

    @Override
    public void setContentView(int layoutResID) {

        System.out.println("bybajie BaseActivity setContentView");
        Toast.makeText(this, "setContentView", Toast.LENGTH_SHORT);
        super.setContentView(layoutResID);
        mUnbinder = ButterKnife.bind(this);
        initView();
    }

    public void showLoadingDialog(String message) {
        if(loaddingDialog == null) {
            loaddingDialog = new LoaddingDialog(this, message);
        }
        if(!loaddingDialog.isShowing()) {
            loaddingDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        if(loaddingDialog != null && loaddingDialog.isShowing()) {
            loaddingDialog.dismiss();
        }
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
