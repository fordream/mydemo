/**      
* SmileyVo.java Create on 2013-12-6     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.widget.smiley;

/** 
 * @ClassName: SmileyVo 
 * @Description: TODO(表情对象) 
 * @author chuanglong
 * @date 2013-12-6 下午4:35:24 
 * @Version 1.0
 * 
 */
public class SmileyVo {
    
    /**
     * 表情名前缀
     */
    public static final String NAME_PREFIX ="smiley_";
    
    /**
     * 表情格式化前缀
     */
    public static final String FORMAT_PREFIX ="[sm:";
    /**
     * 表情格式后缀
     */
    public static final String FORMAT_SUFFIX ="]";
    
    /**
     * 名字前缀
     */
    public String namePrefix ;
    /**
     * id
     */
    public String id;   
    /**
     * 名字字符串
     */
    public String name;
    /**
     * 格式化替换的字符串
     */
    public String format ;
    public  SmileyVo(String fullName)
    {
	if(fullName.startsWith(NAME_PREFIX))
	{
	    namePrefix = NAME_PREFIX ;
	    id = fullName.substring(NAME_PREFIX.length());
	    name = fullName;
	    format = FORMAT_PREFIX + id + FORMAT_SUFFIX;
	}
    }
    
    public void parse(String format)
    {
	if(format.startsWith(FORMAT_PREFIX) && format.endsWith(FORMAT_SUFFIX))
	{
	    this.format = format ;
	    id = format.substring(NAME_PREFIX.length(),format.length()-2);
	    name = NAME_PREFIX + id ;
	    namePrefix = NAME_PREFIX;
	}
    }

    /**
     * @return the namePrefix
     */
    public String getNamePrefix() {
        return namePrefix;
    }

    /**
     * @param namePrefix the namePrefix to set
     */
    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }
    
    

}
