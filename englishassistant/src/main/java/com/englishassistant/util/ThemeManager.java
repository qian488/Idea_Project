package com.englishassistant.util;

import java.awt.*;
import javax.swing.*;

/**
 * 主题管理工具类
 * 负责管理应用程序的界面主题
 *
 * 主要功能:
 * 1. 主题切换
 *    - 日间/夜间模式切换
 *    - 主题初始化
 *    - UI更新
 *
 * 2. 界面管理
 *    - 组件样式更新
 *    - 背景色管理
 *    - 主题状态跟踪
 *
 * 特点:
 * - FlatLaf主题支持
 * - 平滑切换效果
 * - 全局主题统一
 */
public class ThemeManager {
    private static boolean isDarkMode = false;
    private static JButton themeButton;
    
    public static void initialize(JButton button) {
        themeButton = button;
        updateThemeButton();
    }
    
    public static void setupDefaultTheme() {
        try {
            // 使用 FlatLaf 的默认亮色主题
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            isDarkMode = false;
        } catch (Exception e) {
            System.err.println("无法设置默认主题: " + e.getMessage());
            // 如果设置失败，尝试使用系统默认主题
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("无法设置系统默认主题: " + ex.getMessage());
            }
        }
    }
    
    public static void toggleTheme() {
        try {
            if (isDarkMode) {
                // 切换到亮色主题
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            } else {
                // 切换到暗色主题
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
            }
            isDarkMode = !isDarkMode;
            updateThemeButton();
            updateUI();
        } catch (Exception e) {
            System.err.println("切换主题失败: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "切换主题失败: " + e.getMessage(), 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void updateThemeButton() {
        if (themeButton != null) {
            themeButton.setText(isDarkMode ? "日间模式" : "夜间模式");
        }
    }
    
    private static void updateUI() {
        // 更新所有窗口的UI
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
            // 更新所有面板的背景色
            updatePanelBackgrounds(window);
        }
    }
    
    private static void updatePanelBackgrounds(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(UIManager.getColor("Panel.background"));
            }
            if (comp instanceof Container) {
                updatePanelBackgrounds((Container) comp);
            }
        }
    }
    
    public static boolean isDarkMode() {
        return isDarkMode;
    }
} 