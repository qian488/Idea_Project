package com.englishassistant.view.study;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import com.englishassistant.controller.WordController;
import com.englishassistant.controller.SentenceController;
import com.englishassistant.model.Word;
import com.englishassistant.model.Sentence;
import com.englishassistant.view.common.StyledPanel;


/**
 * 单词学习面板类
 * 提供单词查询、翻译和学习功能的界面
 * 
 * 功能说明:
 * 1. 单词查询功能
 *    - translateWord(): 精确翻译单词
 *    - searchWord(): 模糊搜索单词
 *    - displayWord(): 显示单词信息
 *    - displayWordInfo(): 显示单词详细信息
 * 
 * 2. 导航功能
 *    - showPrevious(): 显示上一个结果
 *    - showNext(): 显示下一个结果
 *    - updateNavigationButtons(): 更新导航按钮状态
 * 
 * 3. 界面组件
 *    - 搜索区: 输入框、翻译按钮、搜索按钮
 *    - 信息区: 单词标签、翻译标签、难度标签
 *    - 例句区: 相关例句显示
 *    - 导航区: 上一个/下一个按钮
 * 
 * 4. 快捷键支持
 *    - Enter: 翻译
 *    - Alt+F: 搜索
 * 
 * @author Your Name
 */
public class WordStudyPanel extends StyledPanel {
    private final WordController wordController = new WordController();
    private final SentenceController sentenceController = new SentenceController();
    
    // 输入和按钮区域
    private JTextField inputField;
    private JButton translateButton;
    private JButton searchButton;
    
    // 单词信息显示区域
    private JPanel wordInfoPanel;
    private JLabel wordLabel;
    private JLabel translationLabel;
    private JLabel difficultyLabel;
    
    // 例句显示区域
    private JTextArea sentencesArea;
    
    // 相关单词区域
    private JTextArea relatedWordsArea;
    
    // 导航按钮
    private JButton prevButton;
    private JButton nextButton;
    
    private List<Word> searchResults;
    private int currentIndex = 0;
    
    public WordStudyPanel() {
        addStyledBorder("单词学习");
        setLayout(new BorderLayout(10, 10));
        initComponents();
    }
    
    private void initComponents() {
        // 顶部输入区域
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputField = createStyledTextField(30);
        translateButton = createStyledButton("翻译");
        searchButton = createStyledButton("搜索");
        
        inputPanel.add(createStyledLabel("单词："));
        inputPanel.add(inputField);
        inputPanel.add(translateButton);
        inputPanel.add(searchButton);
        
        // 单词信息显示区域
        wordInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        Font largeFont = new Font("Microsoft YaHei", Font.BOLD, 24);
        Font normalFont = new Font("Microsoft YaHei", Font.PLAIN, 18);
        
        wordLabel = new JLabel("请输入要查找的单词");
        wordLabel.setFont(largeFont);
        
        translationLabel = new JLabel("");
        translationLabel.setFont(normalFont);
        
        difficultyLabel = new JLabel("");
        difficultyLabel.setFont(normalFont);
        
        gbc.gridy = 0;
        wordInfoPanel.add(wordLabel, gbc);
        
        gbc.gridy = 1;
        wordInfoPanel.add(translationLabel, gbc);
        
        gbc.gridy = 2;
        wordInfoPanel.add(difficultyLabel, gbc);
        
        // 例句显示区域
        sentencesArea = new JTextArea(5, 40);
        sentencesArea.setEditable(false);
        sentencesArea.setLineWrap(true);
        sentencesArea.setWrapStyleWord(true);
        sentencesArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        
        // 相关单词区域
        relatedWordsArea = new JTextArea(3, 40);
        relatedWordsArea.setEditable(false);
        relatedWordsArea.setLineWrap(true);
        relatedWordsArea.setWrapStyleWord(true);
        relatedWordsArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        
        // 导航按钮面板
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        prevButton = createStyledButton("上一个");
        nextButton = createStyledButton("下一个");
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
        navigationPanel.add(prevButton);
        navigationPanel.add(nextButton);
        
        // 内容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(wordInfoPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(new JLabel("例句："));
        contentPanel.add(new JScrollPane(sentencesArea));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(new JLabel("相关单词："));
        contentPanel.add(new JScrollPane(relatedWordsArea));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(navigationPanel);
        
        // 添加到主面板
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        
        // 添加事件监听
        translateButton.addActionListener(e -> translateWord());
        searchButton.addActionListener(e -> searchWord());
        prevButton.addActionListener(e -> showPrevious());
        nextButton.addActionListener(e -> showNext());
        
        // 添加回车键支持
        inputField.addActionListener(e -> translateWord());
        
        // 添加快捷键
        translateButton.setMnemonic(KeyEvent.VK_ENTER);
        searchButton.setMnemonic(KeyEvent.VK_F);
        
        // 添加工具提示
        inputField.setToolTipText("输入要查找的单词，按回车键翻译或点击按钮");
        translateButton.setToolTipText("翻译当前输入的单词 (Alt + Enter)");
        searchButton.setToolTipText("搜索相关单词和例句 (Alt + F)");
    }
    
