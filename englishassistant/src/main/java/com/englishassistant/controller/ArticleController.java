package com.englishassistant.controller;

import java.io.*;
import java.sql.*;
import java.util.*;

import com.englishassistant.model.Article;
import com.englishassistant.model.Word;
import com.englishassistant.util.ArticleExportImportUtil;
import com.englishassistant.util.DatabaseUtil;

/**
 * 文章管理控制器
 * 负责处理文章相关的所有数据库操作和业务逻辑
 *
 * 主要功能:
 * 1. 文章基本操作
 *    - 添加新文章(addArticle)
 *    - 获取所有文章(getAllArticles)
 *    - 搜索文章(searchArticles)
 *    - 更新文章(updateArticle)
 *    - 删除文章(deleteArticle)
 * 
 * 2. 文章与单词关联
 *    - 添加单词关联(addWordAssociations)
 *    - 更新单词关联(updateWordAssociations)
 *    - 加载关联单词(loadRelatedWords)
 *
 * 3. 导入导出功能
 *    - 导入文章(importArticles)
 *    - 导出文章(exportArticles)
 *    - 检查重复文章(isArticleExists)
 *
 * 4. 数据维护
 *    - ID重排序(reorderIds)
 *    - 阅读进度更新(updateReadProgress)
 *
 * 数据表结构:
 * articles表:
 * - id: 主键
 * - title: 文章标题
 * - content: 文章内容
 * - translation: 中文翻译
 * - difficulty: 难度等级
 * - create_time: 创建时间
 * - notes: 笔记
 * - read_progress: 阅读进度
 * - last_read_time: 最后阅读时间
 */
public class ArticleController {
    private final WordController wordController = new WordController();
    
