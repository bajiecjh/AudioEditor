package com.bajie.audio.entity;

import java.util.List;

/**
 * bajie on 2020/6/7 23:08
 */
public class MainViewEntity {
    public static final int ITEM_FIRST = 0;

    public static final int ITEM_SECOND = 1;

    public static final int ACTIOIN_ADD_WATERMARK = 0;

    public int type;
    public List<MainViewItemEntity> mainViewEntities;

    public MainViewEntity(int type) {
        this.type = type;
    }

    public MainViewEntity(int type, List<MainViewItemEntity> mainViewEntities) {
        this.type = type;
        this.mainViewEntities = mainViewEntities;
    }

    public static class MainViewItemEntity {
        public int icon;
        public String title;
        public String subtitle;
        public int action;

        public MainViewItemEntity(int icon, String title, String subtitle, int action) {
            this.icon = icon;
            this.title = title;
            this.subtitle = subtitle;
            this.action = action;
        }
    }
}
