/**      
 * ContactObj.java Create on 2014-4-24     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.object;

import java.util.ArrayList;
import java.util.List;

import com.iwgame.msgs.module.account.object.UserObject;

/**
 * @ClassName: ContactObj
 * @Description: 通讯录对象(包含待关注和待邀请对象集合)
 * @author 王卫
 * @date 2014-4-24 上午10:36:08
 * @Version 1.0
 * 
 */
public class ContactObj {
	// 待关注用户对象
	public List<UserObject> followObjects = new ArrayList<UserObject>();
	// 待邀请用户对象
	public List<UserObject> inviteObjects = new ArrayList<UserObject>();

}
