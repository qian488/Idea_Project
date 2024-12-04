package com.englishassistant.model;

/**
 * 单词实体类
 * 用于存储和管理英语单词的基本信息
 *
 * 主要功能:
 * 1. 基本信息管理
 *    - 英文单词
 *    - 中文翻译
 *    - 音标记录
 *    - 难度等级
 *
 * 2. 数据展示
 *    - 格式化输出
 *    - 信息整合展示
 *
 * 数据表字段:
 * - id: INTEGER PRIMARY KEY
 * - word: TEXT NOT NULL
 * - translation: TEXT NOT NULL
 * - phonetic: TEXT
 * - difficulty: INTEGER
 */
public class Word {
    // 数据库ID
    private int id;
    
    // 基本属性
    private String word;
    private String translation;
    private String phonetic;
    private int difficulty;

    // 构造函数
    public Word() {}

    public Word(String word, String translation, String phonetic, int difficulty) {
        this.word = word;
        this.translation = translation;
        this.phonetic = phonetic;
        this.difficulty = difficulty;
    }

    // Getter和Setter方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }

    public String getPhonetic() { return phonetic; }
    public void setPhonetic(String phonetic) { this.phonetic = phonetic; }

    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }

    @Override
    public String toString() {
        return String.format("%s - %s [%s] (难度: %d)", 
            word, translation, phonetic, difficulty);
    }
} 