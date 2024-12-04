package com.englishassistant.view.test;

import java.awt.*;
import javax.swing.*;

import com.englishassistant.view.common.StyledPanel;

/**
 * 测试中心面板类
 * 作为测试功能的主容器，管理所有测试相关的功能模块
 * 
 * 功能说明:
 * 1. 面板管理
 *    - testPanel: 单词测试面板，提供测试功能
 *    - testHistoryPanel: 历史记录面板，显示测试历史
 * 
 * 2. 面板切换方法
 *    - showTest(): 切换到测试面板
 *    - showHistory(): 切换到历史记录面板并刷新数据
 * 
 * 3. 界面实现
 *    - 使用CardLayout实现面板切换
 *    - 继承自StyledPanel保持统一风格
 *    - 默认显示测试面板
 * 
 * 实现细节:
 * - 使用CardLayout管理子面板
 * - 提供面板切换的公共接口
 * - 自动刷新历史记录数据
 * 
 * @author Your Name
 */
public class TestCenter extends StyledPanel {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final TestPanel testPanel;
    private final TestHistoryPanel testHistoryPanel;
    
    public TestCenter() {
        // 初始化布局
        setLayout(new BorderLayout());
        
        // 初始化内容面板
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // 初始化子面板
        testPanel = new TestPanel();
        testHistoryPanel = new TestHistoryPanel();
        
        // 添加面板到卡片布局
        contentPanel.add(testPanel, "test");
        contentPanel.add(testHistoryPanel, "history");
        
        // 布局
        add(contentPanel, BorderLayout.CENTER);
        
        // 默认显示测试面板
        cardLayout.show(contentPanel, "test");
    }
    
    public void showTest() {
        cardLayout.show(contentPanel, "test");
    }
    
    public void showHistory() {
        cardLayout.show(contentPanel, "history");
        testHistoryPanel.loadHistory();
    }
} 