    public boolean addArticle(Article article) {
        String sql = "INSERT INTO articles (title, content, translation, difficulty, create_time, notes, read_progress, last_read_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, article.getTitle());
                pstmt.setString(2, article.getContent());
                pstmt.setString(3, article.getTranslation());
                pstmt.setInt(4, article.getDifficulty());
                pstmt.setTimestamp(5, new Timestamp(article.getCreateTime().getTime()));
                pstmt.setString(6, article.getNotes());
                pstmt.setInt(7, article.getReadProgress());
                pstmt.setTimestamp(8, article.getLastReadTime() != null ? 
                    new Timestamp(article.getLastReadTime().getTime()) : null);
                
                if (pstmt.executeUpdate() > 0) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        int articleId = rs.getInt(1);
                        addWordAssociations(conn, articleId, article.getRelatedWords());
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
            System.err.println("添加文章失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void addWordAssociations(Connection conn, int articleId, List<Word> words) throws SQLException {
        // 先删除现有的关联
        String deleteSql = "DELETE FROM article_word WHERE article_id = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, articleId);
            deleteStmt.executeUpdate();
        }
        
        // 使用Set来防止重复的单词关联
        Set<Integer> addedWordIds = new HashSet<>();
        
        // 添加新的关联
        String insertSql = "INSERT INTO article_word (article_id, word_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (Word word : words) {
                // 检查这个单词是否已经添加过
                if (!addedWordIds.contains(word.getId())) {
                    pstmt.setInt(1, articleId);
                    pstmt.setInt(2, word.getId());
                    pstmt.addBatch();
                    addedWordIds.add(word.getId());
                }
            }
            // 只有当有需要添加的关联时才执行batch
            if (!addedWordIds.isEmpty()) {
                pstmt.executeBatch();
            }
        }
    }
    
    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        String sql = """
            SELECT a.*, GROUP_CONCAT(w.id) as word_ids
            FROM articles a
            LEFT JOIN article_word aw ON a.id = aw.article_id
            LEFT JOIN words w ON aw.word_id = w.id
            GROUP BY a.id
            ORDER BY a.create_time DESC
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Article article = new Article();
                article.setId(rs.getInt("id"));
                article.setTitle(rs.getString("title"));
                article.setContent(rs.getString("content"));
                article.setTranslation(rs.getString("translation"));
                article.setDifficulty(rs.getInt("difficulty"));
                article.setCreateTime(rs.getTimestamp("create_time"));
                article.setNotes(rs.getString("notes"));
                article.setReadProgress(rs.getInt("read_progress"));
                article.setLastReadTime(rs.getTimestamp("last_read_time"));
                
                // 处理关联的单词
                String wordIds = rs.getString("word_ids");
                if (wordIds != null && !wordIds.trim().isEmpty()) {
                    for (String idStr : wordIds.split(",")) {
                        try {
                            idStr = idStr.trim();
                            if (!idStr.isEmpty()) {
                                Word word = wordController.getWordById(Integer.parseInt(idStr));
                                if (word != null) {
                                    article.addRelatedWord(word);
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("跳过无效的单词ID: " + idStr);
                        }
                    }
                }
                
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("获取文章列表失败: " + e.getMessage());
            e.printStackTrace();
        }
        return articles;
    }
    
    public List<Article> searchArticles(String keyword) {
        List<Article> articles = new ArrayList<>();
        String sql = """
            SELECT DISTINCT a.* FROM articles a
            LEFT JOIN article_word aw ON a.id = aw.article_id
            LEFT JOIN words w ON aw.word_id = w.id
            WHERE a.title LIKE ? OR a.content LIKE ? OR a.translation LIKE ?
            ORDER BY a.create_time DESC
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Article article = new Article();
                article.setId(rs.getInt("id"));
                article.setTitle(rs.getString("title"));
                article.setContent(rs.getString("content"));
                article.setTranslation(rs.getString("translation"));
                article.setDifficulty(rs.getInt("difficulty"));
                article.setCreateTime(rs.getTimestamp("create_time"));
                article.setNotes(rs.getString("notes"));
                
                // 加载关联的单词
                loadRelatedWords(article);
                
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("搜索文章失败: " + e.getMessage());
            e.printStackTrace();
        }
        return articles;
    }
    
    private void loadRelatedWords(Article article) {
        String sql = """
            SELECT w.* FROM words w
            JOIN article_word aw ON w.id = aw.word_id
            WHERE aw.article_id = ?
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, article.getId());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Word word = new Word();
                word.setId(rs.getInt("id"));
                word.setWord(rs.getString("word"));
                word.setTranslation(rs.getString("translation"));
                word.setPhonetic(rs.getString("phonetic"));
                word.setDifficulty(rs.getInt("difficulty"));
                article.addRelatedWord(word);
            }
        } catch (SQLException e) {
            System.err.println("加载文章关联单词失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean deleteArticle(int id) {
        String sql = "DELETE FROM articles WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("删除文章失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateArticle(Article article) {
        String sql = """
            UPDATE articles 
            SET title = ?, content = ?, translation = ?, 
                difficulty = ?, notes = ?, read_progress = ?, 
                last_read_time = ?
            WHERE id = ?
        """;
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, article.getTitle());
                pstmt.setString(2, article.getContent());
                pstmt.setString(3, article.getTranslation());
                pstmt.setInt(4, article.getDifficulty());
                pstmt.setString(5, article.getNotes());
                pstmt.setInt(6, article.getReadProgress());
                pstmt.setTimestamp(7, article.getLastReadTime() != null ? 
                    new Timestamp(article.getLastReadTime().getTime()) : null);
                pstmt.setInt(8, article.getId());
                
                if (pstmt.executeUpdate() > 0) {
                    // 更新单词关联
                    updateWordAssociations(conn, article);
                    conn.commit();
                    return true;
                }
                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("更新文章失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void updateWordAssociations(Connection conn, Article article) throws SQLException {
        // 删除旧的关联
        String deleteSql = "DELETE FROM article_word WHERE article_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, article.getId());
            pstmt.executeUpdate();
        }
        
        // 添加新的关联
        if (!article.getRelatedWords().isEmpty()) {
            addWordAssociations(conn, article.getId(), article.getRelatedWords());
        }
    }
    
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
            System.err.println("获取单词失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 刷新文章的单词关联
     * @param article 要刷新的文章
     */
    public void refreshWordAssociations(Article article) {
        String sql = """
            SELECT w.* FROM words w
            WHERE LOWER(?) LIKE LOWER(CONCAT('%', w.word, '%'))
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, article.getContent());
            ResultSet rs = pstmt.executeQuery();
            
            // 清除现有关联
            article.getRelatedWords().clear();
            
            // 添加新的关联
            while (rs.next()) {
                Word word = new Word();
                word.setId(rs.getInt("id"));
                word.setWord(rs.getString("word"));
                word.setTranslation(rs.getString("translation"));
                word.setPhonetic(rs.getString("phonetic"));
                word.setDifficulty(rs.getInt("difficulty"));
                article.addRelatedWord(word);
            }
            
            // 更新数据库中的关联关系
            updateWordAssociations(conn, article);
            
        } catch (SQLException e) {
            System.err.println("刷新文章单词关联失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 刷新所有文章的单词关联
     */
    public void refreshAllArticleWordAssociations() {
        List<Article> articles = getAllArticles();
        for (Article article : articles) {
            refreshWordAssociations(article);
        }
    }
    
    public boolean updateReadProgress(int articleId, int progress) {
        String sql = "UPDATE articles SET read_progress = ?, last_read_time = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, progress);
            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(3, articleId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("更新阅读进度失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void loadFromResultSet(Article article, ResultSet rs) throws SQLException {
        article.setId(rs.getInt("id"));
        article.setTitle(rs.getString("title"));
        article.setContent(rs.getString("content"));
        article.setTranslation(rs.getString("translation"));
        article.setDifficulty(rs.getInt("difficulty"));
        article.setCreateTime(rs.getTimestamp("create_time"));
        article.setNotes(rs.getString("notes"));
        article.setReadProgress(rs.getInt("read_progress"));
        article.setLastReadTime(rs.getTimestamp("last_read_time"));
    }
    
    // 添加初始化数据库表的方法
    public void initTable() {
        // 创建新表（不除旧表，保留数据）
        String[] createTables = {
            """
            CREATE TABLE IF NOT EXISTS articles (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                translation TEXT,
                difficulty INTEGER DEFAULT 1,
                create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                notes TEXT,
                read_progress INTEGER DEFAULT 0,
                last_read_time TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS article_word (
                article_id INTEGER,
                word_id INTEGER,
                PRIMARY KEY (article_id, word_id),
                FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
                FOREIGN KEY (word_id) REFERENCES words(id) ON DELETE CASCADE
            )
            """
        };
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 创建表（如果不存在）
            for (String sql : createTables) {
                stmt.execute(sql);
            }
            System.out.println("文章相关数据表初始化成功");
        } catch (SQLException e) {
            System.err.println("初始化文章表失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean exportArticles(String filePath, List<Article> articles, boolean isJson) throws IOException {
        try {
            if (isJson) {
                ArticleExportImportUtil.exportToJson(filePath, articles);
            } else {
                ArticleExportImportUtil.exportToTxt(filePath, articles);
            }
            return true;
        } catch (IOException e) {
            System.err.println("导出文章失败: " + e.getMessage());
            throw e;
        }
    }
    
    public List<Article> importArticles(String filePath, boolean isJson) throws IOException {
        try {
            List<Article> articles;
            if (isJson) {
                articles = ArticleExportImportUtil.importFromJson(filePath);
            } else {
                articles = ArticleExportImportUtil.importFromTxt(filePath);
            }
            
            // 确保导入的文章被保存到数据库
            if (articles != null && !articles.isEmpty()) {
                int successCount = 0;
                for (Article article : articles) {
                    if (addArticleIfNotExists(article)) {  // 使用新的方法避免重复添加
                        successCount++;
                    }
                }
                
                // 重新排序ID
                if (successCount > 0) {
                    reorderIds();
                }
                
                System.out.printf("成功导入 %d/%d 篇文章%n", successCount, articles.size());
            }
            
            return articles;
        } catch (IOException e) {
            System.err.println("导入文章失败: " + e.getMessage());
            throw e;
        }
    }
    
    public void reorderIds() {
        String sql = """
            UPDATE articles
            SET id = (
                SELECT new_id
                FROM (
                    SELECT id, ROW_NUMBER() OVER (ORDER BY id) as new_id
                    FROM articles
                ) t
                WHERE t.id = articles.id
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
    
    public boolean isArticleExists(Article article) {
        String sql = "SELECT 1 FROM articles WHERE title = ? COLLATE NOCASE";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, article.getTitle().trim());
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            System.err.println("检查文章是否存在时出错: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean addArticleIfNotExists(Article article) {
        if (isArticleExists(article)) {
            System.out.println("文章已存在，跳过: " + article.getTitle());
            return false;
        }
        return addArticle(article);
    }
} 