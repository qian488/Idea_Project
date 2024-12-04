package com.englishassistant.model;

import java.util.*;

/**
 * 错题记录实体类
 * 用于记录和管理用户的错题信息
 *
 * 主要功能:
 * 1. 错题信息记录
 *    - 关联单词
 *    - 错误答案
 *    - 错误时间
 *    - 错误次数
 *
 * 2. 掌握状态管理
 *    - 掌握标记
 *    - 错误次数累计
 *    - 状态更新
 *
 * 数据表字段:
 * - id: INTEGER PRIMARY KEY
 * - word_id: INTEGER
 * - wrong_answer: TEXT
 * - wrong_time: TIMESTAMP
 * - wrong_count: INTEGER
 * - mastered: BOOLEAN
 */
public class WrongWord {
    // 数据库ID
    private int id;
    
    // 基本属性
    private Word word;
    private String wrongAnswer;
    private Date wrongTime;
    private int wrongCount;
    private boolean mastered;

    // 构造函数
    public WrongWord() {}

    public WrongWord(Word word, String wrongAnswer) {
        this.word = word;
        this.wrongAnswer = wrongAnswer;
        this.wrongTime = new Date();
        this.wrongCount = 1;
        this.mastered = false;
    }

    // Getter和Setter方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Word getWord() { return word; }
    public void setWord(Word word) { this.word = word; }

    public String getWrongAnswer() { return wrongAnswer; }
    public void setWrongAnswer(String wrongAnswer) { this.wrongAnswer = wrongAnswer; }

    public Date getWrongTime() { return wrongTime; }
    public void setWrongTime(Date wrongTime) { this.wrongTime = wrongTime; }

    public int getWrongCount() { return wrongCount; }
    public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }

    public boolean isMastered() { return mastered; }
    public void setMastered(boolean mastered) { this.mastered = mastered; }

    // 业务方法
    public void incrementWrongCount() {
        this.wrongCount++;
    }

    @Override
    public String toString() {
        return String.format("%s (错误次数: %d, 已掌握: %s)", 
            word.getWord(), wrongCount, mastered ? "是" : "否");
    }
} 