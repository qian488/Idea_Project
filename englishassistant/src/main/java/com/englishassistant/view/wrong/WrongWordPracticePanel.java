package com.englishassistant.view.wrong;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import com.englishassistant.model.WrongWord;
import com.englishassistant.view.common.StyledPanel;

/**
 * 错题练习面板类
 * 提供错题练习的核心功能界面
 * 
 * 功能说明:
 * 1. 练习流程
 *    - showCurrentWord(): 显示当前练习词
 *    - checkAnswer(): 检查答案正确性
 *    - nextWord(): 进入下一个单词
 *    - notifyPracticeCompleted(): 通知练习完成
 * 
 * 2. 状态管理
 *    - setWrongWords(): 设置练习词列表
 *    - addPracticeListener(): 添加练习监听器
 *    - 更新掌握状态和错误次数
 * 
 * 3. 界面组件
 *    - 单词显示区: 显示中文提示
 *    - 答案输入框: 输入英文答案
 *    - 按钮区: 检查和下一题按钮
 *    - 结果反馈: 答案正确性提示
 * 
 * @author Your Name
 */
public class WrongWordPracticePanel extends StyledPanel {
    private List<WrongWord> wrongWords;
    private int currentIndex = 0;
    private JLabel wordLabel;
    private JTextField answerField;
    private JButton submitButton;
    private JButton nextButton;
    private List<WrongWordPracticeListener> listeners = new ArrayList<>();
    
    public WrongWordPracticePanel() {
        this.wrongWords = new ArrayList<>();
        initComponents();
    }
    
    private void initComponents() {
        // 使用 BorderLayout 作为主布局
        setLayout(new BorderLayout());
        
        // 创建一个居中的主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 单词显示标签
        wordLabel = new JLabel("");
        wordLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 36));
        wordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 答案输入框面板
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        answerField = new JTextField(20);
        answerField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 24));
        answerField.setPreferredSize(new Dimension(400, 40));
        inputPanel.add(answerField);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitButton = createStyledButton("检查");
        nextButton = createStyledButton("下一个");
        
        submitButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        nextButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        
        submitButton.setPreferredSize(new Dimension(100, 35));
        nextButton.setPreferredSize(new Dimension(100, 35));
        
        buttonPanel.add(submitButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(nextButton);
        
        // 添加组件到主面板
        mainPanel.add(Box.createVerticalGlue());  // 顶部弹性空间
        mainPanel.add(wordLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalGlue());  // 底部弹性空间
        
        // 将主面板添加到中央
        add(mainPanel, BorderLayout.CENTER);
        
        // 添加事件监听
        submitButton.addActionListener(e -> checkAnswer());
        nextButton.addActionListener(e -> nextWord());
        answerField.addActionListener(e -> {
            if (submitButton.isEnabled()) {
                checkAnswer();
            } else if (nextButton.isEnabled()) {
                nextWord();
            }
        });
        
        // 初始化按钮状态
        submitButton.setEnabled(true);
        nextButton.setEnabled(false);
        
        // 添加大小调整监听器
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // 确保输入框宽度不超过面板宽度
                int maxWidth = getWidth() - 100;  // 留出边距
                int width = Math.min(maxWidth, 400);  // 最大宽度400
                answerField.setPreferredSize(new Dimension(width, 40));
                inputPanel.revalidate();
            }
        });
    }
    
    public void setWrongWords(List<WrongWord> wrongWords) {
        this.wrongWords = new ArrayList<>(wrongWords);
        this.currentIndex = 0;
        showCurrentWord();
    }
    
    private void showCurrentWord() {
        if (currentIndex < wrongWords.size()) {
            WrongWord wrongWord = wrongWords.get(currentIndex);
            wordLabel.setText(wrongWord.getWord().getTranslation());  // 显示中文，让用户输入英文
            answerField.setText("");
            answerField.setEnabled(true);
            answerField.requestFocus();
        }
    }
    
    private void checkAnswer() {
        if (currentIndex >= wrongWords.size()) return;
        
        WrongWord wrongWord = wrongWords.get(currentIndex);
        String answer = answerField.getText().trim();
        String correctWord = wrongWord.getWord().getWord();
        
        if (answer.equalsIgnoreCase(correctWord)) {
            JOptionPane.showMessageDialog(this,
                "正确！",
                "结果",
                JOptionPane.INFORMATION_MESSAGE);
            wrongWord.setMastered(true);
        } else {
            JOptionPane.showMessageDialog(this,
                String.format("错误！\n你的答案：%s\n正确答案：%s", 
                    answer, correctWord),
                "结果",
                JOptionPane.ERROR_MESSAGE);
            wrongWord.incrementWrongCount();
        }
        
        submitButton.setEnabled(false);
        nextButton.setEnabled(true);
        
        if (currentIndex >= wrongWords.size() - 1) {
            nextButton.setText("完成");
        }
    }
    
    private void nextWord() {
        currentIndex++;
        if (currentIndex >= wrongWords.size()) {
            notifyPracticeCompleted();
            JOptionPane.showMessageDialog(this,
                "练习完成！",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
            // 获取当前面板所在的窗口并关闭
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JDialog) {
                ((JDialog) window).dispose();
            }
        } else {
            answerField.setText("");  // 清空输入框
            submitButton.setEnabled(true);  // 重新启用提交按钮
            nextButton.setEnabled(false);   // 禁用下一题按钮
            showCurrentWord();
            answerField.requestFocus();     // 让输入框获得焦点
        }
    }
    
    public void addPracticeListener(WrongWordPracticeListener listener) {
        listeners.add(listener);
    }
    
    private void notifyPracticeCompleted() {
        for (WrongWordPracticeListener listener : listeners) {
            listener.onPracticeCompleted(wrongWords);
        }
    }
} 