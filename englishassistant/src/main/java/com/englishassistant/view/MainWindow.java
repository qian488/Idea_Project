package com.englishassistant.view;

import java.awt.*;
import javax.swing.*;

import com.englishassistant.util.ThemeManager;
import com.englishassistant.view.manage.ManagementCenter;
import com.englishassistant.view.study.StudyCenter;
import com.englishassistant.view.test.TestCenter;
import com.englishassistant.view.wrong.WrongWordCenter;

/**
 * 主窗口类
 * 作为应用程序的主界面，管理所有功能模块的容器
 *
 * 主要功能:
 * 1. 界面布局管理
 *    - 选项卡式布局
 *    - 菜单栏组织
 *    - 主题切换
 *
 * 2. 功能中心管理
 *    - 学习中心(StudyCenter)
 *      * 单词学习
 *      * 例句学习
 *      * 文章学习
 *    - 管理中心(ManagementCenter)
 *      * 单词管理
 *      * 例句管理
 *      * 文章管理
 *    - 测试中心(TestCenter)
 *      * 单词测试
 *      * 测试历史
 *    - 错题本(WrongWordCenter)
 *      * 错题列表
 *      * 错题练习
 *
 * 3. 界面交互
 *    - 模块切换
 *    - 主题切换
 *    - 窗口管理
 *
 * 特点:
 * - 清晰的模块划分
 * - 统一的界面风格
 * - 灵活的布局管理
 */
public class MainWindow extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final StudyCenter studyCenter;
    private final ManagementCenter managementCenter;
    private final TestCenter testCenter;
    private final WrongWordCenter wrongWordCenter;
    
    public MainWindow() {
        setTitle("英语学习小助手");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 初始化布局
        setLayout(new BorderLayout());
        
        // 创建顶部菜单栏
        JMenuBar menuBar = new JMenuBar();
        
        // 创建主菜单
        JMenu studyMenu = new JMenu("学习");
        JMenu manageMenu = new JMenu("管理");
        JMenu testMenu = new JMenu("测试");
        
        // 创建子菜单项
        JMenuItem wordStudyItem = new JMenuItem("单词学习");
        JMenuItem sentenceStudyItem = new JMenuItem("例句学习");
        JMenuItem articleStudyItem = new JMenuItem("文章学习");
        studyMenu.add(wordStudyItem);
        studyMenu.add(sentenceStudyItem);
        studyMenu.add(articleStudyItem);
        
        JMenuItem wordManageItem = new JMenuItem("单词管理");
        JMenuItem sentenceManageItem = new JMenuItem("例句管理");
        JMenuItem articleManageItem = new JMenuItem("文章管理");
        manageMenu.add(wordManageItem);
        manageMenu.add(sentenceManageItem);
        manageMenu.add(articleManageItem);
        
        JMenuItem testItem = new JMenuItem("单词测试");
        JMenuItem historyItem = new JMenuItem("测试历史");
        JMenuItem wrongPracticeItem = new JMenuItem("错题练习");
        testMenu.add(testItem);
        testMenu.add(historyItem);
        testMenu.addSeparator();
        testMenu.add(wrongPracticeItem);
        
        // 添加菜单到菜单栏
        menuBar.add(studyMenu);
        menuBar.add(manageMenu);
        menuBar.add(testMenu);
        
        // 添加主题切换按钮到右侧
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton themeButton = new JButton("夜间模式");
        themeButton.addActionListener(e -> ThemeManager.toggleTheme());
        rightPanel.add(themeButton);
        menuBar.add(rightPanel);
        
        // 设置菜单栏
        setJMenuBar(menuBar);
        
        // 初始化内容面板
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // 初始化各个中心面板
        studyCenter = new StudyCenter();
        managementCenter = new ManagementCenter(studyCenter);
        testCenter = new TestCenter();
        wrongWordCenter = new WrongWordCenter();
        
        // 添加面板到卡片布局
        contentPanel.add(studyCenter, "study");
        contentPanel.add(managementCenter, "manage");
        contentPanel.add(testCenter, "test");
        contentPanel.add(wrongWordCenter, "wrong");
        
        // 添加菜单事件
        wordStudyItem.addActionListener(e -> {
            cardLayout.show(contentPanel, "study");
            studyCenter.showWordStudy();
        });
        
        sentenceStudyItem.addActionListener(e -> {
            cardLayout.show(contentPanel, "study");
            studyCenter.showSentenceStudy();
        });
        
        articleStudyItem.addActionListener(e -> {
            cardLayout.show(contentPanel, "study");
            studyCenter.showArticleStudy();
            studyCenter.getArticleStudyPanel().refreshArticles();
        });
        
        wordManageItem.addActionListener(e -> {
            cardLayout.show(contentPanel, "manage");
            managementCenter.showWordManage();
        });
        
        sentenceManageItem.addActionListener(e -> {
            cardLayout.show(contentPanel, "manage");
            managementCenter.showSentenceManage();
        });
        
        articleManageItem.addActionListener(e -> {
            cardLayout.show(contentPanel, "manage");
            managementCenter.showArticleManage();
        });
        
        testItem.addActionListener(e -> {
            cardLayout.show(contentPanel, "test");
            testCenter.showTest();
        });
        
        historyItem.addActionListener(e -> {
            cardLayout.show(contentPanel, "test");
            testCenter.showHistory();
        });
        
        wrongPracticeItem.addActionListener(e -> {
            cardLayout.show(contentPanel, "wrong");
            wrongWordCenter.showList();
        });
        
        // 布局
        add(contentPanel, BorderLayout.CENTER);
        
        // 设置窗口属性
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
        
        // 初始化主题管理器
        ThemeManager.initialize(themeButton);
        
        // 默认显示单词学习面板
        cardLayout.show(contentPanel, "study");
        studyCenter.showWordStudy();
    }
} 