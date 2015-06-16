package com.iwgame.msgs.widget.smiley;

import java.lang.reflect.Field;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.youban.msgs.R;

public class SmileyPagerGridAdapter extends BaseAdapter {

    List<Object> data;
    Context context;
    int pageIndex;
    LayoutInflater inflater;

    public SmileyPagerGridAdapter(Context context, List<Object> data, int i) {
	this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	this.context = context;
	this.data = data;
	this.pageIndex = i;
    }

    @Override
    public int getCount() {
	// TODO Auto-generated method stub
	return data.size();
    }

    @Override
    public Object getItem(int position) {
	// TODO Auto-generated method stub
	return data.get(position);
    }

    @Override
    public long getItemId(int position) {
	// TODO Auto-generated method stub
	return position;
    }

    @Override
    public int getItemViewType(int position) {
	// TODO Auto-generated method stub
	return super.getItemViewType(position);

    }

    @Override
    public int getViewTypeCount() {
	// TODO Auto-generated method stub
	return super.getViewTypeCount();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	// TODO Auto-generated method stub

	if (convertView == null) {
	    convertView = this.inflater.inflate(R.layout.smiley_grid_item, null);
	}
	Object obj = data.get(position);
	String imageName = "";
	if(obj instanceof SmileyVo)
	{
	    imageName =((SmileyVo)obj).getName();
	}
	else if (obj instanceof DelSmileyVo)
	{
	    imageName = DelSmileyVo.FILENAME;
	}

	ImageView iv = (ImageView) convertView.findViewById(R.id.smiley_grid_item);
	Field f;
	try {
	    f = (Field) R.drawable.class.getDeclaredField(imageName);
	    int i = f.getInt(R.drawable.class);
	    // Drawable drawable = context.getResources().getDrawable(i);
	    iv.setImageResource(i);
	} catch (SecurityException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (NoSuchFieldException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	convertView.setTag(obj);

	return convertView;

    }

}
