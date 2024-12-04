package com.englishassistant.view.manage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.io.*;
import java.text.*;

import com.englishassistant.controller.ArticleController;
import com.englishassistant.controller.WordController;
import com.englishassistant.model.Article;
import com.englishassistant.model.Word;
import com.englishassistant.view.common.StyledPanel;
import com.englishassistant.view.study.StudyCenter;

/**
 * 文章管理面板类
 * 提供文章的管理和阅读进度跟踪功能
 * 
 * 功能说明:
 * 1. 基本操作
 *    - addArticle(): 添加新文章
 *    - showEditDialog(): 编辑文章
 *    - deleteArticle(): 删除文章
 *    - clearFields(): 清空输入字段
 * 
 * 2. 单词关联
 *    - autoAssociateWords(): 自动识别文章中的单词
 *    - updateWordAssociations(): 更新单词关联关系
 * 
 * 3. 数据管理
 *    - loadArticles(): 加载所有文章
 *    - searchArticles(): 搜索文章
 *    - importData(): 导入文章数据(支持TXT/JSON)
 *    - exportData(): 导出文章数据
 *    - refreshAndReorder(): 刷新并重排序文章
 * 
 * 4. 阅读进度
 *    - updateReadProgress(): 更新阅读进度
 *    - saveReadingState(): 保存阅读状态
 *    - loadReadingState(): 加载阅读状态
 * 
 * 5. 界面组件
 *    - 文章信息输入区(标题、内容、翻译、难度)
 *    - 文章列表表格
 *    - 阅读进度显示
 *    - 自定义操作按钮
 * 
 * 实现细节:
 * - 继承自StyledPanel保持统一风格
 * - 使用JTable展示文章列表
 * - 实现文章内容的分段显示
 * - 支持阅读进度的持久化
 * - 提供文章预览功能
 * 
 * @author Your Name
 */
public class ArticleManagePanel extends StyledPanel {
    private final ArticleController articleController = new ArticleController();
    private final WordController wordController = new WordController();
    private DefaultTableModel tableModel;
    private JTable articleTable;
    private JTextField searchField;
    private JTextField titleField;
    private JTextArea contentArea;
    private JTextArea translationArea;
    private JComboBox<Integer> difficultyBox;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private StudyCenter studyCenter;
    
    public ArticleManagePanel() {
        super();
        setLayout(new BorderLayout(10, 10));
        initComponents();
        loadArticles();
    }
    
