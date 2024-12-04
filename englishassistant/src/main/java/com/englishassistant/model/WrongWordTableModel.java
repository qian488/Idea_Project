package com.englishassistant.model;

import java.text.*;
import java.util.*;
import javax.swing.table.AbstractTableModel;

/**
 * 错题表格数据模型
 * 用于在JTable中展示错题信息
 *
 * 主要功能:
 * 1. 数据展示
 *    - 表格列定义
 *    - 数据格式化
 *    - 单元格渲染
 *
 * 2. 交互控制
 *    - 单元格编辑
 *    - 数据更新
 *    - 选择管理
 *
 * 表格列定义:
 * 1. 单词: 英文单词
 * 2. 翻译: 中文翻译
 * 3. 错误答案: 用户答案
 * 4. 错误次数: 统计次数
 * 5. 是否掌握: 掌握状态
 * 6. 错误时间: 最后错误时间
 */
public class WrongWordTableModel extends AbstractTableModel {
    // 数据源
    private List<WrongWord> wrongWords = new ArrayList<>();
    
    // 表格配置
    private final String[] columnNames = {"单词", "翻译", "错误答案", "错误次数", "是否掌握", "错误时间"};
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    // AbstractTableModel实现
    @Override
    public int getRowCount() { return wrongWords.size(); }
    
    @Override
    public int getColumnCount() { return columnNames.length; }
    
    @Override
    public String getColumnName(int column) { return columnNames[column]; }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        WrongWord wrongWord = wrongWords.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> wrongWord.getWord().getWord();
            case 1 -> wrongWord.getWord().getTranslation();
            case 2 -> wrongWord.getWrongAnswer();
            case 3 -> wrongWord.getWrongCount();
            case 4 -> wrongWord.isMastered() ? "是" : "否";
            case 5 -> dateFormat.format(wrongWord.getWrongTime());
            default -> null;
        };
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 4; // 只允许编辑"是否掌握"列
    }
    
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex == 4) {
            WrongWord wrongWord = wrongWords.get(rowIndex);
            wrongWord.setMastered(value.toString().equals("是"));
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
    
    // 数据操作方法
    public void setWrongWords(List<WrongWord> wrongWords) {
        this.wrongWords = wrongWords;
        fireTableDataChanged();
    }
    
    public List<WrongWord> getWrongWords() { return wrongWords; }
    
    public WrongWord getWrongWordAt(int row) { return wrongWords.get(row); }
} 