package com.englishassistant.view.wrong;

import java.awt.*;
import java.util.List;
import javax.swing.*;

import com.englishassistant.view.common.StyledPanel;
import com.englishassistant.model.WrongWord;

/**
 * 错题本中心面板类
 * 作为错题本功能的主容器，管理所有错题相关的功能模块
 * 
 * 功能说明:
 * 1. 面板管理
 *    - listPanel: 错题列表面板，显示所有错题
 *    - practicePanel: 错题练习面板，提供练习功能
 * 
 * 2. 面板切换方法
 *    - showList(): 显示错题列表并刷新数据
 *    - showPractice(): 切换到练习模式并加载选中错题
 * 
 * 3. 界面实现
 *    - 使用CardLayout实现面板切换
 *    - 继承自StyledPanel保持统一风格
 *    - 默认显示错题列表面板
 * 
 * @author Your Name
 */
public class WrongWordCenter extends StyledPanel {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final WrongWordPanel listPanel;
    private final WrongWordPracticePanel practicePanel;
    
    public WrongWordCenter() {
        // 初始化布局
        setLayout(new BorderLayout());
        
        // 初始化内容面板
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // 初始化子面板
        listPanel = new WrongWordPanel();
        practicePanel = new WrongWordPracticePanel();
        
        // 添加面板到卡片布局
        contentPanel.add(listPanel, "list");
        contentPanel.add(practicePanel, "practice");
        
        // 布局
        add(contentPanel, BorderLayout.CENTER);
        
        // 默认显示列表面板
        cardLayout.show(contentPanel, "list");
    }
    
    public void showList() {
        cardLayout.show(contentPanel, "list");
        listPanel.loadWrongWords();
    }
    
    public void showPractice() {
        List<WrongWord> selectedWords = listPanel.getSelectedWrongWords();
        if (selectedWords.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "请先在错题列表中选择要练习的单词",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(contentPanel, "list");
            return;
        }
        practicePanel.setWrongWords(selectedWords);
        cardLayout.show(contentPanel, "practice");
    }
} 