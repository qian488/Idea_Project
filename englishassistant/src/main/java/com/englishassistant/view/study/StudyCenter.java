package com.englishassistant.view.study;

import java.awt.*;
import javax.swing.*;

import com.englishassistant.view.common.StyledPanel;

/**
 * 学习中心面板类
 * 作为所有学习功能的容器，负责组织和切换各个学习子面板
 * 
 * 功能说明:
 * 1. 面板管理
 *    - wordStudyPanel: 单词学习面板
 *    - sentenceStudyPanel: 例句学习面板
 *    - articleStudyPanel: 文章学习面板
 * 
 * 2. 面板切换方法
 *    - showWordStudy(): 切换到单词学习
 *    - showSentenceStudy(): 切换到例句学习
 *    - showArticleStudy(): 切换到文章学习
 *    - getArticleStudyPanel(): 获取文章学习面板实例
 * 
 * 3. 界面实现
 *    - 使用CardLayout实现面板切换
 *    - 继承自StyledPanel保持统一风格
 *    - 默认显示单词学习面板
 * 
 * @author Your Name
 */
public class StudyCenter extends StyledPanel {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final WordStudyPanel wordStudyPanel;
    private final SentenceStudyPanel sentenceStudyPanel;
    private final ArticleStudyPanel articleStudyPanel;
    
    public StudyCenter() {
        setLayout(new BorderLayout());
        
        // 初始化内容面板
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // 初始化子面板
        wordStudyPanel = new WordStudyPanel();
        sentenceStudyPanel = new SentenceStudyPanel();
        articleStudyPanel = new ArticleStudyPanel();
        
        // 添加面板到卡片布局
        contentPanel.add(wordStudyPanel, "word");
        contentPanel.add(sentenceStudyPanel, "sentence");
        contentPanel.add(articleStudyPanel, "article");
        
        // 布局
        add(contentPanel, BorderLayout.CENTER);
        
        // 默认显示单词学习面板
        cardLayout.show(contentPanel, "word");
    }
    
    // 提供切换面板的方法，供菜单项调用
    public void showWordStudy() {
        cardLayout.show(contentPanel, "word");
    }
    
    public void showSentenceStudy() {
        cardLayout.show(contentPanel, "sentence");
    }
    
    public void showArticleStudy() {
        cardLayout.show(contentPanel, "article");
    }
    
    public ArticleStudyPanel getArticleStudyPanel() {
        return articleStudyPanel;
    }
} 