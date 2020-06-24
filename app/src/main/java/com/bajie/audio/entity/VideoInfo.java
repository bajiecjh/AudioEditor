package com.bajie.audio.entity;

/**
 * bajie on 2020/6/19 15:56
 */
public class VideoInfo {
    public String path; // 路径
    public int rotation; // 旋转角度
    public int width; // 宽
    public int height;  // 高
    public int bitRate; // 比特率
    public int frameRate;   // 帧率
    public int frameInterval;   // 关键帧间隔
    public int duration;    // 时长

    public int expWidth;    // 期望宽度
    public int expHeight;   // 期望高度
    public int cutPoint;    // 剪切的开始点
    public int cutDuratioin;    // 剪切的时长
}
