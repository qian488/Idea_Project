package com.englishassistant.view.manage;

import java.awt.*;
import javax.swing.*;

import com.englishassistant.view.common.StyledPanel;
import com.englishassistant.view.study.StudyCenter;

/**
 * 管理中心面板类
 * 作为所有管理功能的容器，负责组织和切换各个管理子面板
 * 
 * 功能说明:
 * 1. 面板管理
 *    - 单词管理面板(WordManagePanel)
 *    - 例句管理面板(SentenceManagePanel) 
 *    - 文章管理面板(ArticleManagePanel)
 * 
 * 2. 面板切换
 *    - showWordManage(): 显示单词管理面板
 *    - showSentenceManage(): 显示例句管理面板
 *    - showArticleManage(): 显示文章管理面板
 * 
 * 3. 界面布局
 *    - 使用CardLayout实现面板切换
 *    - 统一的界面风格(继承自StyledPanel)
 *    - 与StudyCenter的交互
 * 
 * 实现细节:
 * - 使用CardLayout管理多个面板
 * - 持有StudyCenter引用以实现面板间通信
 * - 提供面板切换的公共接口
 * 
 * @author Your Name
 */
public class ManagementCenter extends StyledPanel {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final WordManagePanel wordManagePanel;
    private final SentenceManagePanel sentenceManagePanel;
    private final ArticleManagePanel articleManagePanel;
    private final StudyCenter studyCenter;
    
    public ManagementCenter(StudyCenter studyCenter) {
        this.studyCenter = studyCenter;
        
        // 初始化布局
        setLayout(new BorderLayout());
        
        // 初始化内容面板
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // 初始化子面板
        wordManagePanel = new WordManagePanel();
        sentenceManagePanel = new SentenceManagePanel();
        articleManagePanel = new ArticleManagePanel();
        
        // 添加面板到卡片布局
        contentPanel.add(wordManagePanel, "word");
        contentPanel.add(sentenceManagePanel, "sentence");
        contentPanel.add(articleManagePanel, "article");
        
        // 布局
        add(contentPanel, BorderLayout.CENTER);
        
        // 默认显示单词管理面板
        cardLayout.show(contentPanel, "word");
    }
    
    // 提供切换面板的方法，供菜单项调用
    public void showWordManage() {
        cardLayout.show(contentPanel, "word");
    }
    
    public void showSentenceManage() {
        cardLayout.show(contentPanel, "sentence");
    }
    
    public void showArticleManage() {
        cardLayout.show(contentPanel, "article");
    }
} 