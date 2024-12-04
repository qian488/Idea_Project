package com.englishassistant.controller;

import java.sql.*;
import java.util.*;

import com.englishassistant.util.DatabaseUtil;

/**
 * 统计分析控制器
 * 负责提供学习数据的统计分析功能
 *
 * 主要功能:
 * 1. 学习进度统计(getLearningProgress)
 *    - 总单词数统计
 *    - 已掌握单词数
 *    - 单词难度分布
 *    - 最近测试成绩
 *
 * 2. 错题分析(getErrorAnalysis)
 *    - 错误次数最多的单词
 *    - 错题分布统计
 *    - 掌握程度分析
 *
 * 3. 学习时间分析(getLearningTimeDistribution)
 *    - 每日学习时间分布
 *    - 学习频率统计
 *
 * 数据来源:
 * - words表: 单词总量和难度分布
 * - wrong_words表: 错题统计
 * - test_history表: 测试成绩分析
 */
public class StatisticsController {
    
    public Map<String, Object> getLearningProgress() {
        Map<String, Object> stats = new HashMap<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            // 总单词数
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM words")) {
                if (rs.next()) {
                    stats.put("totalWords", rs.getInt("total"));
                }
            }
            
            // 已掌握单词数
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT COUNT(*) as mastered FROM wrong_words WHERE mastered = 1")) {
                if (rs.next()) {
                    stats.put("masteredWords", rs.getInt("mastered"));
                }
            }
            
            // 难度分布
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT difficulty, COUNT(*) as count FROM words GROUP BY difficulty")) {
                Map<Integer, Integer> difficultyDistribution = new HashMap<>();
                while (rs.next()) {
                    difficultyDistribution.put(
                        rs.getInt("difficulty"), 
                        rs.getInt("count")
                    );
                }
                stats.put("difficultyDistribution", difficultyDistribution);
            }
            
            // 最近7天的测试成绩
            try (PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT test_date, score FROM test_history " +
                     "WHERE test_date >= date('now', '-7 days')")) {
                ResultSet rs = pstmt.executeQuery();
                List<Map<String, Object>> recentScores = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> score = new HashMap<>();
                    score.put("date", rs.getDate("test_date"));
                    score.put("score", rs.getDouble("score"));
                    recentScores.add(score);
                }
                stats.put("recentScores", recentScores);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    public Map<String, Object> getErrorAnalysis() {
        Map<String, Object> analysis = new HashMap<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            // 错误次数最多的单词
            String sql = """
                SELECT w.word, w.translation, COUNT(*) as error_count
                FROM wrong_words ww
                JOIN words w ON ww.word_id = w.id
                GROUP BY w.id
                ORDER BY error_count DESC
                LIMIT 10
            """;
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                List<Map<String, Object>> mostErrors = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> wordError = new HashMap<>();
                    wordError.put("word", rs.getString("word"));
                    wordError.put("translation", rs.getString("translation"));
                    wordError.put("errorCount", rs.getInt("error_count"));
                    mostErrors.add(wordError);
                }
                analysis.put("mostErrors", mostErrors);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return analysis;
    }
    
    public Map<String, Integer> getLearningTimeDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = """
                SELECT strftime('%H', test_date) as hour,
                       COUNT(*) as count
                FROM test_history
                GROUP BY hour
                ORDER BY hour
            """;
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    distribution.put(
                        rs.getString("hour"),
                        rs.getInt("count")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return distribution;
    }
} 