    private void translateWord() {
        String keyword = inputField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "请输入要查询的单词！", 
                "提示", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 使用精确匹配查找单词
        Word word = wordController.getWordByExactMatch(keyword);
        if (word == null) {
            JOptionPane.showMessageDialog(this, 
                "未找到单词 \"" + keyword + "\"！\n" +
                "您可以在管理中心添加这个单词。", 
                "未找到", 
                JOptionPane.INFORMATION_MESSAGE);
            clearDisplay(); // 清空显示
            
            // 重置搜索相关的状态
            searchResults = null;
            currentIndex = 0;
            updateNavigationButtons();
            return;
        }
        
        // 重置搜索相关的状态
        searchResults = null;
        currentIndex = 0;
        updateNavigationButtons();
        
        displayWord(word);
    }
    
    // 清空显示内容
    private void clearDisplay() {
        wordLabel.setText("请输入要查找的单词");
        translationLabel.setText("");
        difficultyLabel.setText("");
        sentencesArea.setText("");
        relatedWordsArea.setText("");
    }
    
    private void displayWord(Word word) {
        // 显示单词基本信息
        wordLabel.setText(word.getWord());
        translationLabel.setText("翻译：" + word.getTranslation());
        difficultyLabel.setText("难度：" + word.getDifficulty());
    }
    
    private void searchWord() {
        String keyword = inputField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入要搜索的单词！");
            return;
        }
        
        searchResults = wordController.searchWords(keyword);
        currentIndex = 0;
        
        if (searchResults.isEmpty()) {
            wordLabel.setText("未找到相关单词");
            translationLabel.setText("");
            difficultyLabel.setText("");
            sentencesArea.setText("");
            relatedWordsArea.setText("");
        } else {
            displayWordInfo(searchResults.get(0));
        }
        
        updateNavigationButtons();
    }
    
    private void displayWordInfo(Word word) {
        // 显示单词基本信息
        displayWord(word);
        
        // 获取并显示相关例句
        List<Sentence> sentences = sentenceController.searchSentences(word.getWord());
        StringBuilder sentenceText = new StringBuilder();
        for (Sentence sentence : sentences) {
            sentenceText.append(sentence.getEnglishSentence()).append("\n");
            sentenceText.append(sentence.getChineseSentence()).append("\n\n");
        }
        sentencesArea.setText(sentenceText.toString());
        
        // 获取并显示相关单词
        List<Word> relatedWords = wordController.searchWords(word.getWord());
        StringBuilder relatedText = new StringBuilder();
        for (Word related : relatedWords) {
            if (!related.getWord().equals(word.getWord())) {
                relatedText.append(related.getWord())
                          .append(" - ")
                          .append(related.getTranslation())
                          .append("\n");
            }
        }
        relatedWordsArea.setText(relatedText.toString());
        
        // 如果有多个结果，显示当前位置
        if (searchResults != null && searchResults.size() > 1) {
            difficultyLabel.setText(String.format("难度：%d  (%d/%d)", 
                word.getDifficulty(), currentIndex + 1, searchResults.size()));
        }
    }
    
    private void showPrevious() {
        if (searchResults != null && currentIndex > 0) {
            currentIndex--;
            displayWordInfo(searchResults.get(currentIndex));
            updateNavigationButtons();
        }
    }
    
    private void showNext() {
        if (searchResults != null && currentIndex < searchResults.size() - 1) {
            currentIndex++;
            displayWordInfo(searchResults.get(currentIndex));
            updateNavigationButtons();
        }
    }
    
    private void updateNavigationButtons() {
        // 只有在搜索模式下且有多个结果时才启用导航按钮
        boolean hasMultipleResults = searchResults != null && searchResults.size() > 1;
        prevButton.setEnabled(hasMultipleResults && currentIndex > 0);
        nextButton.setEnabled(hasMultipleResults && currentIndex < searchResults.size() - 1);
        
        // 如果不是搜索模式，隐藏导航按钮
        prevButton.setVisible(hasMultipleResults);
        nextButton.setVisible(hasMultipleResults);
    }
} 