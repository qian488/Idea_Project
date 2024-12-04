package com.englishassistant.view.common;

import java.awt.*;
import javax.swing.*;

/**
 * 样式化面板基类
 * 为所有面板提供统一的样式和常用组件创建方法
 *
 * 主要功能:
 * 1. 基础样式设置
 *    - 统一内边距(PADDING = 10)
 *    - 统一字体设置
 *    - 统一边框样式
 *
 * 2. 组件创建工具
 *    - 创建统一样式按钮
 *    - 创建统一样式文本框
 *    - 创建统一样式标签
 *    - 创建统一样式文本区域
 *
 * 3. 边框管理
 *    - 添加带标题边框
 *    - 设置内边距
 *    - 设置边框样式
 *
 * 特点:
 * - 统一的字体(Microsoft YaHei)
 * - 统一的组件样式
 * - 可复用的工具方法
 */
public class StyledPanel extends JPanel {
    protected static final int PADDING = 10;
    protected static final Font TITLE_FONT = new Font("Microsoft YaHei", Font.BOLD, 14);
    protected static final Font CONTENT_FONT = new Font("Microsoft YaHei", Font.PLAIN, 13);
    
    public StyledPanel() {
        setupPanel();
    }
    
    /**
     * 设置面板的基本样式
     * - 添加统一的内边距
     * - 设置默认字体
     */
    protected void setupPanel() {
        setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        setFont(CONTENT_FONT);
    }
    
    /**
     * 创建统一样式的按钮
     * - 使用统一字体
     * - 关闭焦点绘制
     */
    protected JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(CONTENT_FONT);
        button.setFocusPainted(false);
        return button;
    }
    
    /**
     * 创建统一样式的文本框
     * - 使用统一字体
     * - 指定列数
     */
    protected JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(CONTENT_FONT);
        return textField;
    }
    
    /**
     * 创建统一样式的标签
     * - 使用统一字体
     */
    protected JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(CONTENT_FONT);
        return label;
    }
    
    /**
     * 添加带标题的边框
     * - 使用标题字体
     * - 添加内边距
     * - 使用浅灰色边框
     */
    protected void addStyledBorder(String title) {
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                title,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                TITLE_FONT
            ),
            BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)
        ));
    }
    
    protected JTextArea createStyledTextArea(int rows, int cols) {
        JTextArea textArea = new JTextArea(rows, cols);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }
} 