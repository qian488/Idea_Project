package com.englishassistant.view.manage;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.englishassistant.view.common.StyledPanel;
import com.englishassistant.controller.SentenceController;
import com.englishassistant.model.Sentence;
import com.englishassistant.controller.WordController;
import com.englishassistant.model.Word;
import com.englishassistant.util.DataExportImportUtil;
import com.englishassistant.util.DataExportImportUtil.FileType;
/**
 * 例句管理面板类
 * 提供例句的管理和单词关联功能
 * 
 * 功能说明:
 * 1. 基本操作
 *    - addSentence(): 添加新例句
 *    - showEditDialog(): 编辑例句
 *    - deleteSentence(): 删除例句
 *    - clearFields(): 清空输入字段
 * 
 * 2. 单词关联
 *    - loadWordList(): 加载可关联的单词列表
 *    - autoAssociateWords(): 自动识别和关联单词
 *    - updateWordAssociations(): 更新单词关联关系
 * 
 * 3. 数据管理
 *    - loadSentences(): 加载所有例句
 *    - searchSentences(): 搜索例句
 *    - importData(): 导入例句数据
 *    - exportData(): 导出例句数据
 *    - refreshAll(): 刷新所有数据和关联
 * 
 * 4. 界面组件
 *    - 例句输入区(英文、中文、关联单词)
 *    - 例句列表表格
 *    - 单词选择列表
 *    - 自定义操作按钮
 * 
 * 实现细节:
 * - 继承自StyledPanel保持统一风格
 * - 使用JTable展示例句列表
 * - 实现智能单词关联
 * - 支持批量数据处理
 * 
 * @author Your Name
 */
public class SentenceManagePanel extends StyledPanel {
    private final SentenceController sentenceController = new SentenceController();
    private final WordController wordController = new WordController();
    
    private DefaultTableModel tableModel;
    private JTable sentenceTable;
    private JTextField searchField;
    private JTextField englishField;
    private JTextField chineseField;
    private JList<Word> wordList;
    private DefaultListModel<Word> wordListModel;
    
    public SentenceManagePanel() {
        setLayout(new BorderLayout(10, 10));
        initComponents();
        loadSentences();
    }
    
