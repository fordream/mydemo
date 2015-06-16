/**      
 * BadWordsImpl.java Create on 2014-1-29     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.words;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.utils.FileUtils;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: WordsManagerImpl
 * @Description: 关键字管理类
 * @author 王卫
 * @date 2014-1-29 下午2:24:52
 * @Version 1.0
 * 
 */
public class WordsManagerImpl implements WordsManager {

	private static final String TAG = "WordsManagerImpl";

	// 关键字集合
	private List<String> keyWords;
	
	// 名称关键字集合
	private List<String> namekeyWords;

	private static byte[] lock = new byte[0];

	private static WordsManagerImpl instance = null;

	private WordsManagerImpl() {
	}

	public static WordsManagerImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new WordsManagerImpl();
				return instance;
			}
		} else {
			return instance;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.words.BadWords#match(java.lang.String)
	 */
	@Override
	public boolean match(String content) {
		if (keyWords == null) {
			getWordsData();
		}
		if (keyWords != null) {
			for (String word : keyWords) {
				if (!"".equals(word) && content.contains(word)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.words.WordsManager#matchName(java.lang.String)
	 */
	@Override
	public boolean matchName(String content) {
		if (namekeyWords == null) {
			getNameWordsData();
		}
		if (namekeyWords != null) {
			for (String word : namekeyWords) {
				if (!"".equals(word) && content.contains(word)) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.words.BadWords#replace(java.lang.String)
	 */
	@Override
	public String replace(String content) {
		if (keyWords == null) {
			getWordsData();
		}
		if (keyWords != null) {
			for (String word : keyWords) {
				if (content.contains(word)) {
					content = content.replace(word, replaceWord(word));
				}
			}
		}
		return content;
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.words.WordsManager#replaceName(java.lang.String)
	 */
	@Override
	public String replaceName(String content) {
		if (namekeyWords == null) {
			getNameWordsData();
		}
		if (namekeyWords != null) {
			for (String word : namekeyWords) {
				if (content.contains(word)) {
					content = content.replace(word, replaceWord(word));
				}
			}
		}
		return content;
	}

	/**
	 * @param word
	 * @return
	 */
	private CharSequence replaceWord(String word) {
		StringBuffer sb = new StringBuffer();
		if (word != null) {
			for (int i = 0; i < word.length(); i++) {
				sb.append("*");
			}
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.words.WordsManager#setWords(java.lang.String)
	 */
	@Override
	public void setWords(String words) {
		if (words != null && !"".equals(words)) {
			String[] strarr = words.split("\\|");
			keyWords = new ArrayList<String>();
			for (int i = 0; i < strarr.length; i++) {
				keyWords.add(strarr[i]);
			}
			Collections.sort(keyWords, new Comparator<String>() {

				public int compare(String o1, String o2) {
					return o2.length() - o1.length();
				}

			});
			LogUtil.d(TAG, keyWords.toString());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.words.WordsManager#setNameWords(java.lang.String)
	 */
	@Override
	public void setNameWords(String nameWords) {
		if (nameWords != null && !"".equals(nameWords)) {
			String[] strarr = nameWords.split("\\|");
			namekeyWords = new ArrayList<String>();
			for (int i = 0; i < strarr.length; i++) {
				namekeyWords.add(strarr[i]);
			}
			Collections.sort(namekeyWords, new Comparator<String>() {

				public int compare(String o1, String o2) {
					return o2.length() - o1.length();
				}

			});
			LogUtil.d(TAG, namekeyWords.toString());
		}
	}

	/**
	 * 获取敏感词数据
	 * 
	 * @return
	 */
	private void getWordsData() {
		try {
			String words = (String) FileUtils.readFile(SystemContext.getInstance().getContext(), SystemConfig.MSGS_WORDS);
			setWords(words);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取名称敏感词数据
	 * 
	 * @return
	 */
	private void getNameWordsData() {
		try {
			String namewords = (String) FileUtils.readFile(SystemContext.getInstance().getContext(), SystemConfig.MSGS_NAMEWORDS);
			setNameWords(namewords);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
