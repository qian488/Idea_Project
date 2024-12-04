package com.englishassistant.controller;

import java.sql.*;
import java.util.*;

import com.englishassistant.model.Word;
import com.englishassistant.util.DatabaseUtil;

/**
 * 单词管理控制器
 * 负责处理单词的所有数据库操作和业务逻辑
 *
 * 主要功能:
 * 1. 单词基本操作
 *    - 添加单词(addWord)
 *    - 获取所有单词(getAllWords)
 *    - 搜索单词(searchWords)
 *    - 更新单词(updateWord)
 *    - 删除单词(deleteWord)
 *
 * 2. 单词查询
 *    - 精确匹配查找(getWordByExactMatch)
 *    - 根据ID获取(getWordById)
 *
 * 3. 数据维护
 *    - 导入单词(addWordIfNotExists)
 *    - ID重排序(reorderIds)
 *
 * 数据表结构:
 * words表:
 * - id: 主键
 * - word: 英文单词
 * - translation: 中文翻译
 * - phonetic: 音标
 * - difficulty: 难度等级
 */
public class WordController {
    
    public boolean addWord(Word word) {
        String sql = "INSERT INTO words (word, translation, phonetic, difficulty) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, word.getWord());
            pstmt.setString(2, word.getTranslation());
            pstmt.setString(3, word.getPhonetic());
            pstmt.setInt(4, word.getDifficulty());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("添加单词失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Word> getAllWords() {
        List<Word> words = new ArrayList<>();
        String sql = "SELECT * FROM words";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Word word = new Word();
                word.setId(rs.getInt("id"));
                word.setWord(rs.getString("word"));
                word.setTranslation(rs.getString("translation"));
                word.setPhonetic(rs.getString("phonetic"));
                word.setDifficulty(rs.getInt("difficulty"));
                words.add(word);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return words;
    }
    
    public List<Word> searchWords(String keyword) {
        List<Word> words = new ArrayList<>();
        String sql = """
            SELECT * FROM words 
            WHERE word LIKE ? OR translation LIKE ?
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Word word = new Word();
                word.setId(rs.getInt("id"));
                word.setWord(rs.getString("word"));
                word.setTranslation(rs.getString("translation"));
                word.setPhonetic(rs.getString("phonetic"));
                word.setDifficulty(rs.getInt("difficulty"));
                words.add(word);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return words;
    }
    
    public boolean deleteWord(int id) {
        String sql = "DELETE FROM words WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateWord(Word word) {
        String sql = "UPDATE words SET word = ?, translation = ?, difficulty = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, word.getWord());
            pstmt.setString(2, word.getTranslation());
            pstmt.setInt(3, word.getDifficulty());
            pstmt.setInt(4, word.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("修改单词失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean addWordIfNotExists(Word word) {
        // 首先验证数据
        if (word.getWord() == null || word.getWord().trim().isEmpty()) {
            System.err.println("单词为空，跳过导入");
            return false;
        }
        if (word.getTranslation() == null || word.getTranslation().trim().isEmpty()) {
            System.err.println("翻译为空，跳过导入: " + word.getWord());
            return false;
        }

        System.out.println("正在导入单词: " + word.getWord() + ", 翻译: " + word.getTranslation());

        String checkSql = "SELECT id FROM words WHERE word = ?";
        String insertSql = "INSERT INTO words (word, translation, phonetic, difficulty) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            // 检查是否存在
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, word.getWord().trim());
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    System.out.println("单词已存在，跳过导入: " + word.getWord());
                    return false;
                }
            }
            
            // 插入新单词
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                String wordText = word.getWord().trim();
                String translation = word.getTranslation().trim();
                String phonetic = word.getPhonetic() != null ? word.getPhonetic().trim() : "";
                int difficulty = word.getDifficulty() > 0 ? word.getDifficulty() : 1;

                insertStmt.setString(1, wordText);
                insertStmt.setString(2, translation);
                insertStmt.setString(3, phonetic);
                insertStmt.setInt(4, difficulty);
                
                int result = insertStmt.executeUpdate();
                if (result > 0) {
                    System.out.println("成功导入单词: " + wordText);
                    return true;
                } else {
                    System.err.println("导入单词失败: " + wordText);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("导入单词出错: " + word.getWord());
            System.err.println("错误信息: " + e.getMessage());
            System.err.println("SQL状态: " + e.getSQLState());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 重新排序所有单词的ID
     */
    public void reorderIds() {
        String sql = """
            CREATE TABLE words_temp AS 
            SELECT NULL as id, word, translation, phonetic, difficulty 
            FROM words 
            ORDER BY difficulty, word COLLATE NOCASE;
            
            DROP TABLE words;
            
            CREATE TABLE words (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                word TEXT NOT NULL,
                translation TEXT NOT NULL,
                phonetic TEXT,
                difficulty INTEGER
            );
            
            INSERT INTO words (word, translation, phonetic, difficulty)
            SELECT word, translation, phonetic, difficulty FROM words_temp;
            
            DROP TABLE words_temp;
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            conn.setAutoCommit(false);
            try {
                for (String command : sql.split(";")) {
                    if (!command.trim().isEmpty()) {
                        stmt.execute(command.trim());
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 精确匹配查找单词
     * @param word 要查找的单词
     * @return 如果找到返回Word对象，否则返回null
     */
    public Word getWordByExactMatch(String word) {
        if (word == null || word.trim().isEmpty()) {
            return null;
        }
        
        String sql = "SELECT * FROM words WHERE word = ? COLLATE NOCASE";  // 添加COLLATE NOCASE使查询不区分大小写
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, word.trim());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Word result = new Word();
                result.setId(rs.getInt("id"));
                result.setWord(rs.getString("word"));
                result.setTranslation(rs.getString("translation"));
                result.setPhonetic(rs.getString("phonetic"));
                result.setDifficulty(rs.getInt("difficulty"));
                return result;
            }
        } catch (SQLException e) {
            System.err.println("精确匹配查找单词失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 根据ID获取单词
     * @param id 单词ID
     * @return 如果找到返回Word对象，否则返回null
     */
    public Word getWordById(int id) {
        String sql = "SELECT * FROM words WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Word word = new Word();
                word.setId(rs.getInt("id"));
                word.setWord(rs.getString("word"));
                word.setTranslation(rs.getString("translation"));
                word.setPhonetic(rs.getString("phonetic"));
                word.setDifficulty(rs.getInt("difficulty"));
                return word;
            }
        } catch (SQLException e) {
            System.err.println("根据ID获取单词失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
} 