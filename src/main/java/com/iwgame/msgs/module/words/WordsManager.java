/**      
 * BadWords.java Create on 2014-1-29     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.words;

/**
 * @ClassName: WordsManager
 * @Description: 敏感词管理接口
 * @author 王卫
 * @date 2014-1-29 下午2:22:46
 * @Version 1.0
 * 
 */
public interface WordsManager {

	/**
	 * 文字内容
	 * 
	 * @param content
	 */
	public boolean match(String content);

	/**
	 * 文字名称内容
	 * 
	 * @param content
	 */
	public boolean matchName(String content);

	/**
	 * 替换
	 * 
	 * @param content
	 * @return
	 */
	public String replace(String content);

	/**
	 * 替换名称关键词
	 * 
	 * @param content
	 * @return
	 */
	public String replaceName(String content);

	/**
	 * 设置关键词（格式为“xxx|xxx|xxx”）
	 * 
	 * @param words
	 */
	public void setWords(String words);

	/**
	 * 设置名称关键词（格式为“xxx|xxx|xxx”）
	 * 
	 * @param nameWords
	 */
	public void setNameWords(String nameWords);

}
