package com.englishassistant.model;

import java.util.*;

/**
 * 例句实体类
 * 用于存储和管理英语例句及其关联信息
 *
 * 主要功能:
 * 1. 基本信息管理
 *    - 英文例句
 *    - 中文翻译
 *    - 音频路径(预留)
 *
 * 2. 单词关联
 *    - 管理关联的单词列表
 *    - 添加/移除关联单词
 *
 * 数据表字段:
 * sentences表:
 * - id: INTEGER PRIMARY KEY
 * - english_sentence: TEXT NOT NULL
 * - chinese_sentence: TEXT
 * - audio_path: TEXT
 *
 * sentence_word表:
 * - sentence_id: INTEGER
 * - word_id: INTEGER
 */
public class Sentence {
    // 数据库ID
    private int id;
    
    // 基本属性
    private String englishSentence;
    private String chineseSentence;
    
    // 关联属性
    private List<Word> relatedWords;
    private String audioPath;  // 预留音频路径

    // 构造函数
    public Sentence() {
        this.relatedWords = new ArrayList<>();
    }

    public Sentence(String englishSentence, String chineseSentence) {
        this();
        this.englishSentence = englishSentence;
        this.chineseSentence = chineseSentence;
    }

    // Getter和Setter方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEnglishSentence() { return englishSentence; }
    public void setEnglishSentence(String englishSentence) { this.englishSentence = englishSentence; }

    public String getChineseSentence() { return chineseSentence; }
    public void setChineseSentence(String chineseSentence) { this.chineseSentence = chineseSentence; }

    public List<Word> getRelatedWords() { return relatedWords; }
    public void setRelatedWords(List<Word> relatedWords) { this.relatedWords = relatedWords; }

    public String getAudioPath() { return audioPath; }
    public void setAudioPath(String audioPath) { this.audioPath = audioPath; }

    // 业务方法
    public void addRelatedWord(Word word) {
        if (!relatedWords.contains(word)) {
            relatedWords.add(word);
        }
    }

    public void removeRelatedWord(Word word) {
        relatedWords.remove(word);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", englishSentence, chineseSentence);
    }
} 