package com.bajie.audio.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2017/11/28 0028.
 */

public class BaseHolder extends RecyclerView.ViewHolder {

    private final SparseArray<View> views;
    private final Context context;
    private View convertView;

    public BaseHolder(Context context, View itemView) {
        super(itemView);
        views = new SparseArray<>();
        this.context = context;
        this.convertView = itemView;
    }

    public  <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public BaseHolder setText(int viewId, CharSequence value) {
        TextView view = getView(viewId);
        view.setText(value);
        return this;
    }

    public BaseHolder setTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        view.setTextColor(color);
        return this;
    }

    public BaseHolder setImageRes(int viewId, int res) {
        ImageView view = getView(viewId);
        if(view == null) {
            System.out.println("bybajie: setImageRes view is null");
        }
        view.setImageResource(res);
        return this;
    }


}