    private void initComponents() {
        // 创建顶部搜索和工具栏
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        
        // 搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("搜索");
        searchButton.addActionListener(e -> searchArticles());
        
        searchPanel.add(new JLabel("搜索:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // 修改工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = createStyledButton("刷新");
        JButton importButton = createStyledButton("导入数据");
        JButton exportButton = createStyledButton("导出数据");
        
        refreshButton.addActionListener(e -> refreshAndReorder());
        importButton.addActionListener(e -> importData());
        exportButton.addActionListener(e -> exportData());
        
        toolBar.add(refreshButton);
        toolBar.add(importButton);
        toolBar.add(exportButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(toolBar, BorderLayout.EAST);
        
        // 创建表格
        String[] columnNames = {"ID", "标题", "难度", "创建时间", "操作"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        
        articleTable = new JTable(tableModel);
        articleTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        articleTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), this::deleteArticle));
        
        // 设置列宽
        articleTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        articleTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        articleTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        articleTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        articleTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        // 创建文章编辑面板
        JPanel editPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        titleField = new JTextField(30);
        contentArea = new JTextArea(10, 30);
        translationArea = new JTextArea(10, 30);
        difficultyBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        translationArea.setLineWrap(true);
        translationArea.setWrapStyleWord(true);
        
        JButton saveButton = new JButton("保存");
        JButton clearButton = new JButton("清空");
        
        // 添加组件到编辑面板
        gbc.gridx = 0; gbc.gridy = 0;
        editPanel.add(new JLabel("标题:"), gbc);
        gbc.gridx = 1;
        editPanel.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        editPanel.add(new JLabel("内容:"), gbc);
        gbc.gridx = 1;
        editPanel.add(new JScrollPane(contentArea), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        editPanel.add(new JLabel("翻译:"), gbc);
        gbc.gridx = 1;
        editPanel.add(new JScrollPane(translationArea), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        editPanel.add(new JLabel("难度:"), gbc);
        gbc.gridx = 1;
        editPanel.add(difficultyBox, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        
        gbc.gridx = 1; gbc.gridy = 4;
        editPanel.add(buttonPanel, gbc);
        
        // 添加事件监听
        saveButton.addActionListener(e -> saveArticle());
        clearButton.addActionListener(e -> clearFields());
        
        // 布局
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(articleTable), editPanel);
        splitPane.setDividerLocation(200);
        
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void loadArticles() {
        tableModel.setRowCount(0);
        List<Article> articles = articleController.getAllArticles();
        for (Article article : articles) {
            Object[] row = {
                article.getId(),
                article.getTitle(),
                article.getDifficulty(),
                dateFormat.format(article.getCreateTime()),
                "删除"
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchArticles() {
        String keyword = searchField.getText().trim();
        tableModel.setRowCount(0);
        List<Article> articles;
        
        if (keyword.isEmpty()) {
            articles = articleController.getAllArticles();
        } else {
            articles = articleController.searchArticles(keyword);
        }
        
        for (Article article : articles) {
            Object[] row = {
                article.getId(),
                article.getTitle(),
                article.getDifficulty(),
                dateFormat.format(article.getCreateTime()),
                "删除"
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加文章", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        titleField = new JTextField(30);
        contentArea = new JTextArea(10, 30);
        translationArea = new JTextArea(10, 30);
        difficultyBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        translationArea.setLineWrap(true);
        translationArea.setWrapStyleWord(true);
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("标题:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("内容:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(new JScrollPane(contentArea), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("翻译:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(new JScrollPane(translationArea), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("难度:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(difficultyBox, gbc);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            String translation = translationArea.getText().trim();
            int difficulty = (Integer) difficultyBox.getSelectedItem();
            
            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "标题和内不能为空！");
                return;
            }
            
            Article article = new Article(title, content, translation, difficulty);
            if (articleController.addArticle(article)) {
                loadArticles();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "添加成功！");
            } else {
                JOptionPane.showMessageDialog(dialog, "添加失败！");
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showReadDialog(int row) {
        int id = (Integer) articleTable.getValueAt(row, 0);
        List<Article> articles = articleController.getAllArticles();
        Article article = articles.stream()
            .filter(a -> a.getId() == id)
            .findFirst()
            .orElse(null);
            
        if (article == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            article.getTitle(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea contentArea = new JTextArea(article.getContent());
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        
        JTextArea translationArea = new JTextArea(article.getTranslation());
        translationArea.setLineWrap(true);
        translationArea.setWrapStyleWord(true);
        translationArea.setEditable(false);
        
        contentPanel.add(new JScrollPane(contentArea));
        contentPanel.add(new JScrollPane(translationArea));
        
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteArticle(int row) {
        int id = (Integer) articleTable.getValueAt(row, 0);
        int option = JOptionPane.showConfirmDialog(
            this,
            "确定要删除这篇文章吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION
        );
        
        if (option == JOptionPane.YES_OPTION) {
            if (articleController.deleteArticle(id)) {
                loadArticles();  // 刷新管理面板的文章列表
                
                // 通知学习面板刷新
                if (studyCenter != null && studyCenter.getArticleStudyPanel() != null) {
                    studyCenter.getArticleStudyPanel().refreshArticles();
                }
                
                JOptionPane.showMessageDialog(this, "删除成功！");
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！");
            }
        }
    }
    
    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private final DeleteAction deleteAction;
        
        public ButtonEditor(JCheckBox checkBox, DeleteAction deleteAction) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
            this.deleteAction = deleteAction;
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                deleteAction.delete(articleTable.getSelectedRow());
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    @FunctionalInterface
    interface DeleteAction {
        void delete(int row);
    }
    
    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出文章数据");
        
        // 添加文件过滤器，同时支持 JSON 和 TXT
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON 文件 (*.json)", "json"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
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
                
                // 获取所有文章
                List<Article> articles = articleController.getAllArticles();
                
                // 根据扩展名选择导出方式
                boolean isJson = extension.equals(".json");
                articleController.exportArticles(file.getAbsolutePath(), articles, isJson);
                
                JOptionPane.showMessageDialog(this, "文章数据导出成功！");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "导出失败: " + ex.getMessage());
            }
        }
    }
    
    private void importData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导入文章数据");
        
        // 添加件过滤器，同时支持 JSON 和 TXT
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON 文件 (*.json)", "json"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String extension = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase();
                
                // 根据扩展名确定是否为JSON格式
                boolean isJson = extension.equals(".json");
                
                // 导入数据
                List<Article> articles = articleController.importArticles(file.getAbsolutePath(), isJson);
                
                if (articles != null && !articles.isEmpty()) {
                    // 显示导入确认对话框
                    int option = JOptionPane.showConfirmDialog(this,
                        String.format("确定要导入 %d 篇文章吗？\n导入时将自动关联单词。", articles.size()),
                        "确认导入",
                        JOptionPane.YES_NO_OPTION);
                        
                    if (option == JOptionPane.YES_OPTION) {
                        int successCount = 0;
                        for (Article article : articles) {
                            // 在添加文章前，先进行单词关联
                            autoAssociateWords(article);
                            
                            if (articleController.addArticleIfNotExists(article)) {
                                successCount++;
                            }
                        }
                        
                        // 重新加载文章列表
                        loadArticles();
                        
                        // 通知学习面板刷新
                        if (studyCenter != null && studyCenter.getArticleStudyPanel() != null) {
                            studyCenter.getArticleStudyPanel().refreshArticles();
                        }
                        
                        JOptionPane.showMessageDialog(this, 
                            String.format("成功导入 %d/%d 篇文章", successCount, articles.size()));
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "文件中没有找到有效的文章数据！");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "导入失败: " + ex.getMessage());
            }
        }
    }
    
    private void saveArticle() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        String translation = translationArea.getText().trim();
        int difficulty = (Integer) difficultyBox.getSelectedItem();
        
        // 输入验证
        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "标题和内容为必填项！", 
                "提示", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 创建文章对象
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setTranslation(translation);
        article.setDifficulty(difficulty);
        article.setCreateTime(new Date());
        article.setReadProgress(0);  // 新文章的阅读进度为0
        
        // 自动关联单词
        autoAssociateWords(article);
        
        // 保存到数据库
        if (articleController.addArticle(article)) {
            JOptionPane.showMessageDialog(this, "文章保存成功！");
            clearFields();
            loadArticles();  // 刷新文章列表
        } else {
            JOptionPane.showMessageDialog(this, 
                "保存失败！请检查数据库连接。", 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
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
    
    private void clearFields() {
        titleField.setText("");
        contentArea.setText("");
        translationArea.setText("");
        difficultyBox.setSelectedIndex(0);
    }
    
    private void refreshAndReorder() {
        // 显示确认对话框
        int option = JOptionPane.showConfirmDialog(this,
            "确定要重新排序所有文章ID吗？",
            "确认刷新",
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            // 显示进度对话框
            ProgressMonitor progressMonitor = new ProgressMonitor(this,
                "正在重新排序...",
                "", 0, 100);
            
            // 在后台线程中执行刷新操作
            SwingWorker<Void, Integer> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    setProgress(20);
                    // 重新排序文章ID
                    articleController.reorderIds();
                    setProgress(80);
                    return null;
                }
                
                @Override
                protected void done() {
                    progressMonitor.close();
                    loadArticles();  // 重新加载文章列表
                    
                    // 通知学习面板刷新
                    if (studyCenter != null && studyCenter.getArticleStudyPanel() != null) {
                        studyCenter.getArticleStudyPanel().refreshArticles();
                    }
                    
                    JOptionPane.showMessageDialog(ArticleManagePanel.this,
                        "文章ID重新排序完成！",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            };
            
            worker.addPropertyChangeListener(evt -> {
                if ("progress".equals(evt.getPropertyName())) {
                    progressMonitor.setProgress((Integer) evt.getNewValue());
                }
            });
            
            worker.execute();
        }
    }
} 