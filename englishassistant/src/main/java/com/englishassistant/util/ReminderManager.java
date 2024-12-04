package com.englishassistant.util;

import java.io.*;
import java.awt.*;

import java.awt.TrayIcon.MessageType;
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalTime;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.sound.sampled.*;


/**
 * 学习提醒管理工具类
 * 负责定时提醒功能的实现
 *
 * 主要功能:
 * 1. 提醒管理
 *    - 设置定时提醒
 *    - 显示提醒消息
 *    - 停止提醒
 *    - 更新提醒间隔
 *
 * 2. 系统集成
 *    - 系统托盘支持
 *    - 声音提醒
 *    - 配置保存
 *    - 免打扰模式
 *
 * 特点:
 * - 后台守护进程
 * - 配置持久化
 * - 多种提醒方式
 */
public class ReminderManager {
    private static Timer timer = new Timer(true);
    private static final String REMINDER_TITLE = "学习提醒";
    private static final String SOUND_FILE = "reminder.wav";
    private static final Preferences prefs = Preferences.userNodeForPackage(ReminderManager.class);
    private static TrayIcon trayIcon;
    private static boolean doNotDisturb = false;
    private static ReminderConfig config = new ReminderConfig();
    
    static {
        setupTrayIcon();
    }
    
    // 设置系统托盘图标
    private static void setupTrayIcon() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = new ImageIcon("icons/reminder.png").getImage();
            
            PopupMenu popup = new PopupMenu();
            MenuItem doNotDisturbItem = new MenuItem("免打扰模式");
            doNotDisturbItem.addActionListener(e -> toggleDoNotDisturb());
            popup.add(doNotDisturbItem);
            
            trayIcon = new TrayIcon(image, "学习提醒", popup);
            trayIcon.setImageAutoSize(true);
            
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }
    
    // 设置定时提醒
    public static void scheduleReminder(int intervalHours) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!doNotDisturb) {
                    showReminder();
                }
            }
        }, 0, intervalHours * 60 * 60 * 1000);
        
        // 保存设置
        prefs.putInt("reminderInterval", intervalHours);
    }
    
    // 显示提醒
    private static void showReminder() {
        if (!isWithinReminderTime()) {
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            // 播放提醒音效
            if (config.isSoundEnabled()) {
                playSound();
            }
            
            // 显示系统托盘通知
            if (config.isNotificationEnabled() && trayIcon != null) {
                trayIcon.displayMessage(
                    REMINDER_TITLE,
                    config.getCustomMessage(),
                    MessageType.INFO
                );
            }
            
            // 显示对话框
            Window mainWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
            JOptionPane.showMessageDialog(
                mainWindow,
                config.getCustomMessage(),
                REMINDER_TITLE,
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }
    
    // 播放提醒音效
    private static void playSound() {
        try {
            File soundFile = new File(SOUND_FILE);
            if (soundFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 切换免打扰模式
    private static void toggleDoNotDisturb() {
        doNotDisturb = !doNotDisturb;
        prefs.putBoolean("doNotDisturb", doNotDisturb);
        
        if (trayIcon != null) {
            trayIcon.displayMessage(
                "提醒设置",
                doNotDisturb ? "已开启免打扰模式" : "已关闭免打扰模式",
                MessageType.INFO
            );
        }
    }
    
    // 停止提醒
    public static void stopReminder() {
        if (timer != null) {
            timer.cancel();
            timer = new Timer(true);
        }
    }
    
    // 更新提醒间隔
    public static void updateInterval(int newIntervalHours) {
        stopReminder();
        scheduleReminder(newIntervalHours);
    }
    
    // 加载保存的设置
    public static void loadSettings() {
        int savedInterval = prefs.getInt("reminderInterval", 1); // 默认1小时
        doNotDisturb = prefs.getBoolean("doNotDisturb", false);
        scheduleReminder(savedInterval);
    }
    
    // 获取当前设置
    public static int getCurrentInterval() {
        return prefs.getInt("reminderInterval", 1);
    }
    
    public static boolean isDoNotDisturb() {
        return doNotDisturb;
    }
    
    // 获取配置
    public static ReminderConfig getConfig() {
        return config;
    }
    
    // 更新配置
    public static void updateConfig(ReminderConfig newConfig) {
        config = newConfig;
        
        // 更新提醒设置
        if (config.isDoNotDisturb()) {
            stopReminder();
        } else {
            updateInterval(config.getIntervalHours());
        }
        
        // 保存配置到Preferences
        prefs.putInt("reminderInterval", config.getIntervalHours());
        prefs.putBoolean("doNotDisturb", config.isDoNotDisturb());
        prefs.putBoolean("soundEnabled", config.isSoundEnabled());
        prefs.putBoolean("notificationEnabled", config.isNotificationEnabled());
        prefs.put("customMessage", config.getCustomMessage());
        prefs.put("startTime", config.getStartTime().toString());
        prefs.put("endTime", config.getEndTime().toString());
    }
    
    // 加载配置
    public static void loadConfig() {
        config.setIntervalHours(prefs.getInt("reminderInterval", 1));
        config.setDoNotDisturb(prefs.getBoolean("doNotDisturb", false));
        config.setSoundEnabled(prefs.getBoolean("soundEnabled", true));
        config.setNotificationEnabled(prefs.getBoolean("notificationEnabled", true));
        config.setCustomMessage(prefs.get("customMessage", "该复习单词了!"));
        
        String startTimeStr = prefs.get("startTime", "09:00");
        String endTimeStr = prefs.get("endTime", "22:00");
        config.setStartTime(LocalTime.parse(startTimeStr));
        config.setEndTime(LocalTime.parse(endTimeStr));
        
        // 应用配置
        if (!config.isDoNotDisturb()) {
            scheduleReminder(config.getIntervalHours());
        }
    }
    
    // 检查是否在提醒时间范围内
    private static boolean isWithinReminderTime() {
        LocalTime now = LocalTime.now();
        return !now.isBefore(config.getStartTime()) && !now.isAfter(config.getEndTime());
    }
} 