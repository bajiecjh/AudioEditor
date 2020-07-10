package com.bajie.audio.view.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bajie.audio.R;

/**
 * bajie on 2020/7/6 17:17
 */
public class LoaddingDialog extends Dialog {
    String message;
    public LoaddingDialog(@NonNull Context context) {
        super(context, R.style.style_dialog_base);
    }

    public LoaddingDialog(@NonNull Context context, String message) {
        super(context);
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loadding);
        if(message != null) {
            ((TextView)findViewById(R.id.tv_loading_tip)).setText(message);
        }
    }
}
