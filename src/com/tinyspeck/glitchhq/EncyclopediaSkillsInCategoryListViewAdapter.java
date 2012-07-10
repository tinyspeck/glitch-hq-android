package com.tinyspeck.glitchhq;

import java.util.Vector;

import com.tinyspeck.glitchhq.BaseFragment.skillAvailable;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EncyclopediaSkillsInCategoryListViewAdapter extends BaseAdapter {

	private Vector<skillAvailable> m_skillsList;
	private LayoutInflater m_inflater;
	private Activity m_act;
	private BaseFragment m_bf;
	private MyApplication m_application;
	
	public class ViewHolder
	{
		ImageView icon;
		TextView name;
		View whole;
	}
	
	public EncyclopediaSkillsInCategoryListViewAdapter(BaseFragment bf, Vector<skillAvailable> skillsList)
	{
		m_skillsList = skillsList;
		m_bf = bf;
		m_act = bf.getActivity();
		m_inflater = (LayoutInflater) m_act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_application = (MyApplication) m_act.getApplicationContext();
	}
	
	public int getCount() 
	{
		if (m_skillsList == null)
			return 0;
		return m_skillsList.size();
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
			convertView = m_inflater.inflate(R.layout.encyclopedia_skills_in_category_list_item, null);
			holder = new ViewHolder();
			
			holder.name = (TextView)convertView.findViewById(R.id.encyclopedia_skills_in_category_name);
			holder.name.setTypeface(m_application.m_vagFont);
			holder.icon = (ImageView)convertView.findViewById(R.id.encyclopedia_skills_in_category_icon);
			holder.whole = (View)convertView.findViewById(R.id.encyclopedia_skills_in_category_item);
			
			convertView.setTag(holder);
		}
		holder = (ViewHolder)convertView.getTag();
		
		if (position < getCount()) {
			skillAvailable skill = m_skillsList.get(position);
			
			holder.name.setText(skill.item);
			DrawableURL.Show(holder.icon, skill.icon, false);
			
			holder.whole.setTag(position);
			holder.whole.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					skillAvailable skill = m_skillsList.get((Integer)v.getTag());
					SkillDetailFragment f = new SkillDetailFragment(m_bf, skill.id);
					((HomeScreen)m_act).setCurrentFragment(f, true);
				}
			});
		}
		
		return convertView;
	}

}
