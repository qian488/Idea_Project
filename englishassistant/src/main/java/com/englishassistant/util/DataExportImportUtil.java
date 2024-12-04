package com.englishassistant.util;

// Java标准库
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

// XML相关
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

// 第三方库
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

// 项目内部类
import com.englishassistant.model.Article;
import com.englishassistant.model.Sentence;
import com.englishassistant.model.Word;

/**
 * 数据导入导出工具类
 * 负责处理不同格式的数据导入导出
 *
 * 主要功能:
 * 1. 多格式支持
 *    - CSV格式
 *    - XML格式
 *    - JSON格式
 *    - Excel格式
 *    - TXT格式
 *
 * 2. 数据处理
 *    - 导入验证
 *    - 格式转换
 *    - 编码处理
 *    - 错误处理
 *
 * 特点:
 * - 支持多种文件格式
 * - 数据完整性验证
 * - 灵活的导入导出
 */
public class DataExportImportUtil {
    
    public enum FileType {
        CSV, XML, JSON, EXCEL, TXT
    }
    
    public static void exportData(String filePath, List<Word> words, List<Sentence> sentences, List<Article> articles, FileType type) throws IOException {
        switch (type) {
            case CSV -> exportToCSV(filePath, words, sentences, articles);
            case XML -> exportToXML(filePath, words, sentences, articles);
            case JSON -> exportToJSON(filePath, words, sentences, articles);
            case EXCEL -> exportToExcel(filePath, words, sentences, articles);
            case TXT -> exportToTXT(filePath, words, sentences, articles);
        }
    }
    
    public static Map<String, List<?>> importData(String filePath, FileType type) throws IOException {
        return switch (type) {
            case CSV -> importFromCSV(filePath);
            case XML -> importFromXML(filePath);
            case JSON -> importFromJSON(filePath);
            case EXCEL -> importFromExcel(filePath);
            case TXT -> importFromTXT(filePath);
        };
    }
    
