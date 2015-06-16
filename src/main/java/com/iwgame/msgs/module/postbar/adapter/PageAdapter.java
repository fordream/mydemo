/**      
 * PostListAdapter.java Create on 2013-12-23     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youban.msgs.R;

/**
 * @ClassName: PostListAdapter
 * @Description: TODO(页码适配器)
 * @author chuanglong
 * @date 2013-12-23 下午12:35:09
 * @Version 1.0
 * 
 */
public class PageAdapter extends BaseAdapter {

    protected static final String TAG = "PageAdapter";
    private Context context;
    private int total;
    LayoutInflater inflater ;


    public PageAdapter(Context context, int total) {
	this.context = context;
	this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.total = total ;
    }
    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
	// TODO Auto-generated method stub
	return total;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
	// TODO Auto-generated method stub
	return position + 1 ;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
	// TODO Auto-generated method stub
	return position;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
	// TODO Auto-generated method stub
	ViewHolder tmpViewHolder = new ViewHolder();
	if (convertView == null) {

	    convertView = this.inflater.inflate(R.layout.postbar_topicreply_pagenum_gallery_item, null);
	    tmpViewHolder.postbar_topicreply_pagenum = (TextView) convertView.findViewById(R.id.postbar_topicreply_pagenum);
	       convertView.setTag(tmpViewHolder);
	} else {
	    tmpViewHolder = (ViewHolder) convertView.getTag();
	}
	final ViewHolder viewHolder = tmpViewHolder;
	viewHolder.postbar_topicreply_pagenum.setText((position +1)+"");
	return convertView;
    }

    class ViewHolder {
	TextView postbar_topicreply_pagenum;
    }
}
