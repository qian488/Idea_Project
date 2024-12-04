package com.englishassistant.model;

import java.util.*;

/**
 * 文章实体类
 * 用于存储和管理英语文章的完整信息
 *
 * 主要功能:
 * 1. 基本信息管理
 *    - 文章标题、内容、翻译
 *    - 难度等级(1-5)
 *    - 创建时间
 *
 * 2. 学习状态跟踪
 *    - 阅读进度(0-100)
 *    - 最后阅读时间
 *    - 学习笔记
 *
 * 3. 单词关联
 *    - 管理关联的生词列表
 *    - 添加/移除关联单词
 *
 * 数据表字段:
 * - id: INTEGER PRIMARY KEY
 * - title: TEXT NOT NULL
 * - content: TEXT NOT NULL
 * - translation: TEXT
 * - difficulty: INTEGER
 * - create_time: TIMESTAMP
 * - notes: TEXT
 * - read_progress: INTEGER
 * - last_read_time: TIMESTAMP
 */
public class Article {
    // 数据库ID
    private int id;
    
    // 基本属性
    private String title;
    private String content;
    private String translation;
    private int difficulty;
    
    // 状态属性
    private Date createTime;
    private String notes;
    private List<Word> relatedWords;
    private int readProgress;
    private Date lastReadTime;
    
    // 构造函数
    public Article() {
        this.createTime = new Date();
        this.relatedWords = new ArrayList<>();
        this.readProgress = 0;
        this.lastReadTime = null;
    }
    
    public Article(String title, String content, String translation, int difficulty) {
        this();
        this.title = title;
        this.content = content;
        this.translation = translation;
        this.difficulty = difficulty;
    }
    
    // Getter和Setter方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }
    
    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public List<Word> getRelatedWords() { return relatedWords; }
    public void setRelatedWords(List<Word> relatedWords) { this.relatedWords = relatedWords; }
    
    public int getReadProgress() { return readProgress; }
    public void setReadProgress(int readProgress) { 
        this.readProgress = Math.min(100, Math.max(0, readProgress)); 
    }
    
    public Date getLastReadTime() { return lastReadTime; }
    public void setLastReadTime(Date lastReadTime) { this.lastReadTime = lastReadTime; }
    
    // 业务方法
    public void addRelatedWord(Word word) {
        if (!relatedWords.contains(word)) {
            relatedWords.add(word);
        }
    }
    
    public void updateReadProgress(int progress) {
        setReadProgress(progress);
        setLastReadTime(new Date());
    }
    
    @Override
    public String toString() {
        return String.format("%s (难度: %d)", title, difficulty);
    }
} 