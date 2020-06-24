package com.bajie.audio.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bajie.audio.R;

/**
 * bajie on 2020/6/23 11:46
 */
public class ActivityHeader extends ConstraintLayout {

    TextView tvTitle;
    TextView tvRightText;
    ImageView ivBack;

    public ActivityHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attributeSet) {
        View inflater = LayoutInflater.from(context).inflate(R.layout.header_activity, this);
        tvTitle = inflater.findViewById(R.id.tv_title);
        tvRightText = inflater.findViewById(R.id.tv_right_text);
        ivBack = inflater.findViewById(R.id.iv_back);

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.HeaderTitle);
        String sTitle = typedArray.getString(R.styleable.HeaderTitle_Title);
        String sRightText = typedArray.getString(R.styleable.HeaderTitle_RightText);

        tvTitle.setText(sTitle);
        tvRightText.setText(sRightText);
    }

    public void setBackOnClickListener(OnClickListener l) {
        ivBack.setOnClickListener(l);
    }

    public void setRightTextClickListener(OnClickListener l) {
        tvRightText.setOnClickListener(l);
    }

}
