package com.englishassistant.view.manage;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.englishassistant.view.common.StyledPanel;
import com.englishassistant.controller.WordController;
import com.englishassistant.model.Word;
import com.englishassistant.util.DataExportImportUtil;
import com.englishassistant.util.DataExportImportUtil.FileType;

/**
 * 单词管理面板类
 * 提供单词的增删改查和导入导出功能
 * 
 * 功能说明:
 * 1. 基本操作
 *    - addWord(): 添加新单词
 *    - showEditDialog(): 编辑单词
 *    - deleteWord(): 删除单词
 *    - clearFields(): 清空输入字段
 * 
 * 2. 数据管理
 *    - loadWords(): 加载所有单词
 *    - searchWords(): 搜索单词
 *    - importData(): 导入单词数据(支持CSV/TXT/JSON/Excel)
 *    - exportData(): 导出单词数据
 *    - refreshData(): 刷新并检查数据完整性
 * 
 * 3. 界面组件
 *    - 单词信息输入区
 *    - 单词列表表格
 *    - 操作按钮组
 *    - 自定义渲染器和编辑器
 * 
 * 实现细节:
 * - 继承自StyledPanel保持统一风格
 * - 使用JTable展示单词列表
 * - 支持多种格式数据导入导出
 * - 实现数据验证和错误提示
 * 
 * @author Your Name
 */
public class WordManagePanel extends StyledPanel {
    private final WordController wordController = new WordController();
    
    private JTextField wordField;
    private JTextField translationField;
    private JComboBox<Integer> difficultyBox;
    private JTextField searchField;
    private JTable wordTable;
    private DefaultTableModel tableModel;
    
