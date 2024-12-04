package com.englishassistant.view.dialog;

import java.awt.*;
import java.time.*;
import java.util.*;
import javax.swing.*;

import com.englishassistant.util.ReminderConfig;
import com.englishassistant.util.ReminderManager;

/**
 * 提醒配置对话框
 * 用于设置和管理提醒相关的配置选项
 *
 * 主要功能:
 * 1. 时间设置
 *    - 提醒间隔设置
 *    - 开始时间设置
 *    - 结束时间设置
 *
 * 2. 提醒选项
 *    - 声音提醒开关
 *    - 桌面通知开关
 *    - 免打扰模式
 *    - 自定义消息
 *
 * 3. 配置管理
 *    - 加载现有配置
 *    - 保存配置更改
 *    - 配置验证
 *
 * 特点:
 * - 直观的界面布局
 * - 实时配置预览
 * - 完整的配置选项
 */
public class ReminderConfigDialog extends JDialog {
    private final ReminderConfig config;
    private JSpinner intervalSpinner;
    private JSpinner startTimeSpinner;
    private JSpinner endTimeSpinner;
    private JCheckBox soundCheckBox;
    private JCheckBox notificationCheckBox;
    private JTextField messageField;
    private JCheckBox doNotDisturbCheckBox;
    
    public ReminderConfigDialog(Frame owner) {
        super(owner, "提醒设置", true);
        this.config = ReminderManager.getConfig();
        initComponents();
        loadConfig();
        setLocationRelativeTo(owner);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // 创建设置面板
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 提醒间隔设置
        gbc.gridx = 0; gbc.gridy = 0;
        settingsPanel.add(new JLabel("提醒间隔(小时):"), gbc);
        gbc.gridx = 1;
        intervalSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
        settingsPanel.add(intervalSpinner, gbc);
        
        // 时间范围设置
        gbc.gridx = 0; gbc.gridy = 1;
        settingsPanel.add(new JLabel("开始时间:"), gbc);
        gbc.gridx = 1;
        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));
        settingsPanel.add(startTimeSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        settingsPanel.add(new JLabel("结束时间:"), gbc);
        gbc.gridx = 1;
        endTimeSpinner = new JSpinner(new SpinnerDateModel());
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));
        settingsPanel.add(endTimeSpinner, gbc);
        
        // 其他选项
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        soundCheckBox = new JCheckBox("启用声音提醒");
        settingsPanel.add(soundCheckBox, gbc);
        
        gbc.gridy = 4;
        notificationCheckBox = new JCheckBox("启用桌面通知");
        settingsPanel.add(notificationCheckBox, gbc);
        
        gbc.gridy = 5;
        doNotDisturbCheckBox = new JCheckBox("免打扰模式");
        settingsPanel.add(doNotDisturbCheckBox, gbc);
        
        gbc.gridy = 6; gbc.gridwidth = 1;
        settingsPanel.add(new JLabel("提醒消息:"), gbc);
        gbc.gridx = 1;
        messageField = new JTextField(20);
        settingsPanel.add(messageField, gbc);
        
        add(settingsPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> saveConfig());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
    }
    
    private void loadConfig() {
        // 加载提醒间隔
        intervalSpinner.setValue(config.getIntervalHours());
        
        // 加载时间范围
        Calendar calendar = Calendar.getInstance();
        
        // 设置开始时间
        calendar.set(Calendar.HOUR_OF_DAY, config.getStartTime().getHour());
        calendar.set(Calendar.MINUTE, config.getStartTime().getMinute());
        startTimeSpinner.setValue(calendar.getTime());
        
        // 设置结束时间
        calendar.set(Calendar.HOUR_OF_DAY, config.getEndTime().getHour());
        calendar.set(Calendar.MINUTE, config.getEndTime().getMinute());
        endTimeSpinner.setValue(calendar.getTime());
        
        // 加载其他选项
        soundCheckBox.setSelected(config.isSoundEnabled());
        notificationCheckBox.setSelected(config.isNotificationEnabled());
        doNotDisturbCheckBox.setSelected(config.isDoNotDisturb());
        messageField.setText(config.getCustomMessage());
    }
    
    private void saveConfig() {
        // 保存提醒间隔
        config.setIntervalHours((Integer) intervalSpinner.getValue());
        
        // 保存时间范围
        Date startDate = (Date) startTimeSpinner.getValue();
        Date endDate = (Date) endTimeSpinner.getValue();
        Calendar calendar = Calendar.getInstance();
        
        // 设置开始时间
        calendar.setTime(startDate);
        config.setStartTime(LocalTime.of(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        ));
        
        // 设置结束时间
        calendar.setTime(endDate);
        config.setEndTime(LocalTime.of(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        ));
        
        // 保存其他选项
        config.setSoundEnabled(soundCheckBox.isSelected());
        config.setNotificationEnabled(notificationCheckBox.isSelected());
        config.setDoNotDisturb(doNotDisturbCheckBox.isSelected());
        config.setCustomMessage(messageField.getText());
        
        // 更新配置
        ReminderManager.updateConfig(config);
        dispose();
    }
} 