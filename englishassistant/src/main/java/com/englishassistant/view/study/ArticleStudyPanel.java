package com.englishassistant.view.study;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.Timer;

import com.englishassistant.controller.ArticleController;
import com.englishassistant.controller.WordController;
import com.englishassistant.model.Article;
import com.englishassistant.model.Word;
import com.englishassistant.view.common.StyledPanel;
import com.englishassistant.util.DataExportImportUtil;
import com.englishassistant.util.DataExportImportUtil.FileType;

/**
 * 文章学习面板类
 * 提供文章阅读和学习功能的界面
 * 
 * 功能说明:
 * 1. 文章阅读
 *    - displayArticle(): 显示文章内容
 *    - updateProgressFromScroll(): 更新阅读进度
 *    - autoSaveProgress(): 自动保存进度
 *    - toggleTranslation(): 切换翻译显示
 * 
 * 2. 单词学习
 *    - showSelectedTextInfo(): 显示选中文本信息
 *    - showWordExplanation(): 显示单词解释
 *    - autoAssociateWords(): 自动关联单词
 *    - highlightWords(): 高亮显示单词
 * 
 * 3. 数据管理
 *    - loadArticles(): 加载文章列表
 *    - refreshArticles(): 刷新文章列表
 *    - importArticle(): 导入文章
 *    - exportArticle(): 导出文章
 * 
 * 4. 界面组件
 *    - 选择区: 文章选择器、显示控制
 *    - 阅读区: 原文显示、翻译显示
 *    - 进度区: 进度条、最后阅读时间
 *    - 信息区: 单词解释、笔记编辑
 * 
 * @author Your Name
 */
public class ArticleStudyPanel extends StyledPanel {
    private final ArticleController articleController = new ArticleController();
    private final WordController wordController = new WordController();
    
    // UI组件
    private JComboBox<Article> articleSelector;
    private JTextPane contentPane;
    private JTextPane translationPane;
    private JPanel wordInfoPanel;
    private JProgressBar readProgressBar;
    private JLabel lastReadLabel;
    private Timer autoSaveTimer;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private JCheckBox showTranslationCheckBox;
    private JTextArea wordExplanationArea;
    private JTextArea notesArea;
    
    // 当前文章
    private Article currentArticle;
    
    public ArticleStudyPanel() {
        addStyledBorder("文章学习");
        setLayout(new BorderLayout(10, 10));
        
        // 初始化数据库表
        articleController.initTable();
        
        initComponents();
        loadArticles();
    }
    
