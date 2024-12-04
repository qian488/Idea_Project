package com.englishassistant.util;

import java.io.*;
import java.sql.*;

/**
 * 数据库工具类
 * 负责数据库连接和初始化
 *
 * 主要功能:
 * 1. 数据库连接
 *    - 获取连接(getConnection)
 *    - 初始化数据库(initDatabase)
 *
 * 2. 表结构管理
 *    - 自动创建表
 *    - 表结构更新
 *    - 数据完整性维护
 *
 * 特点:
 * - SQLite数据库支持
 * - 自动创建数据目录
 * - 表结构自动升级
 * - 事务支持
 */
public class DatabaseUtil {
    private static final String DB_DIR = "data";
    private static final String DB_NAME = "english_assistant.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_DIR + File.separator + DB_NAME;
    
    static {
        // 确保数据目录存在
        File dbDir = new File(DB_DIR);
        if (!dbDir.exists()) {
            if (!dbDir.mkdirs()) {
                System.err.println("无法创建数据库目录");
            }
        }
    }
    
    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        try {
            // 确保SQLite JDBC驱动被加载
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC驱动未找到", e);
        }
    }
    
    public static void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 创建单词表
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS words (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    word TEXT NOT NULL,
                    translation TEXT NOT NULL,
                    phonetic TEXT,
                    difficulty INTEGER
                )
            """);
            
            // 创建例句表
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS sentences (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    english_sentence TEXT NOT NULL,
                    chinese_sentence TEXT NOT NULL
                )
            """);
            
            // 创建句子-单词关联表
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS sentence_word (
                    sentence_id INTEGER,
                    word_id INTEGER,
                    PRIMARY KEY (sentence_id, word_id),
                    FOREIGN KEY (sentence_id) REFERENCES sentences(id),
                    FOREIGN KEY (word_id) REFERENCES words(id)
                )
            """);
            
            // 创建错题本表
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS wrong_words (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    word_id INTEGER NOT NULL,
                    wrong_answer TEXT,
                    wrong_time TIMESTAMP,
                    wrong_count INTEGER DEFAULT 1,
                    mastered BOOLEAN DEFAULT 0,
                    FOREIGN KEY (word_id) REFERENCES words(id)
                )
            """);
            
            // 创建文章表
            stmt.execute("""
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
            """);
            
            // 检查是否需要添加新列
            try {
                // 尝试查询新列，如果不存在会抛出异常
                stmt.executeQuery("SELECT read_progress FROM articles LIMIT 1");
            } catch (SQLException e) {
                // 如果列不存在，添加新列
                stmt.execute("ALTER TABLE articles ADD COLUMN read_progress INTEGER DEFAULT 0");
                stmt.execute("ALTER TABLE articles ADD COLUMN last_read_time TIMESTAMP");
                System.out.println("已添加新列 read_progress 和 last_read_time 到 articles 表");
            }
            
            // 创建测试历史表
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS test_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    test_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    difficulty INTEGER,
                    total_questions INTEGER,
                    correct_count INTEGER,
                    score REAL,
                    test_details TEXT
                )
            """);
            
            System.out.println("数据库初始化成功");
            
        } catch (SQLException e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 