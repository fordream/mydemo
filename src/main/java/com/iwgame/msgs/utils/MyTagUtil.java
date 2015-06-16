/**      
 * MyActionTagUtil.java Create on 2014-1-8     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.youban.msgs.R;
import com.iwgame.msgs.common.MyTagSpan;
import com.iwgame.msgs.common.MyTagSpan.MyTagClickListener;
import com.iwgame.msgs.widget.smiley.SmileyVo;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: MyActionTagUtil
 * @Description: TODO(自定义消息中的标签解析工具(包括表情标签)
 * @author chuanglong
 * @date 2014-1-8 下午6:02:24
 * @Version 1.0
 * 
 */
public class MyTagUtil {
	public static String TAG_FORMAT_PREFIX_PREFIX = "[iwgame:";
	public static String TAG_FORMAT_PREFIX_SUFFIX = "]";
	public static String TAG_FORMAT_SUFFIX = "[/iwgame]";
	public static String TAG_FOLLOW = "[iwgame:follow]";
	public static String TAG_SEX_REG = "\\[gender:[0-9]\\]";
	public static String TAG_REG_STARTIWGAME = "\\[iwgame:\\d+\\]";
	public static String TAG_REG_ENDIWGAME = "\\[/iwgame\\]";
	public static String TAG_REG_IWGAME = "iwgame";

	/** 性别名前缀 **/
	public static final String NAME_SEX_PREFIX = "chat_sex_";
	/** 性别格式化前缀 **/
	public static final String FORMAT_SEX_PREFIX = "[gender:";
	/** 性别格式后缀 **/
	public static final String FORMAT_SEX_SUFFIX = "]";

