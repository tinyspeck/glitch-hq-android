package com.tinyspeck.glitchhq;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinyspeck.glitchhq.BaseFragment.glitchAchievement;

public class AchievementsListViewAdapter extends BaseAdapter {

	private Vector<glitchAchievement> m_achList;
	private LayoutInflater m_inflater;
	private Activity m_act;
	private MyApplication m_application;
	private String m_category;
	
	public class ViewHolder 
	{
		ImageView icon;
		TextView name;
		View whole;
	}
	
	public AchievementsListViewAdapter(Activity act, Vector<glitchAchievement> achList, String category) 
	{	
		m_achList = achList;
		m_act = (HomeScreen)act;
		m_inflater = (LayoutInflater)m_act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_application = (MyApplication)m_act.getApplicationContext();
		m_category = category;
	}
	
	public int getCount() 
	{
		if (m_achList == null) 
			return 0;
		return m_achList.size();
	}

	public void setList(Vector<glitchAchievement> list) 
	{
		m_achList = list;
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
			convertView = m_inflater.inflate(R.layout.achievement_list_item, null);
			holder = new ViewHolder();
			
			holder.name = (TextView)convertView.findViewById(R.id.achievement_name);
			holder.name.setTypeface(m_application.m_vagFont);
			holder.icon = (ImageView)convertView.findViewById(R.id.icon_achievement);
			holder.whole = (View)convertView.findViewById(R.id.achfeed_item);
			
			convertView.setTag(holder);
		}
		holder = (ViewHolder)convertView.getTag();
		
		if (position < getCount()) 
		{
			glitchAchievement ach = m_achList.get(position);
			holder.name.setText(ach.name);
			if (ach.icon != null) {
				DrawableURL.Show(holder.icon, ach.icon, false);
			} else {
				holder.icon.setVisibility(View.GONE);
			}
		}
		holder.whole.setTag(position);
		holder.whole.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View arg0) {
				glitchAchievement currentAchievement = m_achList.get((Integer)arg0.getTag());
				AchievementDetailFragment fm = new AchievementDetailFragment(currentAchievement.id, m_category);
				((HomeScreen)m_act).setCurrentFragment(fm,  true);
			}
		});
		return convertView;
	}

}
