package com.englishassistant.controller;

import java.sql.*;
import java.util.*;

import com.englishassistant.model.Word;
import com.englishassistant.model.WrongWord;
import com.englishassistant.util.DatabaseUtil;

/**
 * 错题本控制器
 * 负责管理用户的错题记录和相关业务逻辑
 *
 * 主要功能:
 * 1. 错题记录管理
 *    - 添加错题记录(addWrongWord)
 *    - 获取所有错题(getAllWrongWords)
 *    - 更新错题状态(updateWrongWord)
 *
 * 2. 错题分析
 *    - 错题统计
 *    - 掌握度跟踪
 *    - 复习提醒
 *
 * 数据表结构:
 * wrong_words表:
 * - id: 主键
 * - word_id: 单词ID(关联words表)
 * - wrong_answer: 错误答案
 * - wrong_time: 错误时间
 * - wrong_count: 错误次数
 * - mastered: 是否已掌握
 */
public class WrongWordController {
    
    public WrongWordController() {
        // 默认构造函数
    }
    
    public boolean addWrongWord(WrongWord wrongWord) {
        String sql = "INSERT INTO wrong_words (word_id, wrong_answer, wrong_time, wrong_count, mastered) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, wrongWord.getWord().getId());
            pstmt.setString(2, wrongWord.getWrongAnswer());
            pstmt.setTimestamp(3, new Timestamp(wrongWord.getWrongTime().getTime()));
            pstmt.setInt(4, wrongWord.getWrongCount());
            pstmt.setBoolean(5, wrongWord.isMastered());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<WrongWord> getAllWrongWords() {
        List<WrongWord> wrongWords = new ArrayList<>();
        String sql = """
            SELECT wrong_words.*, words.word, words.translation, words.difficulty
            FROM wrong_words
            JOIN words ON wrong_words.word_id = words.id
            ORDER BY wrong_words.wrong_time DESC
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Word word = new Word(
                    rs.getString("word"),
                    rs.getString("translation"),
                    "",
                    rs.getInt("difficulty")
                );
                word.setId(rs.getInt("word_id"));
                
                WrongWord wrongWord = new WrongWord(word, rs.getString("wrong_answer"));
                wrongWord.setId(rs.getInt("id"));
                wrongWord.setWrongTime(rs.getTimestamp("wrong_time"));
                wrongWord.setWrongCount(rs.getInt("wrong_count"));
                wrongWord.setMastered(rs.getBoolean("mastered"));
                
                wrongWords.add(wrongWord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wrongWords;
    }
    
    public boolean updateWrongWord(WrongWord wrongWord) {
        String sql = "UPDATE wrong_words SET wrong_count = ?, wrong_time = ?, mastered = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, wrongWord.getWrongCount());
            pstmt.setTimestamp(2, new Timestamp(wrongWord.getWrongTime().getTime()));
            pstmt.setBoolean(3, wrongWord.isMastered());
            pstmt.setInt(4, wrongWord.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 