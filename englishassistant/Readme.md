《面向对象程序设计（java）实践》题目及要求
《面向对象程序设计（java）实践》是完成《Java语言程序设计》学习后的综合性实训项目。要求每位学生自拟/选择一个题目完成。

一、要求
1) 功能：实现题目要求的功能;
2) 图形用户界面，界面友好；
3) 基于文件/数据库实现数据维护；
4) 设计文档不少于3000字（不能贴大段代码），word/wps10页以上。

二、评分标准
实现功能（50%）+界面友好(15%)+代码规范(10%)+文档规范(25%)。

三、题目及需求
英语学习助手
功能需求：
1、实现英语单词的录入、修改、删除等基本操作。
2、实现常用英语单词例句的录入、修改、删除等基本操作。
3、实现英语单词检索、翻译等。
4、常用英语单词例句检索。
5、根据难度随机生成一份单词测试题目。

# 英语学习助手项目说明

## 项目概述
一个基于Java Swing开发的英语学习辅助工具，帮助用户进行英语单词、例句和文章的学习和管理。项目采用MVC架构，使用SQLite数据库存储数据，实现了完整的学习、管理和测试功能。

## 核心功能模块

### 1. 学习中心 (StudyCenter)
- **单词学习**
  * 单词查询和翻译
  * 相关例句展示
  * 相关单词推荐
  * 快捷键支持
- **例句学习**
  * 例句浏览和搜索
  * 中英对照显示
  * 单词解释显示
  * 上下文导航
- **文章学习**
  * 文章阅读和翻译
  * 进度自动保存
  * 生词标注解释
  * 学习笔记功能

### 2. 管理中心 (ManagementCenter)
- **单词管理**
  * 单词的增删改查
  * 难度等级设置
  * 批量导入导出
  * 智能去重
- **例句管理**
  * 例句的增删改查
  * 自动关联单词
  * 批量导入导出
  * 数据验证
- **文章管理**
  * 文章导入导出
  * 阅读进度管理
  * 自动关联单词
  * 笔记管理

### 3. 测试系统 (TestCenter)
- **单词测试**
  * 难度选择
  * 随机出题
  * 实时评分
  * 错题记录
- **测试历史**
  * 成绩记录
  * 统计分析
  * 进步跟踪
  * 数据导出
- **错题练习**
  * 错题记录
  * 针对性练习
  * 掌握度跟踪
  * 智能复习

## 技术架构

### 1. 数据存储
- SQLite数据库表结构：
  * words: 单词信息(id, word, translation, phonetic, difficulty)
  * sentences: 例句信息(id, english_sentence, chinese_sentence)
  * sentence_word: 关联关系(sentence_id, word_id)
  * wrong_words: 错题记录(id, word_id, wrong_answer, wrong_time, wrong_count, mastered)
  * test_history: 测试历史(id, test_time, difficulty, total_questions, correct_count, score)
  * articles: 文章信息(id, title, content, translation, difficulty, create_time, notes, read_progress)

### 2. 项目结构
src/main/java/com/englishassistant/
├── controller/ # 控制器类
│ ├── ArticleController.java # 文章管理控制器
│ ├── SentenceController.java # 例句管理控制器
│ ├── StatisticsController.java # 统计分析控制器
│ ├── TestController.java # 测试系统控制器
│ ├── WordController.java # 单词管理控制器
│ └── WrongWordController.java # 错题管理控制器
│
├── model/ # 实体类
│ ├── Article.java # 文章实体类
│ ├── Sentence.java # 例句实体类
│ ├── Test.java # 测试实体类
│ ├── TestHistory.java # 测试历史实体类
│ ├── Word.java # 单词实体类
│ ├── WrongWord.java # 错题实体类
│ └── WrongWordTableModel.java # 错题表格模型
│
├── view/ # 界面类
│ ├── common/ # 公共组件
│ │ └── StyledPanel.java # 基础面板类
│ │
│ ├── manage/ # 管理中心
│ │ ├── ArticleManagePanel.java # 文章管理面板
│ │ ├── ManagementCenter.java # 管理中心主面板
│ │ ├── SentenceManagePanel.java # 例句管理面板
│ │ └── WordManagePanel.java # 单词管理面板
│ │
│ ├── study/ # 学习中心
│ │ ├── ArticleStudyPanel.java # 文章学习面板
│ │ ├── SentenceStudyPanel.java # 例句学习面板
│ │ ├── StudyCenter.java # 学习中心主面板
│ │ └── WordStudyPanel.java # 单词学习面板
│ │
│ ├── test/ # 测试系统
│ │ ├── TestCenter.java # 测试中心主面板
│ │ ├── TestHistoryPanel.java # 测试历史面板
│ │ └── TestPanel.java # 测试面板
│ │
│ └── wrong/ # 错题本
│ ├── WrongWordCenter.java # 错题本主面板
│ ├── WrongWordPanel.java # 错题列表面板
│ ├── WrongWordPracticePanel.java # 错题练习面板
│ └── WrongWordPracticeListener.java # 练习监听器
│
└── util/ # 工具类
├── ArticleExportImportUtil.java # 文章导入导出工具
├── BackupManager.java # 数据备份管理工具
├── DatabaseUtil.java # 数据库操作工具
├── DataExportImportUtil.java # 数据导入导出工具
├── ReminderConfig.java # 提醒配置类
├── ReminderManager.java # 提醒管理工具
└── ThemeManager.java # 主题管理工具


### 3. 技术特点
- 基于Java Swing的GUI实现
- MVC架构设计
- SQLite数据持久化
- 多种设计模式应用
- 统一的界面风格

## 系统要求
- JDK 17+
- SQLite 3.x
- Maven 3.8+
- 操作系统: Windows/Linux/MacOS

## 开发环境
- IDE: IntelliJ IDEA / VS Code
- 构建工具: Maven
- 版本控制: Git
- UI框架: Swing + FlatLaf