	/**
	 * 替换自己定义的标签为spanurl 自己定义标签的格式
	 * [iwgame:action(|param1|param2|...)]actionname[/iwgame] 表情标签[em:n]
	 * 
	 * @param content
	 * @param content
	 * @param resW
	 * @param resH
	 * @return
	 */
	public static SpannableString analyseMyTag(Context context, String content, int resW, int resH, MyTagClickListener listener) {

		ArrayList<LinkSpec> linkspecs = new ArrayList<LinkSpec>();
		// 先获得linkspecs

		int len = 0;
		int starts = 0;
		int end = 0;
		while (len < content.length()) {

			if (content.indexOf(TAG_FORMAT_PREFIX_PREFIX, starts) != -1) {
				starts = content.indexOf(TAG_FORMAT_PREFIX_PREFIX, starts);
				if (content.indexOf(TAG_FORMAT_PREFIX_SUFFIX, starts + TAG_FORMAT_PREFIX_PREFIX.length()) != -1) {

					end = content.indexOf(TAG_FORMAT_PREFIX_SUFFIX, starts + TAG_FORMAT_PREFIX_PREFIX.length());
					String actionandparams = content.substring(starts + TAG_FORMAT_PREFIX_PREFIX.length(), end);

					if (content.indexOf(TAG_FORMAT_SUFFIX, end) != -1) {
						int tmp_starts = content.indexOf(TAG_FORMAT_SUFFIX, end);
						String allstr = content.substring(starts, tmp_starts + TAG_FORMAT_SUFFIX.length());

						// 获得actionandparams 和actionname
						String actionname = content.substring(end + TAG_FORMAT_PREFIX_SUFFIX.length(), tmp_starts);
						// 替换字符串，增加链接
						content = content.replace(allstr, actionname);
						LinkSpec linkspec = new LinkSpec();
						linkspec.url = actionandparams;
						linkspec.start = starts;
						linkspec.end = starts + actionname.length();
						linkspecs.add(linkspec);
						starts = starts + actionname.length();
						len = starts + actionname.length();
						end = starts + actionname.length() + 1;
					} else {
						break;
					}

				} else {
					break;
				}
			} else {
				break;
			}

		}

		SpannableString ss = new SpannableString(content);
		// 增加链接
		for (int i = 0; i < linkspecs.size(); i++) {
			LinkSpec linkspec = linkspecs.get(i);
			MyTagSpan span = new MyTagSpan(linkspec.url);
			span.setMyTagClickListener(listener);
			ss.setSpan(span, linkspec.start, linkspec.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		// 替换表情
		len = 0;
		starts = 0;
		end = 0;
		while (starts < content.length()) {
			// [sm:0]
			starts = content.indexOf(SmileyVo.FORMAT_PREFIX, starts);
			if (starts != -1) {
				end = content.indexOf(SmileyVo.FORMAT_SUFFIX, starts + SmileyVo.FORMAT_PREFIX.length());
				if (end != -1) {
					String phrase = content.substring(starts + SmileyVo.FORMAT_PREFIX.length(), end);
					String imageName = SmileyVo.NAME_PREFIX + phrase;
					try {
						Field f = (Field) R.drawable.class.getDeclaredField(imageName);
						int i = f.getInt(R.drawable.class);
						Drawable drawable = context.getResources().getDrawable(i);
						if (drawable != null) {
							drawable.setBounds(0, 0, resW > 0 ? resW : drawable.getIntrinsicWidth(), resH > 0 ? resH : drawable.getIntrinsicHeight());
							ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
							ss.setSpan(span, starts, end + SmileyVo.FORMAT_SUFFIX.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {

					}
					starts = end + 1;
				} else {
					starts = content.length();
				}
			} else {
				starts = content.length();
			}
		}
		return ss;
	}

	/**
	 * 
	 * @param context
	 * @param content
	 * @param resW
	 * @param resH
	 * @return
	 */
	public static SpannableString analyseMessageTag(Context context, String content, int resW, int resH) {
		// 先获得linkspecs
		int len = 0;
		int starts = 0;
		int end = 0;
		while (len < content.length()) {

			if (content.indexOf(TAG_FORMAT_PREFIX_PREFIX, starts) != -1) {
				starts = content.indexOf(TAG_FORMAT_PREFIX_PREFIX, starts);
				if (content.indexOf(TAG_FORMAT_PREFIX_SUFFIX, starts + TAG_FORMAT_PREFIX_PREFIX.length()) != -1) {

					end = content.indexOf(TAG_FORMAT_PREFIX_SUFFIX, starts + TAG_FORMAT_PREFIX_PREFIX.length());
					String actionandparams = content.substring(starts + TAG_FORMAT_PREFIX_PREFIX.length(), end);

					if (content.indexOf(TAG_FORMAT_SUFFIX, end) != -1) {
						int tmp_starts = content.indexOf(TAG_FORMAT_SUFFIX, end);
						String allstr = content.substring(starts, tmp_starts + TAG_FORMAT_SUFFIX.length());

						// 获得actionandparams 和actionname
						String actionname = content.substring(end + TAG_FORMAT_PREFIX_SUFFIX.length(), tmp_starts);
						content = content.replace(allstr, "");
						starts = starts + actionname.length();
						len = starts + actionname.length();
						end = starts + actionname.length() + 1;
					} else {
						break;
					}

				} else {
					break;
				}
			} else {
				break;
			}

		}

		SpannableString ss = new SpannableString(content);
		return ss;
	}

	/**
	 * 解析带有性别标识的文本
	 * 
	 * @param context
	 * @param source
	 * @return
	 */
	public static SpannableString praseSexTagText(Context context, String source, int resW, int resH) {
		SpannableString ss = new SpannableString(source);
		// 替换表情
		int starts = 0;
		int end = 0;
		while (starts < source.length()) {
			// [sex:0]
			starts = source.indexOf(FORMAT_SEX_PREFIX, starts);
			if (starts != -1) {
				end = source.indexOf(FORMAT_SEX_SUFFIX, starts + FORMAT_SEX_PREFIX.length());
				if (end != -1) {
					String phrase = source.substring(starts + FORMAT_SEX_PREFIX.length(), end);
					String imageName = NAME_SEX_PREFIX + phrase;
					try {
						Field f = (Field) R.drawable.class.getDeclaredField(imageName);
						int i = f.getInt(R.drawable.class);
						Drawable drawable = context.getResources().getDrawable(i);
						if (drawable != null) {
							drawable.setBounds(0, 0, resW > 0 ? resW : drawable.getIntrinsicWidth(), resH > 0 ? resH : drawable.getIntrinsicHeight());
							ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
							ss.setSpan(span, starts, end + FORMAT_SEX_SUFFIX.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {

					}
					starts = end + 1;
				} else {
					starts = source.length();
				}
			} else {
				starts = source.length();
			}
		}
		return ss;
	}

	public static class LinkSpec {
		String url;
		int start;
		int end;
	}

	/**
	 * 把action操作替换成相应的字符串
	 * 
	 * @param content
	 * @param action
	 * @param replaceStr
	 * @return
	 */
	public static String replaceAction(String content, int action, String replaceStr) {

		int len = 0;
		int starts = 0;
		int end = 0;
		while (len < content.length()) {

			if (content.indexOf(TAG_FORMAT_PREFIX_PREFIX, starts) != -1) {
				starts = content.indexOf(TAG_FORMAT_PREFIX_PREFIX, starts);
				if (content.indexOf(TAG_FORMAT_PREFIX_SUFFIX, starts + TAG_FORMAT_PREFIX_PREFIX.length()) != -1) {

					end = content.indexOf(TAG_FORMAT_PREFIX_SUFFIX, starts + TAG_FORMAT_PREFIX_PREFIX.length());
					String actionandparams = content.substring(starts + TAG_FORMAT_PREFIX_PREFIX.length(), end);

					if (content.indexOf(TAG_FORMAT_SUFFIX, end) != -1) {
						int tmp_starts = content.indexOf(TAG_FORMAT_SUFFIX, end);
						String allstr = content.substring(starts, tmp_starts + TAG_FORMAT_SUFFIX.length());

						// 获得actionandparams 和actionname
						// String actionname = content.substring(end +
						// TAG_FORMAT_PREFIX_SUFFIX.length(), tmp_starts);
						// 替换字符串
						if (actionandparams != null && !actionandparams.equals("")) {
							String[] tmp = new String[2];
							int index = actionandparams.indexOf("{");
							if (index != -1) {
								tmp[0] = actionandparams.substring(0, index);
								tmp[1] = actionandparams.substring(index);
							} else {
								tmp[0] = actionandparams;
								tmp[1] = "";
							}

							try {
								int tmpaction = Integer.parseInt(tmp[0]);
								if (tmpaction == action) {
									content = content.replace(allstr, replaceStr);
									starts = starts + replaceStr.length();
									len = starts + replaceStr.length();
									end = starts + replaceStr.length() + 1;
								} else {
									starts = starts + allstr.length();
									len = starts + allstr.length();
									end = starts + allstr.length() + 1;
								}
							} catch (Exception ex) {

							}
						}

					} else {
						break;
					}

				} else {
					break;
				}
			} else {
				break;
			}

		}
		return content;
	}

	/**
	 * 获取推荐游戏消息操作码
	 * 
	 * @param content
	 * @return
	 */
	public static int getMessageOpCode(String content) {
		if (content.contains(TAG_FORMAT_PREFIX_PREFIX)) {
			String perfixcnt = content.substring(content.indexOf(TAG_FORMAT_PREFIX_PREFIX), content.indexOf(TAG_FORMAT_PREFIX_SUFFIX) + 1);
			if (perfixcnt.contains("{")) {
				String optcnt = perfixcnt.substring(perfixcnt.indexOf(TAG_FORMAT_PREFIX_PREFIX) + TAG_FORMAT_PREFIX_PREFIX.length(),
						perfixcnt.indexOf("{"));
				return Integer.valueOf(optcnt);
			} else {
				String optcnt = perfixcnt.substring(perfixcnt.indexOf(TAG_FORMAT_PREFIX_PREFIX) + TAG_FORMAT_PREFIX_PREFIX.length(),
						perfixcnt.indexOf(TAG_FORMAT_PREFIX_SUFFIX));
				return Integer.valueOf(optcnt);
			}
		} else {
			return 0;
		}
	}

	/**
	 * 获取推荐游戏消息显示内容
	 * 
	 * @param content
	 * @return
	 */
	public static String getMessageShowContent(String content) {
		if (content.contains(TAG_FORMAT_PREFIX_PREFIX)) {
			return content.substring(0, content.indexOf(TAG_FORMAT_PREFIX_PREFIX));
		} else {
			return content;
		}
	}

	/**
	 * 获取推荐游戏操作文字内容
	 * 
	 * @param content
	 * @return
	 */
	public static String getOptContent(String content) {
		if (content.contains(TAG_FORMAT_PREFIX_PREFIX)) {
			return content.substring(content.indexOf(TAG_FORMAT_PREFIX_PREFIX));
		} else {
			return content;
		}
	}

	/**
	 * 获取推荐游戏操作GID
	 * 
	 * @param content
	 * @return
	 */
	public static long getOptRecommendGameId(String content) {
		if (content.contains("{\"gid\":")) {
			return Long.valueOf(content.substring(content.indexOf("\":") + 2, content.indexOf("}")));
		} else {
			return 0;
		}
	}
}
