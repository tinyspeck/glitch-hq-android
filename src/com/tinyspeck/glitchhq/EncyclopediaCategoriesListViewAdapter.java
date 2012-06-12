package com.tinyspeck.glitchhq;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EncyclopediaCategoriesListViewAdapter extends BaseAdapter {

	private Vector<String> m_categoriesList;
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
	
	public EncyclopediaCategoriesListViewAdapter(BaseFragment bf, Vector<String> categoriesList)
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

	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if (convertView != null) {
			holder = (ViewHolder)convertView.getTag();
		}
		
		if (holder == null) {
			convertView = m_inflater.inflate(R.layout.encyclopedia_category_list_item, null);
			holder = new ViewHolder();
			
			holder.icon = (ImageView)convertView.findViewById(R.id.encyclopedia_category_icon);
			holder.name = (TextView)convertView.findViewById(R.id.encyclopedia_category_name);
			holder.name.setTypeface(m_application.m_vagFont);
			holder.whole = (View)convertView.findViewById(R.id.encyclopedia_category_item);
			convertView.setTag(holder);
		}
		holder = (ViewHolder)convertView.getTag();
		
		if (position < getCount()) {
			String category = m_categoriesList.get(position);
			holder.name.setText(category);
			holder.whole.setTag(position);
			if (category == "Items") {
				holder.icon.setImageBitmap(BitmapFactory.decodeResource(m_act.getResources(), R.drawable.enc_items));
				holder.whole.setOnClickListener(new OnClickListener() 
				{
					public void onClick(View v) {
						String category = m_categoriesList.get((Integer)v.getTag());
						EncyclopediaItemCategoriesFragment f = new EncyclopediaItemCategoriesFragment();
						((HomeScreen)m_act).setCurrentFragment(f, true);
					}
				});
			} else if (category == "Skills") {
				holder.icon.setImageBitmap(BitmapFactory.decodeResource(m_act.getResources(), R.drawable.enc_skills));
				holder.whole.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						String category = m_categoriesList.get((Integer)v.getTag());
						//EncyclopediaSkillsFragment f = new EncyclopediaSkillsFragment();
						//((HomeScreen)m_act).setCurrentFragment(f, true);
					}
				});
			} else if (category == "Achievements") {
				holder.icon.setImageBitmap(BitmapFactory.decodeResource(m_act.getResources(), R.drawable.enc_achievements));
				holder.whole.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						String category = m_categoriesList.get((Integer)v.getTag());
						//AchievementCategoriesFragment f = new AchievementCategoriesFragment();
						//((HomeScreen)m_act).setCurrentFragment(f, true);
					}
				});
			} else if (category == "Locations") {
				holder.icon.setImageBitmap(BitmapFactory.decodeResource(m_act.getResources(), R.drawable.enc_locations));
				holder.whole.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						String category = m_categoriesList.get((Integer)v.getTag());
						//EncyclopediaLocationHubsFragment f = new EncyclopediaLocationHubsFragment();
						//((HomeScreen)m_act).setCurrentFragment(f, true);
					}
				});
			} else if (category == "Giants") {
				holder.icon.setImageBitmap(BitmapFactory.decodeResource(m_act.getResources(), R.drawable.enc_giants));
				//EncyclopediaGiantsFragment f = new EncyclopediaGiantsFragment();
				//((HomeScreen)m_act).setCurrentFragment(f, true);
			}
		}
		
		return convertView;
	}

}
