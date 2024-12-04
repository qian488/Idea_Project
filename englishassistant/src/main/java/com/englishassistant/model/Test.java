package com.englishassistant.model;

import java.util.*;

/**
 * 测试实体类
 * 用于管理单词测试的进行状态和结果
 *
 * 主要功能:
 * 1. 测试状态管理
 *    - 当前题目索引
 *    - 正确答题计数
 *    - 测试进度跟踪
 *
 * 2. 测试结果统计
 *    - 计算得分
 *    - 记录答题情况
 *    - 难度等级记录
 *
 * 3. 测试流程控制
 *    - 下一题判断
 *    - 测试完成判断
 *    - 正确答题记录
 */
public class Test {
    // 基本属性
    private List<Word> testWords;
    private int currentIndex;
    private int correctCount;
    private int totalCount;
    private int difficulty;
    
    // 构造函数
    public Test(List<Word> testWords, int difficulty) {
        this.testWords = testWords;
        this.difficulty = difficulty;
        this.currentIndex = 0;
        this.correctCount = 0;
        this.totalCount = testWords.size();
    }
    
    // Getter方法
    public Word getCurrentWord() { return testWords.get(currentIndex); }
    public int getCurrentIndex() { return currentIndex; }
    public int getCorrectCount() { return correctCount; }
    public int getTotalCount() { return totalCount; }
    public int getDifficulty() { return difficulty; }
    
    // 业务方法
    public boolean hasNext() { return currentIndex < testWords.size() - 1; }
    
    public void next() {
        if (hasNext()) {
            currentIndex++;
        }
    }
    
    public void addCorrect() { correctCount++; }
    
    public double getScore() {
        return (double) correctCount / totalCount * 100;
    }
} 