package com.bajie.audio.utils;

/**
 * bajie on 2020/7/7 17:50
 */
public class TimeUtils {

    /**毫秒转mm:ss*/
    public static String formatTime(int time) {
        int minute = 0;
        int second = 0;
        if(time < 0) {
            return "00:00";
        }
        minute = time / 60000;
        second = (time - minute * 60000) / 1000;
        return unitFormat(minute) + ":" + unitFormat(second);
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if(i >= 0 && i < 10) {
            retStr = "0" + i;
        } else {
            retStr = i + "";
        }
        return retStr;
    }
}
