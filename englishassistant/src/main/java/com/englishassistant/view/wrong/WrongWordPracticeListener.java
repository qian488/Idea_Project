package com.englishassistant.view.wrong;

import java.util.List;
import com.englishassistant.model.WrongWord;

/**
 * 错题练习监听器接口
 * 用于在练习完成时通知相关组件更新状态
 * 
 * 功能说明:
 * 1. 事件通知
 *    - onPracticeCompleted(): 练习完成时的回调方法
 *    - 传递已练习的错题列表
 * 
 * 2. 状态更新
 *    - 更新错题列表显示
 *    - 刷新掌握状态
 *    - 保存练习结果
 * 
 * 使用场景:
 * - WrongWordPanel实现此接口以接收练习完成通知
 * - WrongWordPracticePanel通过此接口发送通知
 * 
 * @author Your Name
 */
public interface WrongWordPracticeListener {
    /**
     * 练习完成时的回调方法
     * @param practicedWords 已练习的错题列表
     */
    void onPracticeCompleted(List<WrongWord> practicedWords);
} 