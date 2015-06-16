/**      
* SubjectVo.java Create on 2014-4-2     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.chat.vo;

/** 
 * @ClassName: SubjectVo 
 * @Description: TODO(主体对象) 
 * @author chuanglong
 * @param <T>
 * @date 2014-4-2 上午11:11:42 
 * @Version 1.0
 * 
 */
public class SubjectVo {
    
    /**
     * 主体id
     */
    private long subjectId;
    /**
     * 主体类型
     */
    private int subjectType;
    
    private Object   subject ;

    /**
     * @return the subjectId
     */
    public long getSubjectId() {
        return subjectId;
    }

    /**
     * @param subjectId the subjectId to set
     */
    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    /**
     * @return the subjectType
     */
    public int getSubjectType() {
        return subjectType;
    }

    /**
     * @param subjectType the subjectType to set
     */
    public void setSubjectType(int subjectType) {
        this.subjectType = subjectType;
    }

    /**
     * @return the subject
     */
    public Object getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(Object subject) {
        this.subject = subject;
    }
}
