package com.iwgame.msgs.common;

public interface JavaScriptAction {
	public void changeTopView(String title, int showType, int pageId);
	
	public void setShareData(String imageUrl, String shareTitle, String shareContent);
	
	public void gotoTopicListPage();
	
	public void gotoTopicDetailPage(long topicId);
	
	public void showOrDismissTabHost(boolean isShow);
}
