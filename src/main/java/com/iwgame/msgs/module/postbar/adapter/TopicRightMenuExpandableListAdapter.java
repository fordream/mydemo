/**      
* TopicRightMenuExpandableListAdapter.java Create on 2014-4-16     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.postbar.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youban.msgs.R;

/** 
 * @ClassName: TopicRightMenuExpandableListAdapter 
 * @Description: TODO(帖子页面右边弹出菜单的适配器) 
 * @author chuanglong
 * @date 2014-4-16 下午3:55:42 
 * @Version 1.0
 * 
 */
public class TopicRightMenuExpandableListAdapter extends BaseExpandableListAdapter {
    
    private Context context;
    private List<String> listDataHeader;
    private Map<String, List<ChildItem>> listDataChild;
    private LayoutInflater inflater ;

    public TopicRightMenuExpandableListAdapter(Context context, List<String> listDataHeader, Map<String, List<ChildItem>> listDataChild) {
        super();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildItem childItem = (ChildItem) getChild(groupPosition, childPosition);
        ChildViewHolder viewHolder = new ChildViewHolder();
        if(convertView==null){
            convertView = this.inflater.inflate(R.layout.postbar_topiclist_right_menu_main_item, null);
            viewHolder.tv_list_item = (TextView)convertView.findViewById(R.id.tv_list_item);
            viewHolder.iv_list_item = (ImageView)convertView.findViewById(R.id.iv_list_item);
          convertView.setTag(viewHolder);
	} else {
	    viewHolder = (ChildViewHolder) convertView.getTag();
	}
        viewHolder.tv_list_item.setText(childItem.itemName);
        if(childItem.isSelect)
        {
           viewHolder.iv_list_item.setVisibility(View.VISIBLE);
           convertView.setBackgroundColor(context.getResources().getColor(R.color.global_color6));
        }
        else
        {
            viewHolder.iv_list_item.setVisibility(View.INVISIBLE);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.global_color4));
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String groupText = this.listDataHeader.get(groupPosition);
        GroupViewHolder viewHolder = new GroupViewHolder();
        if(convertView==null){
            convertView = this.inflater.inflate(R.layout.postbar_topiclist_right_menu_main_group, null);
            viewHolder.tv_list_header = (TextView)convertView.findViewById(R.id.tv_list_header);
          convertView.setTag(viewHolder);
	} else {
	    viewHolder = (GroupViewHolder) convertView.getTag();
	}
        
        viewHolder.tv_list_header.setText(groupText);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
    class GroupViewHolder {
	TextView tv_list_header;
	
    }
    
    class ChildViewHolder {
	TextView tv_list_item;
	ImageView iv_list_item ;
    }
    
}
