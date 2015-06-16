/**      
 * UserVo.java Create on 2014-10-14     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.object;

/**
 * @ClassName: GuestObject
 * @Description: TODO(...)
 * @author 徐亚辉
 * @date 2014-10-14 下午5:38:13
 * @Version 1.0
 * 
 */
public class GuestObject {
	//账号
	private String account;
	//密码
	private String password;
	//手机绑定状态
	private String status;

	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
