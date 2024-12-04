package com.englishassistant.view.wrong;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import com.englishassistant.controller.WrongWordController;
import com.englishassistant.model.WrongWord;
import com.englishassistant.model.WrongWordTableModel;
import com.englishassistant.view.common.StyledPanel;

/**
 * 错题列表面板类
 * 提供错题的查看、管理和分析功能
 * 
 * 功能说明:
 * 1. 错题管理
 *    - loadWrongWords(): 加载错题列表
 *    - deleteSelectedWords(): 删除选中错题
 *    - getSelectedWrongWords(): 获取选中错题
 * 
 * 2. 练习功能
 *    - startPractice(): 开始错题练习
 *    - onPracticeCompleted(): 练习完成回调
 * 
 * 3. 数据分析
 *    - showAnalysis(): 显示错题分析报告
 *    - exportWrongWords(): 导出错题数据(预留)
 * 
 * 4. 界面组件
 *    - 工具栏: 练习、删除、刷新、分析按钮
 *    - 错题表格: 显示错题详细信息
 *    - 分析报告: 统计和展示错题数据
 * 
 * @author Your Name
 */
public class WrongWordPanel extends StyledPanel implements WrongWordPracticeListener {
    private final WrongWordController controller;
    private final WrongWordTableModel tableModel;
    private JTable wrongWordTable;
    
    public WrongWordPanel() {
        super();
        addStyledBorder("错题本");
        controller = new WrongWordController();
        tableModel = new WrongWordTableModel();
        initComponents();
        loadWrongWords();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // 顶部工具栏
        JPanel toolBar = new JPanel(new BorderLayout());
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 左侧按钮组
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton practiceButton = createStyledButton("开始练习");
        JButton deleteButton = createStyledButton("删除");
        leftButtons.add(practiceButton);
        leftButtons.add(deleteButton);
        
        // 右侧按钮组
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = createStyledButton("刷新");
        JButton analysisButton = createStyledButton("错题分析");
        rightButtons.add(refreshButton);
        rightButtons.add(analysisButton);
        
        toolBar.add(leftButtons, BorderLayout.WEST);
        toolBar.add(rightButtons, BorderLayout.EAST);
        
        // 错题表格
        wrongWordTable = new JTable(tableModel);
        wrongWordTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        wrongWordTable.setRowHeight(25);
        
        // 设置表格列宽
        wrongWordTable.getColumnModel().getColumn(0).setPreferredWidth(100);  // 单词
        wrongWordTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // 翻译
        wrongWordTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // 错误答案
        wrongWordTable.getColumnModel().getColumn(3).setPreferredWidth(80);   // 错误次数
        wrongWordTable.getColumnModel().getColumn(4).setPreferredWidth(80);   // 是否掌握
        wrongWordTable.getColumnModel().getColumn(5).setPreferredWidth(150);  // 错误时间
        
        // 添加事件监听
        practiceButton.addActionListener(e -> startPractice());
        deleteButton.addActionListener(e -> deleteSelectedWords());
        refreshButton.addActionListener(e -> loadWrongWords());
        analysisButton.addActionListener(e -> showAnalysis());
        
        // 添加表格的选择监听器
        wrongWordTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = wrongWordTable.getSelectedRowCount() > 0;
            practiceButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
        });
        
        // 布局
        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(wrongWordTable), BorderLayout.CENTER);
        
        // 初始状态
        practiceButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    public void loadWrongWords() {
        try {
            List<WrongWord> wrongWords = controller.getAllWrongWords();
            tableModel.setWrongWords(wrongWords);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "加载错题列表失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public List<WrongWord> getSelectedWrongWords() {
        int[] selectedRows = wrongWordTable.getSelectedRows();
        List<WrongWord> selectedWords = new ArrayList<>();
        for (int row : selectedRows) {
            selectedWords.add(tableModel.getWrongWordAt(row));
        }
        return selectedWords;
    }
    
    private void startPractice() {
        List<WrongWord> selectedWords = getSelectedWrongWords();
        if (selectedWords.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "请先选择要练习的单词",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // 创建练习对话框
        JDialog practiceDialog = new JDialog();
        practiceDialog.setTitle("错题练习");
        practiceDialog.setModal(true);
        practiceDialog.setSize(400, 300);
        
        WrongWordPracticePanel practicePanel = new WrongWordPracticePanel();
        practicePanel.setWrongWords(selectedWords);
        practicePanel.addPracticeListener(this);
        
        practiceDialog.add(practicePanel);
        practiceDialog.setLocationRelativeTo(this);
        practiceDialog.setVisible(true);
    }
    
    private void deleteSelectedWords() {
        int[] selectedRows = wrongWordTable.getSelectedRows();
        if (selectedRows.length == 0) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要删除选中的 " + selectedRows.length + " 个错题记录吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                for (int row : selectedRows) {
                    WrongWord word = tableModel.getWrongWordAt(row);
                    if (!controller.updateWrongWord(word)) {
                        throw new Exception("删除错题记录失败");
                    }
                }
                loadWrongWords(); // 重新加载数据
                JOptionPane.showMessageDialog(this,
                    "删除成功",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "删除失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportWrongWords() {
        // TODO: 实现导出功能
        JOptionPane.showMessageDialog(this,
            "导出功能开发中...",
            "提示",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public void onPracticeCompleted(List<WrongWord> practicedWords) {
        loadWrongWords();
    }
    
    private void showAnalysis() {
        List<WrongWord> wrongWords = tableModel.getWrongWords();
        if (wrongWords.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "当前没有错题记录！",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // 统计分析
        int totalWords = wrongWords.size();
        int masteredCount = 0;
        int highErrorCount = 0; // 错误次数>=3的单词
        
        Map<Integer, Integer> difficultyStats = new HashMap<>(); // 难度分布
        
        for (WrongWord word : wrongWords) {
            if (word.isMastered()) {
                masteredCount++;
            }
            if (word.getWrongCount() >= 3) {
                highErrorCount++;
            }
            
            int difficulty = word.getWord().getDifficulty();
            difficultyStats.merge(difficulty, 1, Integer::sum);
        }
        
        // 生成分析报告
        StringBuilder report = new StringBuilder();
        report.append("错题分析报告\n\n");
        report.append(String.format("总错题数：%d\n", totalWords));
        report.append(String.format("已掌握：%d (%.1f%%)\n", 
            masteredCount, (double)masteredCount/totalWords*100));
        report.append(String.format("高频错误：%d (%.1f%%)\n", 
            highErrorCount, (double)highErrorCount/totalWords*100));
        
        report.append("\n难度分布：\n");
        for (int i = 1; i <= 5; i++) {
            int count = difficultyStats.getOrDefault(i, 0);
            report.append(String.format("难度 %d：%d (%.1f%%)\n", 
                i, count, (double)count/totalWords*100));
        }
        
        // 显示分析报告
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this,
            scrollPane,
            "错题分析",
            JOptionPane.INFORMATION_MESSAGE);
    }
} 