    private void initComponents() {
        // 顶部面板：搜索和添加
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        
        // 搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = createStyledTextField(20);
        JButton searchButton = createStyledButton("搜索");
        searchButton.addActionListener(e -> searchSentences());
        
        searchPanel.add(createStyledLabel("搜索:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // 修改工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = createStyledButton("刷新");
        JButton importButton = createStyledButton("导入数据");
        JButton exportButton = createStyledButton("导出数据");
        
        refreshButton.addActionListener(e -> refreshAll());
        importButton.addActionListener(e -> importData());
        exportButton.addActionListener(e -> exportData());
        
        toolBar.add(refreshButton);
        toolBar.add(importButton);
        toolBar.add(exportButton);
        
        // 将工具栏添加到顶部面板
        topPanel.add(toolBar, BorderLayout.EAST);
        
        // 添加例句面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        englishField = createStyledTextField(30);
        chineseField = createStyledTextField(30);
        
        // 单词选择列表
        wordListModel = new DefaultListModel<>();
        wordList = new JList<>(wordListModel);
        wordList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane wordScrollPane = new JScrollPane(wordList);
        wordScrollPane.setPreferredSize(new Dimension(200, 100));
        
        JButton refreshWordListButton = createStyledButton("刷新单词列表");
        refreshWordListButton.addActionListener(e -> loadWordList());
        
        // 添加件
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(createStyledLabel("英文例句:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(englishField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(createStyledLabel("中文翻译:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(chineseField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(createStyledLabel("关联单词:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(wordScrollPane, gbc);
        
        gbc.gridy = 3;
        inputPanel.add(refreshWordListButton, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = createStyledButton("添加");
        JButton clearButton = createStyledButton("清空");
        
        addButton.addActionListener(e -> addSentence());
        clearButton.addActionListener(e -> clearFields());
        
        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        
        gbc.gridy = 4;
        inputPanel.add(buttonPanel, gbc);
        
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        
        // 修改表格的操作列
        String[] columnNames = {"ID", "英文例句", "中文翻译", "关联单词", "操作"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // 只有操作列可编辑
            }
        };
        
        sentenceTable = new JTable(tableModel);
        
        // 设置表格的选择模式
        sentenceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 添加双击编辑功能
        sentenceTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = sentenceTable.getSelectedRow();
                    int column = sentenceTable.getSelectedColumn();
                    if (row != -1 && column != 4) { // 不是操作列时才触发编辑
                        showEditDialog(row);
                    }
                }
            }
        });
        
        // 设置操作列的渲染器和编辑器
        TableColumn operationColumn = sentenceTable.getColumnModel().getColumn(4);
        operationColumn.setCellRenderer(new OperationRenderer());
        operationColumn.setCellEditor(new ButtonEditor(new JCheckBox(), this::showEditDialog, this::deleteSentence));
        operationColumn.setPreferredWidth(80);
        
        // 设置列宽
        sentenceTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        sentenceTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        sentenceTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        sentenceTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        sentenceTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(sentenceTable), BorderLayout.CENTER);
        
        // 初始加载单词列表
        loadWordList();
    }
    
    private void loadWordList() {
        wordListModel.clear();
        List<Word> words = wordController.getAllWords();
        for (Word word : words) {
            wordListModel.addElement(word);
        }
    }
    
    private void addSentence() {
        String englishSentence = englishField.getText().trim();
        String chineseSentence = chineseField.getText().trim();
        
        if (englishSentence.isEmpty() || chineseSentence.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写完整的例句信息！");
            return;
        }
        
        Sentence sentence = new Sentence();
        sentence.setEnglishSentence(englishSentence);
        sentence.setChineseSentence(chineseSentence);
        
        // 自动关联单词
        autoAssociateWords(sentence);
        
        // 添加手动选择的单词
        for (Word word : wordList.getSelectedValuesList()) {
            sentence.addRelatedWord(word);
        }
        
        if (sentenceController.addSentence(sentence)) {
            JOptionPane.showMessageDialog(this, "添加成功！");
            clearFields();
            loadSentences();
        } else {
            JOptionPane.showMessageDialog(this, "添加失败！");
        }
    }
    
    private void showEditDialog(int row) {
        int id = (Integer) sentenceTable.getValueAt(row, 0);
        String englishSentence = (String) sentenceTable.getValueAt(row, 1);
        String chineseSentence = (String) sentenceTable.getValueAt(row, 2);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "修改例句", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField englishField = new JTextField(englishSentence, 40);
        JTextField chineseField = new JTextField(chineseSentence, 40);
        
        // 创建单词选择列表
        DefaultListModel<Word> dialogWordListModel = new DefaultListModel<>();
        JList<Word> dialogWordList = new JList<>(dialogWordListModel);
        dialogWordList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // 加载所有单词并设置选中状
        List<Word> allWords = wordController.getAllWords();
        List<Word> relatedWords = sentenceController.getRelatedWords(id);
        
        for (Word word : allWords) {
            dialogWordListModel.addElement(word);
            if (relatedWords.contains(word)) {
                int index = dialogWordListModel.indexOf(word);
                dialogWordList.addSelectionInterval(index, index);
            }
        }
        
        JScrollPane wordScrollPane = new JScrollPane(dialogWordList);
        wordScrollPane.setPreferredSize(new Dimension(200, 100));
        
        // 添加组件到对话框
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("英文例句:"), gbc);
        gbc.gridx = 1;
        dialog.add(englishField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("中文翻译:"), gbc);
        gbc.gridx = 1;
        dialog.add(chineseField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("关联单词:"), gbc);
        gbc.gridx = 1;
        dialog.add(wordScrollPane, gbc);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> {
            Sentence sentence = new Sentence();
            sentence.setId(id);
            sentence.setEnglishSentence(englishField.getText().trim());
            sentence.setChineseSentence(chineseField.getText().trim());
            
            // 自动关联单词
            autoAssociateWords(sentence);
            
            // 添加手动选择的单词
            for (Word word : dialogWordList.getSelectedValuesList()) {
                sentence.addRelatedWord(word);
            }
            
            if (sentenceController.updateSentence(sentence)) {
                JOptionPane.showMessageDialog(dialog, "修改成功！");
                loadSentences();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "修改失败！");
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 1; gbc.gridy = 3;
        dialog.add(buttonPanel, gbc);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void loadSentences() {
        tableModel.setRowCount(0);
        List<Sentence> sentences = sentenceController.getAllSentences();
        for (Sentence sentence : sentences) {
            Object[] row = {
                sentence.getId(),
                sentence.getEnglishSentence(),
                sentence.getChineseSentence(),
                formatRelatedWords(sentence.getRelatedWords()),
                "编辑/删除"
            };
            tableModel.addRow(row);
        }
    }
    
    private String formatRelatedWords(List<Word> words) {
        if (words == null || words.isEmpty()) {
            return "";
        }
        return words.stream()
            .map(Word::getWord)
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    }
    
    private void searchSentences() {
        String keyword = searchField.getText().trim();
        List<Sentence> sentences;
        if (keyword.isEmpty()) {
            sentences = sentenceController.getAllSentences();
        } else {
            sentences = sentenceController.searchSentences(keyword);
            if (sentences.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "未找到包含 \"" + keyword + "\" 的例句！", 
                    "搜索结果", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        loadSentencesToTable(sentences);
    }
    
    private void loadSentencesToTable(List<Sentence> sentences) {
        tableModel.setRowCount(0);
        for (Sentence sentence : sentences) {
            Object[] row = {
                sentence.getId(),
                sentence.getEnglishSentence(),
                sentence.getChineseSentence(),
                formatRelatedWords(sentence.getRelatedWords()),
                "编辑/删除"
            };
            tableModel.addRow(row);
        }
    }
    
    private void deleteSentence(int row) {
        if (row < 0 || row >= sentenceTable.getRowCount()) {
            return;
        }
        
        int id = (Integer) sentenceTable.getValueAt(row, 0);
        String englishSentence = (String) sentenceTable.getValueAt(row, 1);
        
        int option = JOptionPane.showConfirmDialog(this,
            "确定要删除以下例句吗？\n" + englishSentence,
            "确认删除",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (option == JOptionPane.YES_OPTION) {
            if (sentenceController.deleteSentence(id)) {
                SwingUtilities.invokeLater(() -> {
                    ((DefaultTableModel) sentenceTable.getModel()).removeRow(row);
                    sentenceController.reorderIds();  // 重新排序ID
                    loadSentences();  // 重新加载数据以更新显示
                });
                JOptionPane.showMessageDialog(this, "删除成功！");
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！");
            }
        }
    }
    
    private void clearFields() {
        englishField.setText("");
        chineseField.setText("");
        wordList.clearSelection();
    }
    
    // 自定义按钮渲染器
    private static class ButtonRenderer extends JButton implements TableCellRenderer {
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
    
    // 自定义按钮编辑器
    private class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;
        
        public ButtonEditor(JCheckBox checkBox, EditAction editAction, DeleteAction deleteAction) {
            super(checkBox);
            button = new JButton("操作 ▼");
            button.setFocusPainted(false);
            
            button.addActionListener(e -> {
                // 创建弹出菜单
                JPopupMenu popup = new JPopupMenu();
                JMenuItem editItem = new JMenuItem("编辑");
                JMenuItem deleteItem = new JMenuItem("删除");
                
                // 设置菜单项图标（可选）
                editItem.setIcon(UIManager.getIcon("FileView.fileIcon"));
                deleteItem.setIcon(UIManager.getIcon("FileView.fileIcon"));
                
                // 添加事件监听器
                editItem.addActionListener(ev -> {
                    popup.setVisible(false);
                    editAction.edit(currentRow);
                    fireEditingCanceled();
                });
                
                deleteItem.addActionListener(ev -> {
                    popup.setVisible(false);
                    deleteAction.delete(currentRow);
                    fireEditingCanceled();
                });
                
                popup.add(editItem);
                popup.addSeparator();
                popup.add(deleteItem);
                
                // 显示弹出菜单
                popup.show(button, 0, button.getHeight());
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "操作 ▼";
        }
    }
    
    @FunctionalInterface
    interface EditAction {
        void edit(int row);
    }
    
    @FunctionalInterface
    interface DeleteAction {
        void delete(int row);
    }
    
    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出例句数据");
        
        // 添加文件过滤器
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV 文件 (*.csv)", "csv"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML 文件 (*.xml)", "xml"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON 文件 (*.json)", "json"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel 文件 (*.xlsx)", "xlsx"));
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
                FileType fileType = switch (extension.toLowerCase()) {
                    case ".csv" -> FileType.CSV;
                    case ".xml" -> FileType.XML;
                    case ".json" -> FileType.JSON;
                    case ".xlsx" -> FileType.EXCEL;
                    default -> FileType.TXT;
                };
                
                List<Sentence> sentences = sentenceController.getAllSentences();
                DataExportImportUtil.exportData(file.getAbsolutePath(), new ArrayList<>(), sentences, new ArrayList<>(), fileType);
                JOptionPane.showMessageDialog(this, "例句数据导出成功！");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "导出失败: " + ex.getMessage());
            }
        }
    }
    
    private void importData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导入例句数据");
        
        // 添加文件过滤器
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV 文件 (*.csv)", "csv"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML 文件 (*.xml)", "xml"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON 文件 (*.json)", "json"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel 文件 (*.xlsx)", "xlsx"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String extension = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase();
                
                // 根据扩展名确定文件类型
                FileType fileType = switch (extension) {
                    case ".csv" -> FileType.CSV;
                    case ".xml" -> FileType.XML;
                    case ".json" -> FileType.JSON;
                    case ".xlsx" -> FileType.EXCEL;
                    default -> FileType.TXT;
                };
                
                Map<String, List<?>> data = DataExportImportUtil.importData(file.getAbsolutePath(), fileType);
                if (data != null && data.containsKey("sentences")) {
                    @SuppressWarnings("unchecked")
                    List<Sentence> sentences = (List<Sentence>) data.get("sentences");
                    
                    // 显示导入确认对话框
                    int option = JOptionPane.showConfirmDialog(this,
                        String.format("确定要导入 %d 条例句吗？\n导入时将自动关联单词。", sentences.size()),
                        "确认导入",
                        JOptionPane.YES_NO_OPTION);
                        
                    if (option == JOptionPane.YES_OPTION) {
                        int successCount = 0;
                        for (Sentence sentence : sentences) {
                            // 在添加句子前，先进行单词关联
                            autoAssociateWords(sentence);
                            
                            // 如果导入的数据中已有关联单词信息，保留这些信息
                            if (sentence.getRelatedWords() != null && !sentence.getRelatedWords().isEmpty()) {
                                // 合并自动关联和已有关联的单词
                                List<Word> existingWords = sentence.getRelatedWords();
                                for (Word word : existingWords) {
                                    sentence.addRelatedWord(word);
                                }
                            }
                            
                            // 使用 addSentenceIfNotExists 替代 addSentence
                            if (sentenceController.addSentenceIfNotExists(sentence)) {
                                successCount++;
                            }
                        }
                        
                        // 重新排序ID并刷新显示
                        sentenceController.reorderIds();
                        loadSentences();
                        
                        JOptionPane.showMessageDialog(this, 
                            String.format("数据导入完成！\n成功导入 %d/%d 条例句。", 
                                successCount, sentences.size()));
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "文件中没有找到有效的例句数据！");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "导入失败: " + ex.getMessage());
            }
        }
    }
    
    // 在工具栏中添加导入导出按钮
    private void addToolbarButtons(JPanel toolBar) {
        JButton importButton = createStyledButton("导入数据");
        JButton exportButton = createStyledButton("导出数据");
        
        importButton.addActionListener(e -> importData());
        exportButton.addActionListener(e -> exportData());
        
        toolBar.add(importButton);
        toolBar.add(exportButton);
    }
    
    // 修改自动关联单词的方法
    private void autoAssociateWords(Sentence sentence) {
        String englishText = sentence.getEnglishSentence().toLowerCase();
        // 移除标点符号并分割成单词，保留空作为分隔符
        String[] sentenceWords = englishText.replaceAll("[^a-zA-Z\\s]", " ")
                                          .trim()
                                          .split("\\s+");
        
        // 获取所有单词
        List<Word> allWords = wordController.getAllWords();
        
        // 使用Set来存储已匹配的单词，避免重复
        Set<String> matchedWords = new HashSet<>();
        
        // 检查每个单词是否在数据库中存在
        for (String sentenceWord : sentenceWords) {
            // 跳过空字符串
            if (sentenceWord.isEmpty()) {
                continue;
            }
            
            // 对于句子中的每个单词，寻找完全匹配的词库单词
            for (Word word : allWords) {
                String wordText = word.getWord().toLowerCase();
                // 只有完全匹配才建立关联
                if (sentenceWord.equals(wordText) && !matchedWords.contains(wordText)) {
                    sentence.addRelatedWord(word);
                    matchedWords.add(wordText);
                    break; // 找到匹配就跳出内层循环，避免重复匹配
                }
            }
        }
        
        // 如果没有找到任何关联单词，可以记录日志或提示
        if (sentence.getRelatedWords().isEmpty()) {
            System.out.println("警告：句子 \"" + sentence.getEnglishSentence() + "\" 没有找到任何关联单词");
        } else {
            System.out.println("成功关联单词：" + matchedWords + " 到句子：" + sentence.getEnglishSentence());
        }
    }
    
    // 添加刷新方法
    private void refreshAll() {
        // 显示确认对话框
        int option = JOptionPane.showConfirmDialog(this,
            "刷新将重新扫描所有例句和单词并建立关联关系，可能需要一些时间。是否继续？",
            "确认刷新",
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            // 显示进度对话框
            ProgressMonitor progressMonitor = new ProgressMonitor(this,
                "正在刷新数据...",
                "", 0, 100);
            
            // 在后台线程中执行刷新操作
            SwingWorker<Void, Integer> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    // 更新进度
                    setProgress(20);
                    // 刷新所有句子的单词关联
                    sentenceController.refreshWordAssociations();
                    setProgress(80);
                    return null;
                }
                
                @Override
                protected void done() {
                    progressMonitor.close();
                    loadSentences();
                    JOptionPane.showMessageDialog(SentenceManagePanel.this,
                        "刷新完成！",
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
    
    // 新的操作列渲染器
    private static class OperationRenderer extends JPanel implements TableCellRenderer {
        private final JButton button;
        
        public OperationRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            button = new JButton("操作 ▼");
            button.setFocusPainted(false);
            add(button);
            setBackground(Color.WHITE);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                button.setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
                button.setBackground(UIManager.getColor("Button.background"));
            }
            return this;
        }
    }
} 