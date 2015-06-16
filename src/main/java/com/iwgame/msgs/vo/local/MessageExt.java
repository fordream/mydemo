/**      
 * MessageExt.java Create on 2015-1-16     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.vo.local;

/**
 * @ClassName: MessageExt
 * @Description: 消息扩展数据
 * @author 王卫
 * @date 2015-1-16 下午12:03:54
 * @Version 1.0
 * 
 */
public class MessageExt {
	// 操作码
	private int op;
	// 内容
	private Content content;

	public int getOp() {
		return op;
	}

	public void setOp(int op) {
		this.op = op;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	// 内容
	public class Content {
		/**
		 * 
		 */
		public Content() {
			// TODO Auto-generated constructor stub
		}

		// 用户id
		private long uid;
		// 信息描述
		private String msg;

		public long getUid() {
			return uid;
		}

		public void setUid(long uid) {
			this.uid = uid;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}