    public static void exportToCSV(String filePath, List<Word> words, List<Sentence> sentences, List<Article> articles) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, StandardCharsets.UTF_8))) {
            // 写入单词数据
            writer.write("===WORDS===\n");
            writer.write("单词,翻译,难度\n");
            for (Word word : words) {
                writer.write(String.format("%s,%s,%d\n",
                    quote(word.getWord()),
                    quote(word.getTranslation()),
                    word.getDifficulty()
                ));
            }
            
            // 写入例句数据
            writer.write("\n===SENTENCES===\n");
            writer.write("英文例句,中文翻译,关联单词\n");
            for (Sentence sentence : sentences) {
                List<String> wordTexts = new ArrayList<>();
                for (Word word : sentence.getRelatedWords()) {
                    wordTexts.add(word.getWord());
                }
                writer.write(String.format("%s,%s,%s\n",
                    quote(sentence.getEnglishSentence()),
                    quote(sentence.getChineseSentence()),
                    quote(String.join("|", wordTexts))
                ));
            }

            // 写入文章数据
            writer.write("\n===ARTICLES===\n");
            writer.write("标题,内容,翻译,难度,笔记\n");
            for (Article article : articles) {
                writer.write(String.format("%s,%s,%s,%d,%s\n",
                    quote(article.getTitle()),
                    quote(article.getContent()),
                    quote(article.getTranslation()),
                    article.getDifficulty(),
                    quote(article.getNotes() != null ? article.getNotes() : "")
                ));
            }
        }
    }
    
    private static void exportToXML(String filePath, List<Word> words, List<Sentence> sentences, List<Article> articles) throws IOException {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            
            // 创建根元素
            Element rootElement = doc.createElement("english-assistant-data");
            doc.appendChild(rootElement);
            
            // 添加单词数据
            Element wordsElement = doc.createElement("words");
            rootElement.appendChild(wordsElement);
            
            for (Word word : words) {
                Element wordElement = doc.createElement("word");
                wordElement.setAttribute("id", String.valueOf(word.getId()));
                
                Element wordText = doc.createElement("text");
                wordText.setTextContent(word.getWord());
                wordElement.appendChild(wordText);
                
                Element translation = doc.createElement("translation");
                translation.setTextContent(word.getTranslation());
                wordElement.appendChild(translation);
                
                Element phonetic = doc.createElement("phonetic");
                phonetic.setTextContent(word.getPhonetic());
                wordElement.appendChild(phonetic);
                
                Element difficulty = doc.createElement("difficulty");
                difficulty.setTextContent(String.valueOf(word.getDifficulty()));
                wordElement.appendChild(difficulty);
                
                wordsElement.appendChild(wordElement);
            }
            
            // 添加例句数据
            Element sentencesElement = doc.createElement("sentences");
            rootElement.appendChild(sentencesElement);
            
            for (Sentence sentence : sentences) {
                Element sentenceElement = doc.createElement("sentence");
                sentenceElement.setAttribute("id", String.valueOf(sentence.getId()));
                
                Element english = doc.createElement("english");
                english.setTextContent(sentence.getEnglishSentence());
                sentenceElement.appendChild(english);
                
                Element chinese = doc.createElement("chinese");
                chinese.setTextContent(sentence.getChineseSentence());
                sentenceElement.appendChild(chinese);
                
                Element relatedWords = doc.createElement("related-words");
                for (Word word : sentence.getRelatedWords()) {
                    Element wordRef = doc.createElement("word-ref");
                    wordRef.setAttribute("word", word.getWord());
                    relatedWords.appendChild(wordRef);
                }
                sentenceElement.appendChild(relatedWords);
                
                sentencesElement.appendChild(sentenceElement);
            }
            
            // 添加文章数据
            Element articlesElement = doc.createElement("articles");
            rootElement.appendChild(articlesElement);
            
            for (Article article : articles) {
                Element articleElement = doc.createElement("article");
                articleElement.setAttribute("id", String.valueOf(article.getId()));
                
                Element title = doc.createElement("title");
                title.setTextContent(article.getTitle());
                articleElement.appendChild(title);
                
                Element content = doc.createElement("content");
                content.setTextContent(article.getContent());
                articleElement.appendChild(content);
                
                Element translation = doc.createElement("translation");
                translation.setTextContent(article.getTranslation());
                articleElement.appendChild(translation);
                
                Element difficulty = doc.createElement("difficulty");
                difficulty.setTextContent(String.valueOf(article.getDifficulty()));
                articleElement.appendChild(difficulty);
                
                Element notes = doc.createElement("notes");
                notes.setTextContent(article.getNotes());
                articleElement.appendChild(notes);
                
                articlesElement.appendChild(articleElement);
            }
            
            // 写入文件
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
            
        } catch (Exception e) {
            throw new IOException("XML导出失败", e);
        }
    }
    
    private static void exportToJSON(String filePath, List<Word> words, List<Sentence> sentences, List<Article> articles) throws IOException {
        JSONObject root = new JSONObject();
        
        // 添加单词数据
        JSONArray wordsArray = new JSONArray();
        for (Word word : words) {
            JSONObject wordObj = new JSONObject();
            wordObj.put("id", word.getId());
            wordObj.put("word", word.getWord());
            wordObj.put("translation", word.getTranslation());
            wordObj.put("difficulty", word.getDifficulty());
            wordsArray.put(wordObj);
        }
        root.put("words", wordsArray);
        
        // 添加例句数据
        JSONArray sentencesArray = new JSONArray();
        for (Sentence sentence : sentences) {
            JSONObject sentenceObj = new JSONObject();
            sentenceObj.put("id", sentence.getId());
            sentenceObj.put("english", sentence.getEnglishSentence());
            sentenceObj.put("chinese", sentence.getChineseSentence());
            
            JSONArray relatedWordIds = new JSONArray();
            for (Word word : sentence.getRelatedWords()) {
                relatedWordIds.put(word.getId());
            }
            sentenceObj.put("relatedWordIds", relatedWordIds);
            
            sentencesArray.put(sentenceObj);
        }
        root.put("sentences", sentencesArray);
        
        // 添加文章数据
        JSONArray articlesArray = new JSONArray();
        for (Article article : articles) {
            JSONObject articleObj = new JSONObject();
            articleObj.put("id", article.getId());
            articleObj.put("title", article.getTitle());
            articleObj.put("content", article.getContent());
            articleObj.put("translation", article.getTranslation());
            articleObj.put("difficulty", article.getDifficulty());
            articleObj.put("notes", article.getNotes());
            articlesArray.put(articleObj);
        }
        root.put("articles", articlesArray);
        
        // 写入文件
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(root.toString(2));
        }
    }
    
    private static void exportToExcel(String filePath, List<Word> words, List<Sentence> sentences, List<Article> articles) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // 创建单词工作表
            Sheet wordSheet = workbook.createSheet("单词");
            Row headerRow = wordSheet.createRow(0);
            headerRow.createCell(0).setCellValue("单词");
            headerRow.createCell(1).setCellValue("翻译");
            headerRow.createCell(2).setCellValue("音标");
            headerRow.createCell(3).setCellValue("难度");
            
            int rowNum = 1;
            for (Word word : words) {
                Row row = wordSheet.createRow(rowNum++);
                row.createCell(0).setCellValue(word.getWord());
                row.createCell(1).setCellValue(word.getTranslation());
                row.createCell(2).setCellValue(word.getPhonetic());
                row.createCell(3).setCellValue(word.getDifficulty());
            }
            
            // 创建例句工作表
            Sheet sentenceSheet = workbook.createSheet("例句");
            headerRow = sentenceSheet.createRow(0);
            headerRow.createCell(0).setCellValue("英文例句");
            headerRow.createCell(1).setCellValue("中文翻译");
            headerRow.createCell(2).setCellValue("关联单词");
            
            rowNum = 1;
            for (Sentence sentence : sentences) {
                Row row = sentenceSheet.createRow(rowNum++);
                row.createCell(0).setCellValue(sentence.getEnglishSentence());
                row.createCell(1).setCellValue(sentence.getChineseSentence());
                
                // 处理关联单词
                List<String> wordTexts = new ArrayList<>();
                for (Word word : sentence.getRelatedWords()) {
                    wordTexts.add(word.getWord());
                }
                row.createCell(2).setCellValue(String.join(", ", wordTexts));
            }
            
            // 创建文章工作表
            Sheet articleSheet = workbook.createSheet("文章");
            headerRow = articleSheet.createRow(0);
            headerRow.createCell(0).setCellValue("标题");
            headerRow.createCell(1).setCellValue("内容");
            headerRow.createCell(2).setCellValue("翻译");
            headerRow.createCell(3).setCellValue("难度");
            headerRow.createCell(4).setCellValue("笔记");
            
            rowNum = 1;
            for (Article article : articles) {
                Row row = articleSheet.createRow(rowNum++);
                row.createCell(0).setCellValue(article.getTitle());
                row.createCell(1).setCellValue(article.getContent());
                row.createCell(2).setCellValue(article.getTranslation());
                row.createCell(3).setCellValue(article.getDifficulty());
                row.createCell(4).setCellValue(article.getNotes());
            }
            
            // 自动调整列宽
            for (Sheet sheet : new Sheet[]{wordSheet, sentenceSheet, articleSheet}) {
                for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
                    sheet.autoSizeColumn(i);
                }
            }
            
            // 写入文件
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            
        } catch (Exception e) {
            throw new IOException("Excel导出失败: " + e.getMessage(), e);
        }
    }
    
    private static void exportToTXT(String filePath, List<Word> words, List<Sentence> sentences, List<Article> articles) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, StandardCharsets.UTF_8))) {
            // 写入单词数据
            writer.write("=== 单词列表 ===\n\n");
            for (Word word : words) {
                writer.write(String.format("单词: %s\n", word.getWord()));
                writer.write(String.format("翻译: %s\n", word.getTranslation()));
                writer.write(String.format("难度: %d\n", word.getDifficulty()));
                writer.write("\n");
            }
            
            // 写入例句数据
            writer.write("=== 例句列表 ===\n\n");
            for (Sentence sentence : sentences) {
                writer.write(String.format("英文: %s\n", sentence.getEnglishSentence()));
                writer.write(String.format("中文: %s\n", sentence.getChineseSentence()));
                if (!sentence.getRelatedWords().isEmpty()) {
                    List<String> wordTexts = new ArrayList<>();
                    for (Word word : sentence.getRelatedWords()) {
                        wordTexts.add(word.getWord());
                    }
                    writer.write(String.format("关联单词: %s\n", String.join(", ", wordTexts)));
                }
                writer.write("\n");
            }

            // 写入文章数据
            writer.write("=== 文章列表 ===\n\n");
            for (Article article : articles) {
                writer.write(String.format("标题: %s\n", article.getTitle()));
                writer.write(String.format("内容:\n%s\n", article.getContent()));
                writer.write(String.format("翻译:\n%s\n", article.getTranslation()));
                writer.write(String.format("难度: %d\n", article.getDifficulty()));
                if (article.getNotes() != null && !article.getNotes().isEmpty()) {
                    writer.write(String.format("笔记:\n%s\n", article.getNotes()));
                }
                writer.write("\n");
            }
        }
    }
    
    public static Map<String, List<?>> importFromCSV(String filePath) throws IOException {
        List<Word> words = new ArrayList<>();
        List<Sentence> sentences = new ArrayList<>();
        List<Article> articles = new ArrayList<>();
        Map<String, List<?>> result = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            String section = "";
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("===")) {
                    section = line;
                    reader.readLine(); // 跳过标题行
                    continue;
                }
                
                if (line.trim().isEmpty()) continue;
                
                switch (section) {
                    case "===WORDS===" -> {
                        try {
                            String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                            Word word = new Word();
                            word.setWord(unquote(parts[0].trim()));
                            word.setTranslation(unquote(parts[1].trim()));
                            word.setDifficulty(Integer.parseInt(parts[2].trim()));
                            words.add(word);
                        } catch (Exception e) {
                            System.err.println("解析CSV行失败: " + line);
                            e.printStackTrace();
                        }
                    }
                    case "===SENTENCES===" -> {
                        try {
                            String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                            Sentence sentence = new Sentence();
                            sentence.setEnglishSentence(unquote(parts[0].trim()));
                            sentence.setChineseSentence(unquote(parts[1].trim()));
                            if (parts.length > 2) {
                                String[] wordTexts = unquote(parts[2].trim()).split("\\|");
                                for (String wordText : wordTexts) {
                                    if (!wordText.isEmpty()) {
                                        Word word = new Word();
                                        word.setWord(wordText);
                                        sentence.addRelatedWord(word);
                                    }
                                }
                            }
                            sentences.add(sentence);
                        } catch (Exception e) {
                            System.err.println("解析CSV行失败: " + line);
                            e.printStackTrace();
                        }
                    }
                    case "===ARTICLES===" -> {
                        try {
                            String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                            Article article = new Article();
                            article.setTitle(unquote(parts[0].trim()));
                            article.setContent(unquote(parts[1].trim()));
                            article.setTranslation(unquote(parts[2].trim()));
                            article.setDifficulty(Integer.parseInt(parts[3].trim()));
                            if (parts.length > 4) {
                                article.setNotes(unquote(parts[4].trim()));
                            }
                            articles.add(article);
                        } catch (Exception e) {
                            System.err.println("解析CSV行失败: " + line);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        
        result.put("words", words);
        result.put("sentences", sentences);
        result.put("articles", articles);
        return result;
    }
    
    private static String unquote(String str) {
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }
    
    private static String quote(String str) {
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }
    
    private static Map<String, List<?>> importFromXML(String filePath) throws IOException {
        Map<String, List<?>> result = new HashMap<>();
        List<Word> words = new ArrayList<>();
        List<Sentence> sentences = new ArrayList<>();
        List<Article> articles = new ArrayList<>();
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            
            // 解析单词数据
            NodeList wordNodes = doc.getElementsByTagName("word");
            for (int i = 0; i < wordNodes.getLength(); i++) {
                Element wordElement = (Element) wordNodes.item(i);
                Word word = new Word();
                word.setWord(getElementText(wordElement, "text"));
                word.setTranslation(getElementText(wordElement, "translation"));
                word.setPhonetic(getElementText(wordElement, "phonetic"));
                word.setDifficulty(Integer.parseInt(getElementText(wordElement, "difficulty")));
                words.add(word);
            }
            
            // 解析例句数据
            NodeList sentenceNodes = doc.getElementsByTagName("sentence");
            for (int i = 0; i < sentenceNodes.getLength(); i++) {
                Element sentenceElement = (Element) sentenceNodes.item(i);
                Sentence sentence = new Sentence();
                sentence.setEnglishSentence(getElementText(sentenceElement, "english"));
                sentence.setChineseSentence(getElementText(sentenceElement, "chinese"));
                
                // 处理关联单词
                NodeList wordRefNodes = sentenceElement.getElementsByTagName("word-ref");
                for (int j = 0; j < wordRefNodes.getLength(); j++) {
                    Element wordRefElement = (Element) wordRefNodes.item(j);
                    String wordText = wordRefElement.getAttribute("word");
                    Word word = new Word();
                    word.setWord(wordText);
                    sentence.addRelatedWord(word);
                }
                sentences.add(sentence);
            }
            
            // 解析文章数据
            NodeList articleNodes = doc.getElementsByTagName("article");
            for (int i = 0; i < articleNodes.getLength(); i++) {
                Element articleElement = (Element) articleNodes.item(i);
                Article article = new Article();
                article.setTitle(getElementText(articleElement, "title"));
                article.setContent(getElementText(articleElement, "content"));
                article.setTranslation(getElementText(articleElement, "translation"));
                article.setDifficulty(Integer.parseInt(getElementText(articleElement, "difficulty")));
                article.setNotes(getElementText(articleElement, "notes"));
                articles.add(article);
            }
            
            result.put("words", words);
            result.put("sentences", sentences);
            result.put("articles", articles);
            
        } catch (Exception e) {
            throw new IOException("XML解析失败", e);
        }
        
        return result;
    }
    
    private static String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }
    
    private static Map<String, List<?>> importFromJSON(String filePath) throws IOException {
        Map<String, List<?>> result = new HashMap<>();
        List<Word> words = new ArrayList<>();
        List<Sentence> sentences = new ArrayList<>();
        List<Article> articles = new ArrayList<>();
        
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(jsonContent);
            
            // 解析单词数据
            if (root.has("words")) {
                JSONArray wordsArray = root.getJSONArray("words");
                for (int i = 0; i < wordsArray.length(); i++) {
                    JSONObject wordObj = wordsArray.getJSONObject(i);
                    Word word = new Word();
                    word.setWord(wordObj.getString("word"));
                    word.setTranslation(wordObj.getString("translation"));
                    if (wordObj.has("phonetic")) {
                        word.setPhonetic(wordObj.getString("phonetic"));
                    }
                    word.setDifficulty(wordObj.getInt("difficulty"));
                    words.add(word);
                }
            }
            
            // 解析例句数据
            if (root.has("sentences")) {
                JSONArray sentencesArray = root.getJSONArray("sentences");
                for (int i = 0; i < sentencesArray.length(); i++) {
                    JSONObject sentenceObj = sentencesArray.getJSONObject(i);
                    Sentence sentence = new Sentence();
                    sentence.setEnglishSentence(sentenceObj.getString("english"));
                    sentence.setChineseSentence(sentenceObj.getString("chinese"));
                    
                    if (sentenceObj.has("relatedWords")) {
                        JSONArray relatedWordsArray = sentenceObj.getJSONArray("relatedWords");
                        for (int j = 0; j < relatedWordsArray.length(); j++) {
                            JSONObject wordObj = relatedWordsArray.getJSONObject(j);
                            Word word = new Word();
                            word.setWord(wordObj.getString("word"));
                            sentence.addRelatedWord(word);
                        }
                    }
                    sentences.add(sentence);
                }
            }
            
            // 解析文章数据
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
                    articles.add(article);
                }
            }
            
            result.put("words", words);
            result.put("sentences", sentences);
            result.put("articles", articles);
            
        } catch (Exception e) {
            throw new IOException("JSON解析失败: " + e.getMessage(), e);
        }
        
        return result;
    }
    
    private static Map<String, List<?>> importFromExcel(String filePath) throws IOException {
        Map<String, List<?>> result = new HashMap<>();
        List<Word> words = new ArrayList<>();
        List<Sentence> sentences = new ArrayList<>();
        List<Article> articles = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(filePath))) {
            // 读取单词工作表
            Sheet wordSheet = workbook.getSheet("单词");
            if (wordSheet != null) {
                for (int i = 1; i <= wordSheet.getLastRowNum(); i++) {
                    Row row = wordSheet.getRow(i);
                    if (row != null) {
                        Word word = new Word();
                        word.setWord(getStringCellValue(row.getCell(0)));
                        word.setTranslation(getStringCellValue(row.getCell(1)));
                        word.setPhonetic(getStringCellValue(row.getCell(2)));
                        word.setDifficulty((int) row.getCell(3).getNumericCellValue());
                        words.add(word);
                    }
                }
            }
            
            // 读取例句工作表
            Sheet sentenceSheet = workbook.getSheet("例句");
            if (sentenceSheet != null) {
                for (int i = 1; i <= sentenceSheet.getLastRowNum(); i++) {
                    Row row = sentenceSheet.getRow(i);
                    if (row != null) {
                        Sentence sentence = new Sentence();
                        sentence.setEnglishSentence(getStringCellValue(row.getCell(0)));
                        sentence.setChineseSentence(getStringCellValue(row.getCell(1)));
                        
                        // 处理关联单词
                        String wordList = getStringCellValue(row.getCell(2));
                        if (!wordList.isEmpty()) {
                            for (String wordText : wordList.split(",")) {
                                Word word = new Word();
                                word.setWord(wordText.trim());
                                sentence.addRelatedWord(word);
                            }
                        }
                        sentences.add(sentence);
                    }
                }
            }
            
            // 读取文章工作表
            Sheet articleSheet = workbook.getSheet("文章");
            if (articleSheet != null) {
                for (int i = 1; i <= articleSheet.getLastRowNum(); i++) {
                    Row row = articleSheet.getRow(i);
                    if (row != null) {
                        Article article = new Article();
                        article.setTitle(getStringCellValue(row.getCell(0)));
                        article.setContent(getStringCellValue(row.getCell(1)));
                        article.setTranslation(getStringCellValue(row.getCell(2)));
                        article.setDifficulty((int) row.getCell(3).getNumericCellValue());
                        article.setNotes(getStringCellValue(row.getCell(4)));
                        articles.add(article);
                    }
                }
            }
            
            result.put("words", words);
            result.put("sentences", sentences);
            result.put("articles", articles);
            
        } catch (Exception e) {
            throw new IOException("Excel导入失败: " + e.getMessage(), e);
        }
        
        return result;
    }
    
    private static String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return "";
        }
    }
    
    public static Map<String, List<?>> importFromTXT(String filePath) throws IOException {
        List<Word> words = new ArrayList<>();
        List<Sentence> sentences = new ArrayList<>();
        List<Article> articles = new ArrayList<>();
        Map<String, List<?>> result = new HashMap<>();
        
        System.out.println("开始导入文件: " + filePath);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            String section = "";
            Word currentWord = null;
            Sentence currentSentence = null;
            Article currentArticle = null;
            StringBuilder contentBuilder = null;
            StringBuilder translationBuilder = null;
            StringBuilder notesBuilder = null;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                if (line.isEmpty()) {
                    if (currentWord != null && currentWord.getWord() != null && currentWord.getTranslation() != null) {
                        words.add(currentWord);
                        currentWord = null;
                    }
                    if (currentSentence != null) {
                        sentences.add(currentSentence);
                        currentSentence = null;
                    }
                    if (currentArticle != null && contentBuilder != null && translationBuilder != null) {
                        currentArticle.setContent(contentBuilder.toString().trim());
                        currentArticle.setTranslation(translationBuilder.toString().trim());
                        if (notesBuilder != null) {
                            currentArticle.setNotes(notesBuilder.toString().trim());
                        }
                        articles.add(currentArticle);
                        currentArticle = null;
                        contentBuilder = null;
                        translationBuilder = null;
                        notesBuilder = null;
                    }
                    continue;
                }
                
                if (line.startsWith("=== ")) {
                    section = line;
                    System.out.println("进入新部分: " + section);
                    continue;
                }
                
                switch (section) {
                    case "=== 单词列表 ===" -> {
                        if (line.startsWith("单词: ")) {
                            // 如果有未完成的单词，先保存
                            if (currentWord != null && currentWord.getWord() != null && currentWord.getTranslation() != null) {
                                System.out.println("添加未完成的单词: " + currentWord.getWord());
                                words.add(currentWord);
                            }
                            currentWord = new Word();
                            String wordText = line.substring(4).trim();
                            currentWord.setWord(wordText);
                            System.out.println("读取单词: " + wordText);
                        } else if (line.startsWith("翻译:")) {  // 移除空格检查
                            if (currentWord != null) {
                                String translation = line.substring(3).trim();  // 修改截取长度
                                currentWord.setTranslation(translation);
                                System.out.println("设置翻译: " + translation);
                            } else {
                                System.err.println("错误：第" + lineNumber + "行，翻译没有对应的单词");
                            }
                        } else if (line.startsWith("难度:")) {  // 移除空格检查
                            if (currentWord != null) {
                                try {
                                    int difficulty = Integer.parseInt(line.substring(3).trim());  // 修改截取长度
                                    currentWord.setDifficulty(difficulty);
                                    System.out.println("设置难度: " + difficulty);
                                } catch (NumberFormatException e) {
                                    System.err.println("错误：第" + lineNumber + "行，难度格式不正确");
                                    currentWord.setDifficulty(1); // 默认难度
                                }
                            }
                        } else {
                            System.err.println("警告：第" + lineNumber + "行，未识别的行格式: " + line);
                        }
                    }
                    case "=== 例句列表 ===" -> {
                        if (line.startsWith("英文: ")) {
                            if (currentSentence == null) {
                                currentSentence = new Sentence();
                            }
                            currentSentence.setEnglishSentence(line.substring(4).trim());
                        } else if (line.startsWith("中文: ")) {
                            if (currentSentence != null) {
                                currentSentence.setChineseSentence(line.substring(4).trim());
                            }
                        } else if (line.startsWith("关联单词: ")) {
                            if (currentSentence != null) {
                                String[] wordTexts = line.substring(6).split(", ");
                                for (String wordText : wordTexts) {
                                    if (!wordText.isEmpty()) {
                                        Word word = new Word();
                                        word.setWord(wordText);
                                        currentSentence.addRelatedWord(word);
                                    }
                                }
                            }
                        }
                    }
                    case "=== 文章列表 ===" -> {
                        if (line.startsWith("标题: ")) {
                            if (currentArticle != null) {
                                currentArticle.setContent(contentBuilder.toString().trim());
                                currentArticle.setTranslation(translationBuilder.toString().trim());
                                if (notesBuilder != null) {
                                    currentArticle.setNotes(notesBuilder.toString().trim());
                                }
                                articles.add(currentArticle);
                            }
                            currentArticle = new Article();
                            currentArticle.setTitle(line.substring(4).trim());
                            contentBuilder = new StringBuilder();
                            translationBuilder = new StringBuilder();
                            notesBuilder = null;
                        } else if (line.equals("内容:")) {
                            contentBuilder = new StringBuilder();
                        } else if (line.equals("翻译:")) {
                            translationBuilder = new StringBuilder();
                        } else if (line.startsWith("难度: ")) {
                            if (currentArticle != null) {
                                try {
                                    currentArticle.setDifficulty(Integer.parseInt(line.substring(4).trim()));
                                } catch (NumberFormatException e) {
                                    currentArticle.setDifficulty(1);
                                }
                            }
                        } else if (line.equals("笔记:")) {
                            notesBuilder = new StringBuilder();
                        } else {
                            if (contentBuilder != null && translationBuilder == null) {
                                contentBuilder.append(line).append("\n");
                            } else if (translationBuilder != null && notesBuilder == null) {
                                translationBuilder.append(line).append("\n");
                            } else if (notesBuilder != null) {
                                notesBuilder.append(line).append("\n");
                            }
                        }
                    }
                }
            }
            
            // 处理最后一个条目
            if (currentWord != null && currentWord.getWord() != null && currentWord.getTranslation() != null) {
                words.add(currentWord);
            }
            if (currentSentence != null) {
                sentences.add(currentSentence);
            }
            if (currentArticle != null && contentBuilder != null && translationBuilder != null) {
                currentArticle.setContent(contentBuilder.toString().trim());
                currentArticle.setTranslation(translationBuilder.toString().trim());
                if (notesBuilder != null) {
                    currentArticle.setNotes(notesBuilder.toString().trim());
                }
                articles.add(currentArticle);
            }
        }
        
        result.put("words", words);
        result.put("sentences", sentences);
        result.put("articles", articles);
        return result;
    }
} 