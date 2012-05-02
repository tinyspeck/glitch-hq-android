package com.tinyspeck.glitchhq;

import java.util.Vector;

import com.tinyspeck.glitchhq.BaseFragment.glitchAchievementCategory;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AchievementCategoriesListViewAdapter extends BaseAdapter {

	private Vector<glitchAchievementCategory> m_categoriesList;
	private LayoutInflater m_inflater;
	private Activity m_act;
	private BaseFragment m_bf;
	private MyApplication m_application;
	
	public class ViewHolder 
	{
		TextView name;
		TextView count;
		View whole;
	}
	
	public AchievementCategoriesListViewAdapter(BaseFragment bf, Vector<glitchAchievementCategory> categoriesList) 
	{
		m_categoriesList = categoriesList;
		m_act = bf.getActivity();
		m_bf = bf;
		m_inflater = (LayoutInflater)m_act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_application = (MyApplication)m_act.getApplicationContext();
	}

	public int getCount() 
	{
		if (m_categoriesList == null)
			return 0;
		return m_categoriesList.size();
	}

	public Object getItem(int position) 
	{
		return position;
	}

	public long getItemId(int position) 
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder = null;
		
		if (convertView != null) {
			holder = (ViewHolder)convertView.getTag();		
		}
		
		if (holder == null) {
			convertView = m_inflater.inflate(R.layout.category_list_item, null);
			holder = new ViewHolder();
			
			holder.name = (TextView)convertView.findViewById(R.id.category_name);
			holder.name.setTypeface(m_application.m_vagFont);
			holder.count = (TextView)convertView.findViewById(R.id.category_count);
			holder.count.setTypeface(m_application.m_vagLightFont);
			holder.whole = (View)convertView.findViewById(R.id.category_item);
			convertView.setTag(holder);
		}
		holder = (ViewHolder)convertView.getTag();
		
		if (position < getCount()) {
			glitchAchievementCategory category = m_categoriesList.get(position);
			holder.name.setText(category.name);
			holder.count.setText(category.completed + "/" + category.total);
		}
		holder.whole.setTag(position);
		holder.whole.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View arg0) {
				glitchAchievementCategory category = m_categoriesList.get((Integer)arg0.getTag());
				AchievementsFragment fm = new AchievementsFragment(category);
				((HomeScreen)m_act).setCurrentFragment(fm, true);
			}			
		});		
		return convertView;
	}

}
