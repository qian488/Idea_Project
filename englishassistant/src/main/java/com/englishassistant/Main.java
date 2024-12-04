package com.englishassistant;

import com.englishassistant.util.DatabaseUtil;
import com.englishassistant.util.ThemeManager;
import com.englishassistant.view.MainWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 在 EDT 中执行所有 Swing 相关操作
        SwingUtilities.invokeLater(() -> {
            try {
                // 初始化主题管理器
                ThemeManager.setupDefaultTheme();
                
                // 初始化数据库
                DatabaseUtil.initDatabase();
                
                // 创建并显示主窗口
                MainWindow mainWindow = new MainWindow();
                mainWindow.setVisible(true);
                
            } catch (Exception e) {
                System.err.println("程序启动失败: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "程序启动失败: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
} 