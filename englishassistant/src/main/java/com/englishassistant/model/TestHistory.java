package com.englishassistant.model;

import java.util.*;

/**
 * 测试历史记录实体类
 * 用于记录和统计测试结果
 *
 * 主要功能:
 * 1. 测试信息记录
 *    - 测试时间
 *    - 题目总数
 *    - 正确题数
 *    - 最终得分
 *
 * 2. 测试分析
 *    - 难度等级记录
 *    - 正确率计算
 *    - 测试结果展示
 *
 * 数据表字段:
 * - id: INTEGER PRIMARY KEY
 * - test_time: TIMESTAMP
 * - total_questions: INTEGER
 * - correct_count: INTEGER
 * - score: DOUBLE
 * - difficulty: INTEGER
 */
public class TestHistory {
    // 数据库ID
    private int id;
    
    // 基本属性
    private Date testTime;
    private int totalQuestions;
    private int correctCount;
    private double score;
    private int difficulty;
    
    // 构造函数
    public TestHistory() {
        this.testTime = new Date();
    }
    
    public TestHistory(Test test) {
        this.testTime = new Date();
        this.totalQuestions = test.getTotalCount();
        this.correctCount = test.getCorrectCount();
        this.score = test.getScore();
        this.difficulty = test.getDifficulty();
    }
    
    // Getter和Setter方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Date getTestTime() { return testTime; }
    public void setTestTime(Date testTime) { this.testTime = testTime; }
    
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    
    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
    
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    
    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    
    @Override
    public String toString() {
        return String.format("测试记录 [时间: %s, 难度: %d, 得分: %.1f%%]", 
            testTime, difficulty, score);
    }
} 