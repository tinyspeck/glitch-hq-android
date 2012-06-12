package com.tinyspeck.glitchhq;

import java.util.Vector;

import com.tinyspeck.glitchhq.BaseFragment.glitchItem;
import com.tinyspeck.glitchhq.BaseFragment.glitchItemCategory;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EncyclopediaItemsInCategoryListViewAdapter extends BaseAdapter {

	private Vector<glitchItem> m_itemList;
	private LayoutInflater m_inflater;
	private Activity m_act;
	private MyApplication m_application;
	private glitchItemCategory m_category;
	
	public class ViewHolder
	{
		ImageView icon;
		TextView name;
		View whole;
	}
	
	public EncyclopediaItemsInCategoryListViewAdapter(Activity act, Vector<glitchItem> itemList, glitchItemCategory category)
	{
		m_itemList = itemList;
		m_act = act;
		m_inflater = (LayoutInflater) m_act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_application = (MyApplication) m_act.getApplicationContext();
		m_category = category;
	}
	
	public int getCount() {
		if (m_itemList == null)
			return 0;
		return m_itemList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder = null;
		
		if (convertView != null) {
			holder = (ViewHolder)convertView.getTag();
		}
			
		if (holder == null) {
			convertView = m_inflater.inflate(R.layout.encyclopedia_items_in_category_list_item, null);
			holder = new ViewHolder();
			
			holder.name = (TextView)convertView.findViewById(R.id.encyclopedia_items_in_category_name);
			holder.name.setTypeface(m_application.m_vagFont);
			holder.icon = (ImageView)convertView.findViewById(R.id.encyclopedia_items_in_category_icon);
			holder.whole = (View)convertView.findViewById(R.id.encyclopedia_items_in_category_item);
			
			convertView.setTag(holder);
		}
		holder = (ViewHolder)convertView.getTag();
		
		if (position < getCount()) {			
			glitchItem item = m_itemList.get(position);
			
			holder.name.setText(item.name);
			DrawableURL.Show(holder.icon, item.icon, false);
			
			holder.whole.setTag(position);
			holder.whole.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					glitchItem item = m_itemList.get((Integer)v.getTag());
					//EncyclopediaItemDetailFragment f = new EncyclopediaItemDetailFragment(item, m_category);
					//((HomeScreen)m_act).setCurrentFragment(f, true);
				}
			});
		}
		
		return convertView;
	}

}
