package com.bajie.audio.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bajie.audio.R;
import com.bajie.audio.base.BaseAdapter;
import com.bajie.audio.base.BaseHolder;
import com.bajie.audio.entity.MainViewEntity;

import java.util.List;

/**
 * bajie on 2020/6/7 23:11
 */
public class MainPageAdapter extends BaseAdapter<MainViewEntity> {

    private Context mContext;

    public MainPageAdapter(List<MainViewEntity> data) {
        super(data);
    }

    public MainPageAdapter(Context context, List<MainViewEntity> data) {
        super(data);
        mContext = context;
    }

    public interface ActionClickListener {
        void onActionclick(int action);
    }

    private ActionClickListener actionClickListener;

    public void setActionClickListener(ActionClickListener actionClickListener) {
        this.actionClickListener = actionClickListener;
    }

    @Override
    protected void convert(BaseHolder helper, MainViewEntity item) {
        System.out.println("bybajie MainPageAdapter convert item.type=" +item.type);
        switch (item.type) {
            case MainViewEntity.ITEM_FIRST:
                showTestView(helper, item);
                break;
            case MainViewEntity.ITEM_SECOND:
                showItems(helper, item);
                break;
        }
    }

    private void showTestView(BaseHolder holder, MainViewEntity item) {

    }

    private void showItems(BaseHolder holder, MainViewEntity item) {
        RecyclerView recyclerView = holder.getView(R.id.recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        BaseAdapter adapter = new BaseAdapter<MainViewEntity.MainViewItemEntity>(R.layout.item_main_grid_sub, item.mainViewEntities) {
            @Override
            protected void convert(BaseHolder helper, MainViewEntity.MainViewItemEntity item) {

            }

            @Override
            public void onBindViewHolder(BaseHolder holder, int position) {
                MainViewEntity.MainViewItemEntity item = data.get(position);
                super.onBindViewHolder(holder, position);
                holder.setImageRes(R.id.iv_icon, item.icon);
                holder.setText(R.id.tv_title, item.title);
                holder.setText(R.id.tv_subtitle, item.subtitle);
                holder.getView(R.id.content_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (item.action) {
                            case MainViewEntity.ACTIOIN_ADD_WATERMARK:
                                if(actionClickListener != null) {
                                    actionClickListener.onActionclick(MainViewEntity.ACTIOIN_ADD_WATERMARK);
                                }
                                break;
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public int getLayoutRes(int viewType) {
        System.out.println("bybajie MainPageAdapter getLayoutRes viewType=" +viewType);
        switch (viewType) {
            case MainViewEntity.ITEM_FIRST:
                return R.layout.item_main_test;
            case MainViewEntity.ITEM_SECOND:
                return R.layout.item_main_grid;
        }
        return -1;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }
}
