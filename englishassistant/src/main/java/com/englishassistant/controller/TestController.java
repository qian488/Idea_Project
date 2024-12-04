package com.englishassistant.controller;

import java.sql.*;
import java.util.*;

import javax.swing.*;

// import com.englishassistant.model.Test;
import com.englishassistant.model.TestHistory;
import com.englishassistant.model.Word;
import com.englishassistant.util.DatabaseUtil;

/**
 * 测试控制器
 * 负责处理单词测试相关的所有业务逻辑
 *
 * 主要功能:
 * 1. 测试题目生成(generateTest)
 *    - 按难度随机抽取单词
 *    - 生成测试题目
 *
 * 2. 测试历史管理
 *    - 保存测试历史(saveTestHistory)
 *    - 获取测试历史(getTestHistory)
 *    - 统计测试成绩
 *
 * 数据表结构:
 * test_history表:
 * - id: 主键
 * - test_time: 测试时间
 * - difficulty: 测试难度
 * - total_questions: 题目总数
 * - correct_count: 正确题数
 * - score: 得分
 */
public class TestController {
    
    // 生成测试题目
    public List<Word> generateTest(int count, int difficulty) {
        List<Word> testWords = new ArrayList<>();
        String sql = "SELECT * FROM words WHERE difficulty = ? ORDER BY RANDOM() LIMIT ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, difficulty);
            stmt.setInt(2, count);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Word word = new Word(
                    rs.getString("word"),
                    rs.getString("translation"),
                    rs.getString("phonetic"),
                    rs.getInt("difficulty")
                );
                word.setId(rs.getInt("id"));
                testWords.add(word);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return testWords;
    }
    
    // 保存测试历史
    public void saveTestHistory(TestHistory history) {
        String sql = "INSERT INTO test_history (test_time, difficulty, total_questions, correct_count, score) " +
                    "VALUES (?, ?, ?, ?, ?)";
                    
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, new Timestamp(history.getTestTime().getTime()));
            stmt.setInt(2, history.getDifficulty());
            stmt.setInt(3, history.getTotalQuestions());
            stmt.setInt(4, history.getCorrectCount());
            stmt.setDouble(5, history.getScore());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // 获取测试历史记录
    public List<TestHistory> getTestHistory() {
        List<TestHistory> historyList = new ArrayList<>();
        String sql = "SELECT * FROM test_history ORDER BY test_time DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                TestHistory history = new TestHistory();
                history.setId(rs.getInt("id"));
                history.setTestTime(rs.getTimestamp("test_time"));
                history.setTotalQuestions(rs.getInt("total_questions"));
                history.setCorrectCount(rs.getInt("correct_count"));
                history.setScore(rs.getDouble("score"));
                history.setDifficulty(rs.getInt("difficulty"));
                historyList.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "加载测试历史失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
        return historyList;
    }
} 