    private void initComponents() {
        // 顶部控制面板
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // 选择器面板
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        articleSelector = new JComboBox<>();
        articleSelector.setPreferredSize(new Dimension(300, 25));
        
        // 显示控制
        showTranslationCheckBox = new JCheckBox("显示翻译", true);
        showTranslationCheckBox.addActionListener(e -> toggleTranslation());
        
        selectorPanel.add(createStyledLabel("选择文章: "));
        selectorPanel.add(articleSelector);
        selectorPanel.add(Box.createHorizontalStrut(20));
        selectorPanel.add(showTranslationCheckBox);
        
        // 进度显示面板
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        readProgressBar = new JProgressBar(0, 100);
        readProgressBar.setStringPainted(true);
        lastReadLabel = createStyledLabel("");
        
        progressPanel.add(createStyledLabel("阅读进度："));
        progressPanel.add(readProgressBar);
        progressPanel.add(lastReadLabel);
        
        topPanel.add(selectorPanel, BorderLayout.CENTER);
        topPanel.add(progressPanel, BorderLayout.SOUTH);
        
        // 创建主阅读区域
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setResizeWeight(0.5); // 平均分配空间
        
        // 原文显示区域
        contentPane = new JTextPane();
        contentPane.setEditable(false);
        contentPane.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        
        // 翻译显示区域
        translationPane = new JTextPane();
        translationPane.setEditable(false);
        translationPane.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        
        // 添加文本选择事件
        contentPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                String selectedText = contentPane.getSelectedText();
                if (selectedText != null && !selectedText.isEmpty()) {
                    showSelectedTextInfo(selectedText);
                }
            }
        });
        
        // 设置分隔面板
        mainSplitPane.setTopComponent(new JScrollPane(contentPane));
        mainSplitPane.setBottomComponent(new JScrollPane(translationPane));
        
        // 右侧信息面板（用于显示选中文本的翻译）
        wordInfoPanel = new JPanel();
        wordInfoPanel.setLayout(new BoxLayout(wordInfoPanel, BoxLayout.Y_AXIS));
        wordInfoPanel.setPreferredSize(new Dimension(200, 0));
        wordInfoPanel.setBorder(BorderFactory.createTitledBorder("选中内容翻译"));
        
        // 初始化自动保存定时器
        autoSaveTimer = new Timer(30000, e -> autoSaveProgress());
        autoSaveTimer.start();
        
        // 初始化 wordExplanationArea
        wordExplanationArea = new JTextArea();
        wordExplanationArea.setLineWrap(true);
        wordExplanationArea.setWrapStyleWord(true);
        wordExplanationArea.setEditable(false);
        wordInfoPanel.add(new JScrollPane(wordExplanationArea));
        
        // 初始化 notesArea
        notesArea = new JTextArea();
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setPreferredSize(new Dimension(300, 100)); // 设置合适的尺寸
        wordInfoPanel.add(new JScrollPane(notesArea)); // 假设将其添加到 wordInfoPanel
        
        // 添加到主面板
        add(topPanel, BorderLayout.NORTH);
        add(mainSplitPane, BorderLayout.CENTER);
        add(wordInfoPanel, BorderLayout.EAST);
        
        // 添加文章选择事件
        articleSelector.addActionListener(e -> {
            Article selected = (Article) articleSelector.getSelectedItem();
            if (selected != null) {
                displayArticle(selected);
            } else {
                // 清空显示
                clearDisplay();
            }
        });
    }
    
    private void toggleTranslation() {
        translationPane.setVisible(showTranslationCheckBox.isSelected());
        revalidate();
        repaint();
    }
    
    private void showSelectedTextInfo(String selectedText) {
        wordInfoPanel.removeAll();
        
        // 检查是否是单个单词
        if (!selectedText.contains(" ")) {
            Word word = wordController.getWordByExactMatch(selectedText.trim());
            if (word != null) {
                // 显示单词信息
                JLabel wordLabel = new JLabel(word.getWord());
                wordLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
                JLabel translationLabel = new JLabel(word.getTranslation());
                
                wordInfoPanel.add(wordLabel);
                wordInfoPanel.add(Box.createVerticalStrut(5));
                wordInfoPanel.add(translationLabel);
            }
        } else {
            // 显示句子翻译
            JTextArea translationArea = new JTextArea(selectedText);
            translationArea.setLineWrap(true);
            translationArea.setWrapStyleWord(true);
            translationArea.setEditable(false);
            
            wordInfoPanel.add(new JScrollPane(translationArea));
        }
        
        wordInfoPanel.revalidate();
        wordInfoPanel.repaint();
    }
    
    private void loadArticles() {
        articleSelector.removeAllItems();
        List<Article> articles = articleController.getAllArticles();
        
        // 添加一个空选项
        articleSelector.addItem(null);
        
        // 添加所有文章
        for (Article article : articles) {
            articleSelector.addItem(article);
        }
        
        // 如果有文章，选择第一篇
        if (articles.size() > 0) {
            articleSelector.setSelectedIndex(1);  // 选择第一篇文章（索引1，因为0是空选项）
        }
    }
    
    public void refreshArticles() {
        // 保存当前选中的文章ID
        int currentArticleId = -1;
        if (currentArticle != null) {
            currentArticleId = currentArticle.getId();
        }
        
        // 重新加载文章列表
        loadArticles();
        
        // 如果之前有选中的文章，尝试恢复选择
        if (currentArticleId != -1) {
            for (int i = 0; i < articleSelector.getItemCount(); i++) {
                Article article = (Article) articleSelector.getItemAt(i);
                if (article != null && article.getId() == currentArticleId) {
                    articleSelector.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void displayArticle(Article article) {
        currentArticle = article;
        
        // 显示文章内容
        contentPane.setText(article.getContent());
        
        // 显示翻译
        translationPane.setText(article.getTranslation());
        
        // 更新进度显示
        readProgressBar.setValue(article.getReadProgress());
        updateLastReadLabel();
        
        // 高亮显生词
        highlightWords(article.getRelatedWords());
        
        // 自动关联新的单词
        autoAssociateWords(article);
        
        // 根据进度滚动到上次阅读位置
        SwingUtilities.invokeLater(() -> {
            int position = (int) (contentPane.getDocument().getLength() * 
                (article.getReadProgress() / 100.0));
            contentPane.setCaretPosition(Math.min(position, 
                contentPane.getDocument().getLength()));
        });
    }
    
    private void highlightWords(List<Word> words) {
        if (words == null || words.isEmpty()) {
            return;
        }
        
        // 创建高亮样式
        Style highlightStyle = contentPane.addStyle("highlight", null);
        StyleConstants.setBackground(highlightStyle, new Color(255, 255, 0, 100)); // 浅黄色背景
        StyleConstants.setForeground(highlightStyle, Color.BLACK);
        StyleConstants.setBold(highlightStyle, true);
        
        // 清除现有高亮
        StyledDocument doc = contentPane.getStyledDocument();
        Style defaultStyle = contentPane.getStyle(StyleContext.DEFAULT_STYLE);
        doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, true);
        
        String text = contentPane.getText().toLowerCase();
        // 为每个单词添加高亮
        for (Word word : words) {
            String wordText = word.getWord().toLowerCase();
            int pos = 0;
            while ((pos = text.indexOf(wordText, pos)) >= 0) {
                // 检查是否是完整单词（前后是空格或标点）
                boolean isWordStart = pos == 0 || !Character.isLetterOrDigit(text.charAt(pos - 1));
                boolean isWordEnd = pos + wordText.length() >= text.length() || 
                                  !Character.isLetterOrDigit(text.charAt(pos + wordText.length()));
                
                if (isWordStart && isWordEnd) {
                    doc.setCharacterAttributes(pos, wordText.length(), highlightStyle, false);
                }
                pos += wordText.length();
            }
        }
    }
    
    private String getWordAtPosition(int pos) {
        try {
            String text = contentPane.getText();
            int start = pos;
            int end = pos;
            
            // 向前查找单词边界
            while (start > 0 && Character.isLetterOrDigit(text.charAt(start - 1))) {
                start--;
            }
            
            // 向后查找单词边界
            while (end < text.length() && Character.isLetterOrDigit(text.charAt(end))) {
                end++;
            }
            
            if (start != end) {
                return text.substring(start, end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void showWordExplanation(String word) {
        Word foundWord = wordController.getWordByExactMatch(word);
        if (foundWord != null) {
            StringBuilder explanation = new StringBuilder();
            explanation.append("单词: ").append(foundWord.getWord()).append("\n");
            if (foundWord.getPhonetic() != null && !foundWord.getPhonetic().isEmpty()) {
                explanation.append("音标: ").append(foundWord.getPhonetic()).append("\n");
            }
            explanation.append("翻译: ").append(foundWord.getTranslation()).append("\n");
            explanation.append("难度: ").append(foundWord.getDifficulty());
            
            wordExplanationArea.setText(explanation.toString());
        } else {
            wordExplanationArea.setText("未找到单词: " + word);
        }
    }
    
    private void saveNotes() {
        if (currentArticle == null) {
            return;
        }
        
        String notes = notesArea.getText().trim();
        currentArticle.setNotes(notes);
        
        if (articleController.updateArticle(currentArticle)) {
            JOptionPane.showMessageDialog(this, "笔记保存成功！");
        } else {
            JOptionPane.showMessageDialog(this, "笔记保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void autoAssociateWords(Article article) {
        String content = article.getContent().toLowerCase();
        // 移除标点符号并分割成单词
        String[] words = content.replaceAll("[^a-zA-Z\\s]", " ")
                               .trim()
                               .split("\\s+");
        
        // 获取所有单词
        List<Word> allWords = wordController.getAllWords();
        
        // 使用Set来存储已匹配的单词，避免重复
        Set<String> matchedWords = new HashSet<>();
        
        // 检查每个单词是否在数据库中存在
        for (String contentWord : words) {
            if (contentWord.isEmpty()) {
                continue;
            }
            
            // 对于文章中的每个单词，寻找完全匹配的词库单词
            for (Word word : allWords) {
                String wordText = word.getWord().toLowerCase();
                if (contentWord.equals(wordText) && !matchedWords.contains(wordText)) {
                    article.addRelatedWord(word);
                    matchedWords.add(wordText);
                    break;
                }
            }
        }
    }
    
    private void updateProgressFromScroll() {
        if (currentArticle == null) return;
        
        // 计算当前阅读进度
        int totalHeight = contentPane.getDocument().getLength();
        int currentPosition = contentPane.getCaretPosition();
        int progress = (int) ((currentPosition * 100.0) / totalHeight);
        
        // 更新进度
        currentArticle.setReadProgress(progress);
        currentArticle.setLastReadTime(new Date());
        readProgressBar.setValue(progress);
        
        // 保存进度
        articleController.updateReadProgress(
            currentArticle.getId(), 
            progress);
        
        // 更新显示
        updateLastReadLabel();
    }
    
    private void updateLastReadLabel() {
        if (currentArticle != null && currentArticle.getLastReadTime() != null) {
            lastReadLabel.setText("上次阅读: " + 
                dateFormat.format(currentArticle.getLastReadTime()));
        } else {
            lastReadLabel.setText("");
        }
    }
    
    private void autoSaveProgress() {
        if (currentArticle != null) {
            saveProgress();
        }
    }
    
    private void saveProgress() {
        if (currentArticle != null) {
            // 更新当前文章进度
            currentArticle.setReadProgress(readProgressBar.getValue());
            currentArticle.setLastReadTime(new Date());
            
            // 调用更新方法
            articleController.updateReadProgress(
                currentArticle.getId(), 
                currentArticle.getReadProgress());
            
            // 更新界面显示
            updateLastReadLabel();
        }
    }
    
    private void importArticle() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导入文章");
        
        // 添加文件过滤器
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON 文��� (*.json)", "json"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String extension = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase();
                
                // 根据扩展名确定文件类型
                FileType fileType = extension.equals(".json") ? FileType.JSON : FileType.TXT;
                
                Map<String, List<?>> data = DataExportImportUtil.importData(file.getAbsolutePath(), fileType);
                if (data != null && data.containsKey("articles")) {
                    @SuppressWarnings("unchecked")
                    List<Article> articles = (List<Article>) data.get("articles");
                    
                    int successCount = 0;
                    for (Article article : articles) {
                        if (articleController.addArticle(article)) {
                            successCount++;
                        }
                    }
                    
                    loadArticles(); // 重新加载文章列表
                    JOptionPane.showMessageDialog(this, 
                        String.format("成功导入 %d/%d 篇文章", successCount, articles.size()));
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "导入失败: " + ex.getMessage());
            }
        }
    }
    
    private void exportArticle() {
        if (currentArticle == null) {
            JOptionPane.showMessageDialog(this, "请先选择要导出的文章！");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出文章");
        
        // 添加文件过滤器
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON 文件 (*.json)", "json"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String extension = "";
                
                // 根据选择的过滤器确定文件扩展名
                if (fileChooser.getFileFilter() instanceof FileNameExtensionFilter) {
                    FileNameExtensionFilter filter = (FileNameExtensionFilter) fileChooser.getFileFilter();
                    extension = "." + filter.getExtensions()[0];
                }
                
                // 如果文件名没有对应的扩展名，添加扩展名
                if (!file.getName().toLowerCase().endsWith(extension)) {
                    file = new File(file.getAbsolutePath() + extension);
                }
                
                // 根据扩展名确定文件类型
                FileType fileType = extension.equals(".json") ? FileType.JSON : FileType.TXT;
                
                List<Article> articles = new ArrayList<>();
                articles.add(currentArticle);
                
                DataExportImportUtil.exportData(file.getAbsolutePath(), 
                    new ArrayList<>(), new ArrayList<>(), articles, fileType);
                    
                JOptionPane.showMessageDialog(this, "文章导出成功！");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "导出失败: " + ex.getMessage());
            }
        }
    }
    
    private void clearDisplay() {
        contentPane.setText("");
        translationPane.setText("");
        readProgressBar.setValue(0);
        lastReadLabel.setText("");
        currentArticle = null;
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            // 当面板变为可见时刷新文章列表
            refreshArticles();
        }
    }
} 