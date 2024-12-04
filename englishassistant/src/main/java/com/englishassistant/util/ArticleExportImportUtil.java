package com.englishassistant.util;

import java.io.*;
import java.text.*;
import java.util.*;

import org.json.*;

import com.englishassistant.model.Article;
import com.englishassistant.model.Word;

/**
 * 文章导入导出工具类
 * 负责处理文章数据的导入导出功能
 *
 * 主要功能:
 * 1. JSON格式
 *    - 导出到JSON(exportToJson)
 *    - 从JSON导入(importFromJson)
 *
 * 2. TXT格式
 *    - 导出到TXT(exportToTxt)
 *    - 从TXT导入(importFromTxt)
 *
 * 特点:
 * - 支持多种文件格式
 * - 保留文章完整信息
 * - 处理关联单词数据
 * - 自动编码转换
 */

public class ArticleExportImportUtil {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void exportToJson(String filePath, List<Article> articles) throws IOException {
        JSONObject root = new JSONObject();
        JSONArray articlesArray = new JSONArray();
        
        for (Article article : articles) {
            JSONObject articleObj = new JSONObject();
            articleObj.put("title", article.getTitle());
            articleObj.put("content", article.getContent());
            articleObj.put("translation", article.getTranslation());
            articleObj.put("difficulty", article.getDifficulty());
            articleObj.put("notes", article.getNotes());
            articleObj.put("readProgress", article.getReadProgress());
            
            if (article.getCreateTime() != null) {
                articleObj.put("createTime", dateFormat.format(article.getCreateTime()));
            }
            if (article.getLastReadTime() != null) {
                articleObj.put("lastReadTime", dateFormat.format(article.getLastReadTime()));
            }
            
            // 导出关联的单词
            JSONArray wordsArray = new JSONArray();
            for (Word word : article.getRelatedWords()) {
                JSONObject wordObj = new JSONObject();
                wordObj.put("word", word.getWord());
                wordObj.put("translation", word.getTranslation());
                wordObj.put("phonetic", word.getPhonetic());
                wordObj.put("difficulty", word.getDifficulty());
                wordsArray.put(wordObj);
            }
            articleObj.put("relatedWords", wordsArray);
            
            articlesArray.put(articleObj);
        }
        
        root.put("articles", articlesArray);
        
