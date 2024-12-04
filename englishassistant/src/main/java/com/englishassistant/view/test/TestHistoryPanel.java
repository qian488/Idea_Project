package com.englishassistant.view.test;

import java.awt.*;
import java.text.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;

import com.englishassistant.controller.StatisticsController;
import com.englishassistant.controller.TestController;
import com.englishassistant.model.TestHistory;
import com.englishassistant.view.common.StyledPanel;

/**
 * 测试历史记录面板类
 * 提供测试历史记录的查看和管理功能
 * 
 * 功能说明:
 * 1. 数据展示
 *    - loadHistory(): 加载历史记录
 *    - updateStatistics(): 更新统计信息
 *    - deleteSelectedHistory(): 删除选中记录
 * 
 * 2. 统计功能
 *    - 总测试次数统计
 *    - 平均分计算
 *    - 最高分记录
 *    - 最近测试时间
 * 
 * 3. 界面组件
 *    - 工具栏: 刷新、导出、删除按钮
 *    - 数据表: 历史记录表格
 *    - 统计区: 统计信息显示
 * 
 * 4. 数据管理
 *    - 表格数据的增删改查
 *    - 统计信息的实时更新
 *    - 数据导出功能(预留)
 * 
 * @author Your Name
 */
public class TestHistoryPanel extends StyledPanel {
    private final TestController testController;
    private final StatisticsController statisticsController;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    // 统计信息标签
    private JLabel totalTestsLabel;
    private JLabel averageScoreLabel;
    private JLabel highestScoreLabel;
    private JLabel lastTestLabel;
    
    public TestHistoryPanel() {
        super();
        addStyledBorder("测试历史");
        testController = new TestController();
        statisticsController = new StatisticsController();
        initComponents();
        loadHistory();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // 创建表格
        String[] columnNames = {"测试时间", "难度", "题目数量", "正确数", "得分"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // 设置表格不可编辑
            }
        };
        
        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setRowHeight(25);  // 设置行高
        
        // 设置表格列宽和对齐方式
        TableColumnModel columnModel = historyTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(150);  // 测试时间
        columnModel.getColumn(1).setPreferredWidth(50);   // 难度
        columnModel.getColumn(2).setPreferredWidth(80);   // 题目数量
        columnModel.getColumn(3).setPreferredWidth(80);   // 正确数
        columnModel.getColumn(4).setPreferredWidth(80);   // 得分
        
        // 设置表格标题的字体和背景
        JTableHeader header = historyTable.getTableHeader();
        header.setFont(CONTENT_FONT.deriveFont(Font.BOLD));
        header.setBackground(new Color(240, 240, 240));
        
        // 创建工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton refreshButton = createStyledButton("刷新");
        JButton exportButton = createStyledButton("导出历史");
        JButton deleteButton = createStyledButton("删除");
        
        refreshButton.addActionListener(e -> loadHistory());
        exportButton.addActionListener(e -> exportHistory());
        deleteButton.addActionListener(e -> deleteSelectedHistory());
        
        toolBar.add(refreshButton);
        toolBar.add(exportButton);
        toolBar.add(deleteButton);
        
        // 创建统计面板
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "统计信息",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                TITLE_FONT
            ),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // 初始化统计信息标签
        totalTestsLabel = createStyledLabel("总测试次数：0");
        averageScoreLabel = createStyledLabel("平均分：0.0");
        highestScoreLabel = createStyledLabel("最高分：0.0");
        lastTestLabel = createStyledLabel("最近测试：无");
        
        statsPanel.add(totalTestsLabel);
        statsPanel.add(averageScoreLabel);
        statsPanel.add(highestScoreLabel);
        statsPanel.add(lastTestLabel);
        
        // 布局
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(toolBar, BorderLayout.CENTER);
        topPanel.add(statsPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(historyTable), BorderLayout.CENTER);
    }
    
    private void deleteSelectedHistory() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "请先选择要删除的记录",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要删除选中的测试记录吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: 实现删除功能
            loadHistory();
        }
    }
    
    public void loadHistory() {
        tableModel.setRowCount(0);
        List<TestHistory> historyList = testController.getTestHistory();
        
        for (TestHistory history : historyList) {
            Object[] row = {
                dateFormat.format(history.getTestTime()),
                history.getDifficulty(),
                history.getTotalQuestions(),
                history.getCorrectCount(),
                String.format("%.1f%%", history.getScore())
            };
            tableModel.addRow(row);
        }
        
        updateStatistics(historyList);
    }
    
    private void updateStatistics(List<TestHistory> historyList) {
        int totalTests = historyList.size();
        double averageScore = 0;
        double highestScore = 0;
        String lastTestTime = "无";
        
        if (!historyList.isEmpty()) {
            // 计算平均分和最高分
            double totalScore = 0;
            for (TestHistory history : historyList) {
                totalScore += history.getScore();
                highestScore = Math.max(highestScore, history.getScore());
            }
            averageScore = totalScore / totalTests;
            
            // 获取最近测试时间
            lastTestTime = dateFormat.format(historyList.get(0).getTestTime());
        }
        
        // 更新统计信息标签
        totalTestsLabel.setText(String.format("总测试次数：%d", totalTests));
        averageScoreLabel.setText(String.format("平均分：%.1f%%", averageScore));
        highestScoreLabel.setText(String.format("最高分：%.1f%%", highestScore));
        lastTestLabel.setText(String.format("最近测试：%s", lastTestTime));
    }
    
    private void exportHistory() {
        // TODO: 实现导出功能
        JOptionPane.showMessageDialog(this, "导出功能开发中...");
    }
} 