package com.englishassistant.view.test;

import java.awt.*;
import java.util.List;
import javax.swing.*;

import com.englishassistant.controller.TestController;
import com.englishassistant.controller.WrongWordController;
import com.englishassistant.model.*;
import com.englishassistant.view.common.StyledPanel;

/**
 * 单词测试面板类
 * 提供单词测试的核心功能界面
 * 
 * 功能说明:
 * 1. 测试配置
 *    - startTest(): 开始新测试，生成题目
 *    - resetTest(): 重置测试状态
 *    - showCurrentWord(): 显示当前题目
 * 
 * 2. 答题流程
 *    - checkAnswer(): 检查答案正确性
 *    - nextQuestion(): 进入下一题
 *    - showTestResult(): 显示测试结果
 * 
 * 3. 界面组件
 *    - 设置区: 难度选择、题目数量
 *    - 测试区: 题目显示、答案输入
 *    - 控制区: 提交、下一题按钮
 *    - 进度区: 当前进度显示
 * 
 * 4. 数据处理
 *    - 记录正确答题
 *    - 保存错题记录
 *    - 更新测试历史
 * 
 * @author Your Name
 */
public class TestPanel extends StyledPanel {
    private final TestController testController;
    private final WrongWordController wrongWordController;
    
    private JComboBox<Integer> difficultyBox;
    private JSpinner questionCountSpinner;
    private JButton startButton;
    private JLabel wordLabel;
    private JTextField answerField;
    private JLabel resultLabel;
    private JButton submitButton;
    private JButton nextButton;
    private JLabel progressLabel;
    
    private Test currentTest;
    
    public TestPanel() {
        super();
        addStyledBorder("单词测试");
        testController = new TestController();
        wrongWordController = new WrongWordController();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // 顶部设置面板
        JPanel settingsPanel = new JPanel(new BorderLayout());
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 左侧设置区域
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        difficultyBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        questionCountSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        
        leftPanel.add(createStyledLabel("难度："));
        leftPanel.add(difficultyBox);
        leftPanel.add(Box.createHorizontalStrut(20));
        leftPanel.add(createStyledLabel("题目数量："));
        leftPanel.add(questionCountSpinner);
        
        // 右侧按钮区域
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        startButton = createStyledButton("开始测试");
        rightPanel.add(startButton);
        
        settingsPanel.add(leftPanel, BorderLayout.WEST);
        settingsPanel.add(rightPanel, BorderLayout.EAST);
        
        // 中央测试面板
        JPanel testPanel = new JPanel();
        testPanel.setLayout(new BoxLayout(testPanel, BoxLayout.Y_AXIS));
        testPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // 单词显示区域（居中对齐）
        JPanel wordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wordLabel = createStyledLabel("准备开始测试");
        wordLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 32));
        wordPanel.add(wordLabel);
        
        // 答题区域（居中对齐）
        JPanel answerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        answerField = createStyledTextField(20);
        answerField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 18));
        answerPanel.add(answerField);
        
        // 结果显示区域（居中对齐）
        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        resultLabel = createStyledLabel(" ");
        resultLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        resultPanel.add(resultLabel);
        
        // 按钮区域（居中对齐）
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitButton = createStyledButton("提交");
        nextButton = createStyledButton("下一题");
        buttonPanel.add(submitButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(nextButton);
        
        // 进度显示（居中对齐）
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressLabel = createStyledLabel("进度: 0/0");
        progressPanel.add(progressLabel);
        
        // 添加到测试面板
        testPanel.add(wordPanel);
        testPanel.add(Box.createVerticalStrut(30));
        testPanel.add(answerPanel);
        testPanel.add(Box.createVerticalStrut(20));
        testPanel.add(resultPanel);
        testPanel.add(Box.createVerticalStrut(30));
        testPanel.add(buttonPanel);
        testPanel.add(Box.createVerticalStrut(20));
        testPanel.add(progressPanel);
        
        // 添加到主面板
        add(settingsPanel, BorderLayout.NORTH);
        add(testPanel, BorderLayout.CENTER);
        
        // 初始状态
        submitButton.setEnabled(false);
        nextButton.setEnabled(false);
        answerField.setEnabled(false);
        
        // 添加事件监听
        startButton.addActionListener(e -> startTest());
        submitButton.addActionListener(e -> checkAnswer());
        nextButton.addActionListener(e -> nextQuestion());
        answerField.addActionListener(e -> {
            if (submitButton.isEnabled()) {
                checkAnswer();
            } else if (nextButton.isEnabled()) {
                nextQuestion();
            }
        });
    }
    
    private void startTest() {
        int difficulty = (Integer) difficultyBox.getSelectedItem();
        int count = (Integer) questionCountSpinner.getValue();
        
        List<Word> testWords = testController.generateTest(count, difficulty);
        if (testWords.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有找到符合条件的单词！");
            return;
        }
        
        currentTest = new Test(testWords, difficulty);
        
        // 重置界面
        answerField.setText("");
        resultLabel.setText(" ");
        submitButton.setEnabled(true);
        nextButton.setEnabled(false);
        answerField.setEnabled(true);
        startButton.setEnabled(false);
        
        // 显示第一个单词
        showCurrentWord();
        answerField.requestFocus();
    }
    
    private void checkAnswer() {
        String answer = answerField.getText().trim();
        Word currentWord = currentTest.getCurrentWord();
        
        if (answer.equalsIgnoreCase(currentWord.getWord())) {
            resultLabel.setText("正确！");
            currentTest.addCorrect();
        } else {
            resultLabel.setText("错误！正确答案是: " + currentWord.getWord());
            // 记录错题
            WrongWord wrongWord = new WrongWord(currentWord, answer);
            wrongWordController.addWrongWord(wrongWord);
        }
        
        submitButton.setEnabled(false);
        nextButton.setEnabled(true);
        answerField.setEnabled(false);
        
        if (!currentTest.hasNext()) {
            nextButton.setText("完成");
        }
    }
    
    private void nextQuestion() {
        if (currentTest.hasNext()) {
            currentTest.next();
            answerField.setText("");
            resultLabel.setText(" ");
            submitButton.setEnabled(true);
            nextButton.setEnabled(false);
            answerField.setEnabled(true);
            showCurrentWord();
            answerField.requestFocus();
        } else {
            showTestResult();
        }
    }
    
    private void showTestResult() {
        double score = currentTest.getScore();
        String message = String.format(
            "测试完成！\n" +
            "总题数: %d\n" +
            "正确数: %d\n" +
            "得分: %.1f%%",
            currentTest.getTotalCount(),
            currentTest.getCorrectCount(),
            score
        );
        
        JOptionPane.showMessageDialog(this, message);
        
        // 保存测试历史
        TestHistory history = new TestHistory(currentTest);
        testController.saveTestHistory(history);
        
        // 重置界面
        resetTest();
    }
    
    private void resetTest() {
        currentTest = null;
        wordLabel.setText("准备开始测试");
        answerField.setText("");
        resultLabel.setText(" ");
        progressLabel.setText("进度: 0/0");
        submitButton.setEnabled(false);
        nextButton.setEnabled(false);
        answerField.setEnabled(false);
        startButton.setEnabled(true);
        nextButton.setText("下一题");
    }
    
    private void showCurrentWord() {
        Word currentWord = currentTest.getCurrentWord();
        wordLabel.setText(currentWord.getTranslation());
        updateProgress();
    }
    
    private void updateProgress() {
        if (currentTest != null) {
            progressLabel.setText(String.format("进度: %d/%d", 
                currentTest.getCurrentIndex() + 1, 
                currentTest.getTotalCount()));
        }
    }
} 