        // 写入文件
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(root.toString(2));
        }
    }
    
    public static void exportToTxt(String filePath, List<Article> articles) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Article article : articles) {
                writer.println("===== 文章开始 =====");
                writer.println("标题: " + article.getTitle());
                writer.println("难度: " + article.getDifficulty());
                writer.println("创建时间: " + (article.getCreateTime() != null ? 
                    dateFormat.format(article.getCreateTime()) : ""));
                writer.println("最后阅读时间: " + (article.getLastReadTime() != null ? 
                    dateFormat.format(article.getLastReadTime()) : ""));
                writer.println("阅读进度: " + article.getReadProgress() + "%");
                writer.println("\n原文:");
                writer.println(article.getContent());
                writer.println("\n翻译:");
                writer.println(article.getTranslation());
                if (article.getNotes() != null && !article.getNotes().isEmpty()) {
                    writer.println("\n笔记:");
                    writer.println(article.getNotes());
                }
                writer.println("\n关联单词:");
                for (Word word : article.getRelatedWords()) {
                    writer.printf("%s (%s) - %s%n", 
                        word.getWord(), 
                        word.getPhonetic(), 
                        word.getTranslation());
                }
                writer.println("===== 文章结束 =====\n");
            }
        }
    }
    
    public static List<Article> importFromJson(String filePath) throws IOException {
        List<Article> articles = new ArrayList<>();
        String jsonContent = new String(java.nio.file.Files.readAllBytes(
            java.nio.file.Paths.get(filePath)));
        JSONObject root = new JSONObject(jsonContent);
        
        if (root.has("articles")) {
            JSONArray articlesArray = root.getJSONArray("articles");
            for (int i = 0; i < articlesArray.length(); i++) {
                JSONObject articleObj = articlesArray.getJSONObject(i);
                Article article = new Article();
                
                article.setTitle(articleObj.getString("title"));
                article.setContent(articleObj.getString("content"));
                article.setTranslation(articleObj.getString("translation"));
                article.setDifficulty(articleObj.getInt("difficulty"));
                
                if (articleObj.has("notes")) {
                    article.setNotes(articleObj.getString("notes"));
                }
                if (articleObj.has("readProgress")) {
                    article.setReadProgress(articleObj.getInt("readProgress"));
                }
                
                // 解析时间
                try {
                    if (articleObj.has("createTime")) {
                        article.setCreateTime(dateFormat.parse(articleObj.getString("createTime")));
                    }
                    if (articleObj.has("lastReadTime")) {
                        article.setLastReadTime(dateFormat.parse(articleObj.getString("lastReadTime")));
                    }
                } catch (ParseException e) {
                    System.err.println("解析时间失败: " + e.getMessage());
                }
                
                // 导入关联的单词
                if (articleObj.has("relatedWords")) {
                    JSONArray wordsArray = articleObj.getJSONArray("relatedWords");
                    for (int j = 0; j < wordsArray.length(); j++) {
                        JSONObject wordObj = wordsArray.getJSONObject(j);
                        Word word = new Word();
                        word.setWord(wordObj.getString("word"));
                        word.setTranslation(wordObj.getString("translation"));
                        if (wordObj.has("phonetic")) {
                            word.setPhonetic(wordObj.getString("phonetic"));
                        }
                        word.setDifficulty(wordObj.getInt("difficulty"));
                        article.addRelatedWord(word);
                    }
                }
                
                articles.add(article);
            }
        }
        
        return articles;
    }
    
    public static List<Article> importFromTxt(String filePath) throws IOException {
        List<Article> articles = new ArrayList<>();
        Article currentArticle = null;
        StringBuilder content = new StringBuilder();
        StringBuilder translation = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String section = "";
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.equals("===== 文章开始 =====")) {
                    currentArticle = new Article();
                    currentArticle.setCreateTime(new Date());  // 设置创建时间
                    content.setLength(0);
                    translation.setLength(0);
                    continue;
                }
                
                if (line.equals("===== 文章结束 =====")) {
                    if (currentArticle != null) {
                        currentArticle.setContent(content.toString().trim());
                        currentArticle.setTranslation(translation.toString().trim());
                        articles.add(currentArticle);
                    }
                    continue;
                }
                
                if (currentArticle != null) {
                    if (line.startsWith("标题: ")) {
                        currentArticle.setTitle(line.substring(4).trim());
                    } else if (line.startsWith("难度: ")) {
                        try {
                            currentArticle.setDifficulty(Integer.parseInt(line.substring(4).trim()));
                        } catch (NumberFormatException e) {
                            currentArticle.setDifficulty(1);
                        }
                    } else if (line.startsWith("创建时间: ")) {
                        try {
                            String dateStr = line.substring(6).trim();
                            if (!dateStr.isEmpty()) {
                                currentArticle.setCreateTime(dateFormat.parse(dateStr));
                            }
                        } catch (ParseException e) {
                            currentArticle.setCreateTime(new Date());
                        }
                    } else if (line.startsWith("最后阅读时间: ")) {
                        try {
                            String dateStr = line.substring(8).trim();
                            if (!dateStr.isEmpty()) {
                                currentArticle.setLastReadTime(dateFormat.parse(dateStr));
                            }
                        } catch (ParseException e) {
                            // 如果解析失败，保持为null
                        }
                    } else if (line.startsWith("阅读进度: ")) {
                        try {
                            String progressStr = line.substring(6).trim().replace("%", "");
                            currentArticle.setReadProgress(Integer.parseInt(progressStr));
                        } catch (NumberFormatException e) {
                            currentArticle.setReadProgress(0);
                        }
                    } else if (line.equals("原文:")) {
                        section = "content";
                    } else if (line.equals("翻译:")) {
                        section = "translation";
                    } else if (line.equals("笔记:")) {
                        section = "notes";
                    } else if (line.equals("关联单词:")) {
                        section = "words";
                    } else if (!line.isEmpty()) {
                        switch (section) {
                            case "content" -> content.append(line).append("\n");
                            case "translation" -> translation.append(line).append("\n");
                            case "notes" -> currentArticle.setNotes(line);
                            case "words" -> {
                                // 解析关联单词行: "word (phonetic) - translation"
                                String[] parts = line.split(" - ");
                                if (parts.length >= 2) {
                                    String wordInfo = parts[0];
                                    String wordTranslation = parts[1];
                                    
                                    // 提取单词和音标
                                    String word;
                                    String phonetic = "";
                                    int phoneticStart = wordInfo.indexOf('(');
                                    int phoneticEnd = wordInfo.indexOf(')');
                                    
                                    if (phoneticStart > 0 && phoneticEnd > phoneticStart) {
                                        word = wordInfo.substring(0, phoneticStart).trim();
                                        phonetic = wordInfo.substring(phoneticStart + 1, phoneticEnd);
                                    } else {
                                        word = wordInfo.trim();
                                    }
                                    
                                    // 创建Word对象并添加到文章的关联单词列表
                                    Word wordObj = new Word();
                                    wordObj.setWord(word);
                                    wordObj.setTranslation(wordTranslation);
                                    wordObj.setPhonetic(phonetic);
                                    wordObj.setDifficulty(1); // 默认难度
                                    
                                    currentArticle.addRelatedWord(wordObj);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return articles;
    }
} 