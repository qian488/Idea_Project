package com.englishassistant.util;

import java.io.*;
import java.time.*;

/**
 * 提醒配置类
 * 用于存储和管理提醒相关的配置信息
 *
 * 主要功能:
 * 1. 时间设置
 *    - 提醒间隔
 *    - 开始时间
 *    - 结束时间
 *
 * 2. 提醒方式
 *    - 声音提醒
 *    - 通知提醒
 *    - 自定义消息
 *    - 免打扰模式
 *
 * 特点:
 * - 可序列化配置
 * - 默认值支持
 * - 灵活的时间控制
 */
public class ReminderConfig implements Serializable {
    private int intervalHours;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean soundEnabled;
    private boolean notificationEnabled;
    private String customMessage;
    private boolean doNotDisturb;
    
    // 默认配置
    public ReminderConfig() {
        this.intervalHours = 1;
        this.startTime = LocalTime.of(9, 0);
        this.endTime = LocalTime.of(22, 0);
        this.soundEnabled = true;
        this.notificationEnabled = true;
        this.customMessage = "该复习单词了!";
        this.doNotDisturb = false;
    }
    
    // Getters and Setters
    public int getIntervalHours() { return intervalHours; }
    public void setIntervalHours(int intervalHours) { this.intervalHours = intervalHours; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean soundEnabled) { this.soundEnabled = soundEnabled; }
    
    public boolean isNotificationEnabled() { return notificationEnabled; }
    public void setNotificationEnabled(boolean notificationEnabled) { this.notificationEnabled = notificationEnabled; }
    
    public String getCustomMessage() { return customMessage; }
    public void setCustomMessage(String customMessage) { this.customMessage = customMessage; }
    
    public boolean isDoNotDisturb() { return doNotDisturb; }
    public void setDoNotDisturb(boolean doNotDisturb) { this.doNotDisturb = doNotDisturb; }
} 