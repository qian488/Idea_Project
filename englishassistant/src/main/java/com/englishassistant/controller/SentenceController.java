package com.englishassistant.controller;

import java.sql.*;
import java.util.*;

import com.englishassistant.model.Sentence;
import com.englishassistant.model.Word;
import com.englishassistant.util.DatabaseUtil;

/**
 * 例句管理控制器
 * 负责处理例句及其与单词关联的所有数据库操作和业务逻辑
 *
 * 主要功能:
 * 1. 例句基本操作
 *    - 添加例句(addSentence)
 *    - 获取所有例句(getAllSentences)
 *    - 搜索例句(searchSentences)
 *    - 更新例句(updateSentence)
 *    - 删除例句(deleteSentence)
 *
 * 2. 例句与单词关联
 *    - 添加单词关联(addWordAssociations)
 *    - 更新单词关联(updateSentenceAssociations)
 *    - 获取关联单词(getRelatedWords)
 *    - 自动关联单词(autoAssociateWords)
 *
 * 3. 数据维护
 *    - 刷新单词关联(refreshWordAssociations)
 *    - ID重排序(reorderIds)
 *    - 检查重复例句(isSentenceExists)
 *
 * 数据表结构:
 * sentences表:
 * - id: 主键
 * - english_sentence: 英文例句
 * - chinese_sentence: 中文翻译
 *
 * sentence_word表:
 * - sentence_id: 例句ID
 * - word_id: 单词ID
 */
public class SentenceController {
    private final WordController wordController = new WordController();
    
