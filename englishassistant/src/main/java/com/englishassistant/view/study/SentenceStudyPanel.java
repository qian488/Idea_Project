package com.englishassistant.view.study;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.englishassistant.controller.SentenceController;
import com.englishassistant.model.Sentence;
import com.englishassistant.model.Word;
import com.englishassistant.view.common.StyledPanel;

/**
 * 例句学习面板类
 * 提供例句浏览和学习功能的界面
 * 
 * 功能说明:
 * 1. 例句浏览
 *    - loadRandomSentences(): 加载随机例句
 *    - searchSentences(): 搜索例句
 *    - displayCurrentSentence(): 显示当前例句
 * 
 * 2. 导航功能
 *    - showPrevious(): 显示上一个例句
 *    - showNext(): 显示下一个例句
 *    - updateNavigationButtons(): 更新导航按钮状态
 * 
 * 3. 界面组件
 *    - 搜索区: 搜索框和按钮
 *    - 内容区: 英文例句、中文翻译、相关单词
 *    - 导航区: 上一句/下一句按钮
 * 
 * 4. 预留功能
 *    - playSentence(): 例句朗读(待实现)
 *    - practiseSentence(): 例句练习(待实现)
 *    - memorizeTraining(): 记忆训练(待实现)
 * 
 * @author Your Name
 */
public class SentenceStudyPanel extends StyledPanel {
    private final SentenceController sentenceController = new SentenceController();
    
    // UI组件
    private JTextField searchField;
    private JTextArea englishArea;      // 英文例句显示
    private JTextArea chineseArea;      // 中文翻译显示
    private JTextArea relatedWordsArea; // 相关单词显示
    private JLabel progressLabel;       // 进度显示
    private JButton prevButton;         // 上一句
    private JButton nextButton;         // 下一句
    
    // 数据
    private List<Sentence> currentSentences;
    private int currentIndex = 0;
    
    public SentenceStudyPanel() {
        addStyledBorder("例句学习");
        setLayout(new BorderLayout(10, 10));
        initComponents();
        loadRandomSentences();
    }
    
    private void initComponents() {
        // 顶部搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = createStyledTextField(30);
        JButton searchButton = createStyledButton("搜索");
        searchButton.addActionListener(e -> searchSentences());
        
        searchPanel.add(createStyledLabel("搜索例句："));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // 中央内容面板
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        
        // 英文例句区域
        englishArea = createStyledTextArea(5, 40);
        englishArea.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        englishArea.setEditable(false);
        
        // 中文翻译区域
        chineseArea = createStyledTextArea(3, 40);
        chineseArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        chineseArea.setEditable(false);
        
        // 相关单词区域
        relatedWordsArea = createStyledTextArea(4, 40);
        relatedWordsArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        relatedWordsArea.setEditable(false);
        
        // 添加组件
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(createStyledLabel("英文例句："), gbc);
        
        gbc.gridy = 1;
        contentPanel.add(new JScrollPane(englishArea), gbc);
        
        gbc.gridy = 2;
        contentPanel.add(createStyledLabel("中文翻译："), gbc);
        
        gbc.gridy = 3;
        contentPanel.add(new JScrollPane(chineseArea), gbc);
        
        gbc.gridy = 4;
        contentPanel.add(createStyledLabel("相关单词："), gbc);
        
        gbc.gridy = 5;
        contentPanel.add(new JScrollPane(relatedWordsArea), gbc);
        
        // 底部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        prevButton = createStyledButton("上一句");
        nextButton = createStyledButton("下一句");
        progressLabel = createStyledLabel("0/0");
        
        prevButton.addActionListener(e -> showPrevious());
        nextButton.addActionListener(e -> showNext());
        
        controlPanel.add(prevButton);
        controlPanel.add(progressLabel);
        controlPanel.add(nextButton);
        
        // 布局
        add(searchPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void loadRandomSentences() {
        currentSentences = sentenceController.getAllSentences();
        if (!currentSentences.isEmpty()) {
            currentIndex = 0;
            displayCurrentSentence();
        } else {
            clearDisplay();
        }
    }
    
    private void searchSentences() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadRandomSentences();
            return;
        }
        
        currentSentences = sentenceController.searchSentences(keyword);
        if (currentSentences.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "未找到包含 \"" + keyword + "\" 的例句！", 
                "搜索结果", 
                JOptionPane.INFORMATION_MESSAGE);
            clearDisplay();
        } else {
            currentIndex = 0;
            displayCurrentSentence();
        }
    }
    
    private void displayCurrentSentence() {
        if (currentSentences == null || currentSentences.isEmpty()) {
            clearDisplay();
            return;
        }
        
        Sentence sentence = currentSentences.get(currentIndex);
        englishArea.setText(sentence.getEnglishSentence());
        chineseArea.setText(sentence.getChineseSentence());
        
        // 显示相关单词解释
        StringBuilder wordInfo = new StringBuilder();
        for (Word word : sentence.getRelatedWords()) {
            wordInfo.append(word.getWord())
                    .append(": ")
                    .append(word.getTranslation())
                    .append("\n");
        }
        relatedWordsArea.setText(wordInfo.toString());
        
        // 更新导航按钮状态
        updateNavigationButtons();
    }
    
    private void clearDisplay() {
        englishArea.setText("");
        chineseArea.setText("");
        relatedWordsArea.setText("");
        updateNavigationButtons();
    }
    
    private void updateNavigationButtons() {
        boolean hasSentences = currentSentences != null && !currentSentences.isEmpty();
        prevButton.setEnabled(hasSentences && currentIndex > 0);
        nextButton.setEnabled(hasSentences && currentIndex < currentSentences.size() - 1);
    }
    
    private void showPrevious() {
        if (currentIndex > 0) {
            currentIndex--;
            displayCurrentSentence();
        }
    }
    
    private void showNext() {
        if (currentIndex < currentSentences.size() - 1) {
            currentIndex++;
            displayCurrentSentence();
        }
    }
    
    // 预留的功能接口，后续可以实现
    /**
     * 朗读当前例句
     * TODO: 实现语音播放功能
     * - 可以考虑使用TTS引擎
     * - 或者预录音频文件
     * - 或者调用在线API
     */
    private void playSentence() {
        JOptionPane.showMessageDialog(this, 
            "朗读功能开发中...\n" +
            "计划支持：\n" +
            "1. 本地TTS引擎\n" +
            "2. 在线语音服务\n" +
            "3. 录音播放功能", 
            "功能预告", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 例句练习功能
     * TODO: 实现练习模式
     * - 英译中练习
     * - 中译英练习
     * - 听力练习
     * - 跟读练习
     */
    private void practiseSentence() {
        // 预留方法
    }
    
    /**
     * 记忆训练功能
     * TODO: 实现记忆训练
     * - 例句填空
     * - 例句重组
     * - 例句默写
     */
    private void memorizeTraining() {
        // 预留方法
    }
} 