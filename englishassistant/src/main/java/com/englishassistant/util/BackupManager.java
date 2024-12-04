package com.englishassistant.util;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * 数据备份管理工具类
 * 负责数据库文件的备份和还原操作
 *
 * 主要功能:
 * 1. 备份管理
 *    - 创建备份(backup)
 *    - 还原备份(restore)
 *
 * 2. 文件处理
 *    - 自动创建目录
 *    - 时间戳命名
 *    - 文件复制
 *
 * 特点:
 * - 自动备份目录管理
 * - 完整数据库备份
 * - 安全的还原机制
 */
public class BackupManager {
    private static final String BACKUP_DIR = "backup";
    
    public static void backup() throws IOException {
        // 确保备份目录存在
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        
        // 生成备份文件名
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String backupFile = BACKUP_DIR + File.separator + "backup_" + timestamp + ".db";
        
        // 复制数据库文件
        File sourceFile = new File("data" + File.separator + "english_assistant.db");
        File destFile = new File(backupFile);
        
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }
    
    public static void restore(String backupFile) throws IOException {
        // 实现还原逻辑
        File sourceFile = new File(backupFile);
        File destFile = new File("data" + File.separator + "english_assistant.db");
        
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }
} 