    public boolean addSentence(Sentence sentence) {
        String sql = "INSERT INTO sentences (english_sentence, chinese_sentence) VALUES (?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, sentence.getEnglishSentence());
                pstmt.setString(2, sentence.getChineseSentence());
                
                if (pstmt.executeUpdate() > 0) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        int sentenceId = rs.getInt(1);
                        addWordAssociations(conn, sentenceId, sentence.getRelatedWords());
                        conn.commit();
                        return true;
                    }
                }
                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("添加例句失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void addWordAssociations(Connection conn, int sentenceId, List<Word> words) throws SQLException {
        String sql = "INSERT INTO sentence_word (sentence_id, word_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Word word : words) {
                pstmt.setInt(1, sentenceId);
                pstmt.setInt(2, word.getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    public List<Sentence> getAllSentences() {
        List<Sentence> sentences = new ArrayList<>();
        String sql = """
            SELECT s.*, GROUP_CONCAT(w.id) as word_ids, 
                   GROUP_CONCAT(w.word) as words
            FROM sentences s
            LEFT JOIN sentence_word sw ON s.id = sw.sentence_id
            LEFT JOIN words w ON sw.word_id = w.id
            GROUP BY s.id
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Sentence sentence = new Sentence();
                sentence.setId(rs.getInt("id"));
                sentence.setEnglishSentence(rs.getString("english_sentence"));
                sentence.setChineseSentence(rs.getString("chinese_sentence"));
                
                String wordIds = rs.getString("word_ids");
                if (wordIds != null) {
                    String[] ids = wordIds.split(",");
                    String[] words = rs.getString("words").split(",");
                    for (int i = 0; i < ids.length; i++) {
                        Word word = new Word();
                        word.setId(Integer.parseInt(ids[i]));
                        word.setWord(words[i]);
                        sentence.addRelatedWord(word);
                    }
                }
                sentences.add(sentence);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sentences;
    }
    
    public List<Sentence> searchSentences(String keyword) {
        List<Sentence> sentences = new ArrayList<>();
        String sql = """
            SELECT s.*, GROUP_CONCAT(w.id) as word_ids, 
                   GROUP_CONCAT(w.word) as words
            FROM sentences s
            LEFT JOIN sentence_word sw ON s.id = sw.sentence_id
            LEFT JOIN words w ON sw.word_id = w.id
            WHERE s.english_sentence LIKE ? 
            OR s.chinese_sentence LIKE ? 
            OR w.word LIKE ?
            GROUP BY s.id
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Sentence sentence = new Sentence();
                sentence.setId(rs.getInt("id"));
                sentence.setEnglishSentence(rs.getString("english_sentence"));
                sentence.setChineseSentence(rs.getString("chinese_sentence"));
                
                String wordIds = rs.getString("word_ids");
                if (wordIds != null) {
                    String[] ids = wordIds.split(",");
                    String[] words = rs.getString("words").split(",");
                    for (int i = 0; i < ids.length; i++) {
                        Word word = new Word();
                        word.setId(Integer.parseInt(ids[i]));
                        word.setWord(words[i]);
                        sentence.addRelatedWord(word);
                    }
                }
                sentences.add(sentence);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sentences;
    }
    
    public boolean deleteSentence(int id) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 首先删除关联关系
                String deleteSentenceWordSql = "DELETE FROM sentence_word WHERE sentence_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSentenceWordSql)) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }
                
                // 然后删除例句
                String deleteSentenceSql = "DELETE FROM sentences WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSentenceSql)) {
                    pstmt.setInt(1, id);
                    if (pstmt.executeUpdate() > 0) {
                        conn.commit();
                        return true;
                    }
                }
                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void refreshWordAssociations() {
        String sql = """
            SELECT id, english_sentence 
            FROM sentences
        """;
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    int sentenceId = rs.getInt("id");
                    String englishSentence = rs.getString("english_sentence");
                    updateSentenceAssociations(conn, sentenceId, englishSentence);
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
    
    private void updateSentenceAssociations(Connection conn, int sentenceId, String englishSentence) throws SQLException {
        // 获取所有单词
        String getWordsSql = "SELECT id, word FROM words";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(getWordsSql)) {
            
            // 将例句分割成单词（考虑标点符号）
            String[] sentenceWords = englishSentence.toLowerCase()
                .replaceAll("[^a-zA-Z\\s]", "")
                .split("\\s+");
            
            // 检查每个单词是否在词库中
            while (rs.next()) {
                int wordId = rs.getInt("id");
                String wordText = rs.getString("word").toLowerCase();
                
                boolean found = false;
                for (String sentenceWord : sentenceWords) {
                    if (sentenceWord.equals(wordText)) {
                        found = true;
                        break;
                    }
                }
                
                if (found) {
                    // 检查关联是否已存在
                    String checkSql = "SELECT 1 FROM sentence_word WHERE sentence_id = ? AND word_id = ?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                        checkStmt.setInt(1, sentenceId);
                        checkStmt.setInt(2, wordId);
                        ResultSet checkRs = checkStmt.executeQuery();
                        
                        if (!checkRs.next()) {
                            // 添加新的关联
                            String insertSql = "INSERT INTO sentence_word (sentence_id, word_id) VALUES (?, ?)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                                insertStmt.setInt(1, sentenceId);
                                insertStmt.setInt(2, wordId);
                                insertStmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 获取例句关联的单词列表
     * @param sentenceId 例句ID
     * @return 关联的单词列表
     */
    public List<Word> getRelatedWords(int sentenceId) {
        List<Word> words = new ArrayList<>();
        String sql = """
            SELECT w.* 
            FROM words w
            JOIN sentence_word sw ON w.id = sw.word_id
            WHERE sw.sentence_id = ?
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sentenceId);
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
            System.err.println("获取例句关联单词失败: " + e.getMessage());
            e.printStackTrace();
        }
        return words;
    }
    
    /**
     * 更新例句及其关联的单词
     * @param sentence 要更新的例句对象
     * @return 更新是否成功
     */
    public boolean updateSentence(Sentence sentence) {
        String sql = "UPDATE sentences SET english_sentence = ?, chinese_sentence = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 更新例句基本信息
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, sentence.getEnglishSentence());
                    pstmt.setString(2, sentence.getChineseSentence());
                    pstmt.setInt(3, sentence.getId());
                    pstmt.executeUpdate();
                }
                
                // 删除旧的关联关系
                String deleteAssociationsSql = "DELETE FROM sentence_word WHERE sentence_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteAssociationsSql)) {
                    pstmt.setInt(1, sentence.getId());
                    pstmt.executeUpdate();
                }
                
                // 添加新的关联关系，使用Set避免重复
                if (sentence.getRelatedWords() != null && !sentence.getRelatedWords().isEmpty()) {
                    String insertAssociationSql = 
                        "INSERT INTO sentence_word (sentence_id, word_id) VALUES (?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertAssociationSql)) {
                        // 使用Set来去重
                        Set<Integer> addedWordIds = new HashSet<>();
                        for (Word word : sentence.getRelatedWords()) {
                            // 只有当这个word_id还没被添加过时才添加
                            if (addedWordIds.add(word.getId())) {
                                pstmt.setInt(1, sentence.getId());
                                pstmt.setInt(2, word.getId());
                                pstmt.addBatch();
                            }
                        }
                        pstmt.executeBatch();
                    }
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("更新例句失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 检查例句是否已存在
     * @param sentence 要检查的例句
     * @return 如果存在返回true，否则返回false
     */
    public boolean isSentenceExists(Sentence sentence) {
        String sql = "SELECT 1 FROM sentences WHERE english_sentence = ? COLLATE NOCASE";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, sentence.getEnglishSentence().trim());
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            System.err.println("检查例句是否存在时出错: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 如果例句不存在则添加
     * @param sentence 要添加的例句
     * @return 添加是否成功
     */
    public boolean addSentenceIfNotExists(Sentence sentence) {
        if (isSentenceExists(sentence)) {
            System.out.println("例句已存在，跳过: " + sentence.getEnglishSentence());
            return false;
        }
        return addSentence(sentence);
    }
    
    /**
     * 重新排序例句ID
     */
    public void reorderIds() {
        String sql = """
            UPDATE sentences
            SET id = (
                SELECT new_id
                FROM (
                    SELECT id, ROW_NUMBER() OVER (ORDER BY id) as new_id
                    FROM sentences
                ) t
                WHERE t.id = sentences.id
            )
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("重新排序ID失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void autoAssociateWords(Sentence sentence) {
        // 将句子内容转换为小写并分割成单词
        String[] sentenceWords = sentence.getEnglishSentence()
            .toLowerCase()
            // 将所有标点符号替换为空格
            .replaceAll("[^a-zA-Z\\s]", " ")
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
    }
} 