    public WordManagePanel() {
        setLayout(new BorderLayout(10, 10));
        initComponents();
        loadWords();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // 顶部面板：搜索和工具栏
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = createStyledButton("搜索");
        searchButton.addActionListener(e -> searchWords());
        searchPanel.add(new JLabel("搜索:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // 工具栏（右对齐）
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = createStyledButton("刷新");
        JButton importButton = createStyledButton("导入数据");
        JButton exportButton = createStyledButton("导出数据");
        
        refreshButton.addActionListener(e -> refreshData());
        importButton.addActionListener(e -> importData());
        exportButton.addActionListener(e -> exportData());
        
        toolBar.add(refreshButton);
        toolBar.add(importButton);
        toolBar.add(exportButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(toolBar, BorderLayout.EAST);
        
        // 添加单词区域
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wordField = createStyledTextField(15);
        translationField = createStyledTextField(15);
        difficultyBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        JButton addButton = createStyledButton("添加");
        JButton clearButton = createStyledButton("清空");
        
        inputPanel.add(new JLabel("单词:"));
        inputPanel.add(wordField);
        inputPanel.add(new JLabel("翻译:"));
        inputPanel.add(translationField);
        inputPanel.add(new JLabel("难度:"));
        inputPanel.add(difficultyBox);
        inputPanel.add(addButton);
        inputPanel.add(clearButton);
        
        addButton.addActionListener(e -> addWord());
        clearButton.addActionListener(e -> clearFields());
        
        // 布局
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(inputPanel, BorderLayout.CENTER);
        
        add(northPanel, BorderLayout.NORTH);
        
        // 中央面板：单词表格
        String[] columnNames = {"ID", "单词", "翻译", "难度", "操作"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;  // ID列不可编辑
            }
        };
        
        wordTable = new JTable(tableModel);
        
        // 设置表格列宽
        wordTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID列
        wordTable.getColumnModel().getColumn(1).setPreferredWidth(150); // 单词列
        wordTable.getColumnModel().getColumn(2).setPreferredWidth(150); // 翻译列
        wordTable.getColumnModel().getColumn(3).setPreferredWidth(50);  // 难度列
        wordTable.getColumnModel().getColumn(4).setPreferredWidth(100); // 操作列
        
        add(new JScrollPane(wordTable), BorderLayout.CENTER);
    }
    
    private void addWord() {
        String wordText = wordField.getText().trim();
        String translation = translationField.getText().trim();
        int difficulty = (Integer) difficultyBox.getSelectedItem();
        
        if (wordText.isEmpty() || translation.isEmpty()) {
            JOptionPane.showMessageDialog(this, "单词和翻译不能为空！");
            return;
        }
        
        Word word = new Word(wordText, translation, "", difficulty);
        if (wordController.addWord(word)) {
            loadWords();
            clearFields();
            JOptionPane.showMessageDialog(this, "添加成功！");
        } else {
            JOptionPane.showMessageDialog(this, "添加失败！");
        }
    }
    
    private void clearFields() {
        wordField.setText("");
        translationField.setText("");
        difficultyBox.setSelectedIndex(0);
    }
    
    private void loadWords() {
        List<Word> words = wordController.getAllWords();
        loadWordsToTable(words);
    }
    
    private void loadWordsToTable(List<Word> words) {
        tableModel.setRowCount(0);
        for (Word word : words) {
            Object[] row = {
                word.getId(),
                word.getWord(),
                word.getTranslation(),
                word.getDifficulty(),
                "删除"
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchWords() {
        String keyword = searchField.getText().trim();
        List<Word> words;
        if (keyword.isEmpty()) {
            words = wordController.getAllWords();
        } else {
            words = wordController.searchWords(keyword);
            if (words.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "未找到包含 \"" + keyword + "\" 的单词！", 
                    "搜索结果", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        loadWordsToTable(words);
    }
    
    private void showEditDialog(int row) {
        int id = (Integer) wordTable.getValueAt(row, 0);
        String word = (String) wordTable.getValueAt(row, 1);
        String translation = (String) wordTable.getValueAt(row, 2);
        int difficulty = (Integer) wordTable.getValueAt(row, 3);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "修改单词", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField wordField = new JTextField(word, 20);
        JTextField translationField = new JTextField(translation, 20);
        JComboBox<Integer> difficultyBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        difficultyBox.setSelectedItem(difficulty);
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("单词:"), gbc);
        gbc.gridx = 1;
        dialog.add(wordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("翻译:"), gbc);
        gbc.gridx = 1;
        dialog.add(translationField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("难度:"), gbc);
        gbc.gridx = 1;
        dialog.add(difficultyBox, gbc);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> {
            String newWord = wordField.getText().trim();
            String newTranslation = translationField.getText().trim();
            int newDifficulty = (Integer) difficultyBox.getSelectedItem();
            
            if (newWord.isEmpty() || newTranslation.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "单词和翻译不能为空！");
                return;
            }
            
            Word updatedWord = new Word(newWord, newTranslation, "", newDifficulty);
            updatedWord.setId(id);
            
            if (wordController.updateWord(updatedWord)) {
                loadWords();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "修改成功！");
            } else {
                JOptionPane.showMessageDialog(dialog, "修改失败！");
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
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
    
    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private final JTable table;  // 添加table引用
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
            table = wordTable;  // 使用实例变量
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
                int row = table.getSelectedRow();  // 使用实例变量引用
                if (row != -1) {
                    int id = (Integer) table.getValueAt(row, 0);  // 使用实例变量引用
                    int option = JOptionPane.showConfirmDialog(
                        button,
                        "确定要删除这个单词吗？",
                        "确认删除",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (option == JOptionPane.YES_OPTION) {
                        if (wordController.deleteWord(id)) {
                            loadWords();
                            JOptionPane.showMessageDialog(button, "删除成功！");
                        } else {
                            JOptionPane.showMessageDialog(button, "删除失败！");
                        }
                    }
                }
            }
            isPushed = false;
            return label;
        }
    }
    
    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出数据");
        
        // 添加所有支持的文件过滤器
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
                
                List<Word> words = wordController.getAllWords();
                DataExportImportUtil.exportData(file.getAbsolutePath(), words, new ArrayList<>(), new ArrayList<>(), fileType);
                JOptionPane.showMessageDialog(this, "数据导出成功！");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "导出失败: " + ex.getMessage());
            }
        }
    }
    
    private void importData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导入数据");
        
        // 添加所有支持的文件滤器
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
                if (data != null && data.containsKey("words")) {
                    @SuppressWarnings("unchecked")
                    List<Word> words = (List<Word>) data.get("words");
                    for (Word word : words) {
                        wordController.addWordIfNotExists(word);
                    }
                    loadWords();
                    JOptionPane.showMessageDialog(this, "数据导入成功！");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "导入失败: " + ex.getMessage());
            }
        }
    }
    
    private void refreshData() {
        try {
            // 重新排序ID
            wordController.reorderIds();
            
            // 重新加载单词列表
            List<Word> words = wordController.getAllWords();
            
            // 检查重复单词
            Map<String, List<Word>> wordMap = new HashMap<>();
            for (Word word : words) {
                String key = word.getWord().toLowerCase();
                wordMap.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
            }
            
            // 收集重复和问题单词
            List<String> duplicates = new ArrayList<>();
            List<String> noTranslation = new ArrayList<>();
            List<String> invalidDifficulty = new ArrayList<>();
            
            for (Map.Entry<String, List<Word>> entry : wordMap.entrySet()) {
                if (entry.getValue().size() > 1) {
                    duplicates.add(entry.getKey() + " (" + entry.getValue().size() + "次)");
                }
                
                for (Word word : entry.getValue()) {
                    if (word.getTranslation() == null || word.getTranslation().trim().isEmpty()) {
                        noTranslation.add(word.getWord());
                    }
                    if (word.getDifficulty() < 1 || word.getDifficulty() > 5) {
                        invalidDifficulty.add(word.getWord());
                    }
                }
            }
            
            // 更新表格
            loadWordsToTable(words);
            
            // 显示检查结果
            StringBuilder message = new StringBuilder("刷新完成！\n\n");
            message.append("总单词数：").append(words.size()).append("\n");
            
            if (!duplicates.isEmpty()) {
                message.append("\n重复单词：\n");
                duplicates.forEach(word -> message.append("- ").append(word).append("\n"));
            }
            
            if (!noTranslation.isEmpty()) {
                message.append("\n缺少翻译：\n");
                noTranslation.forEach(word -> message.append("- ").append(word).append("\n"));
            }
            
            if (!invalidDifficulty.isEmpty()) {
                message.append("\n难度值异常：\n");
                invalidDifficulty.forEach(word -> message.append("- ").append(word).append("\n"));
            }
            
            JOptionPane.showMessageDialog(this,
                message.toString(),
                "刷新结果",
                duplicates.isEmpty() && noTranslation.isEmpty() && invalidDifficulty.isEmpty() 
                    ? JOptionPane.INFORMATION_MESSAGE 
                    : JOptionPane.WARNING_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "刷新失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}