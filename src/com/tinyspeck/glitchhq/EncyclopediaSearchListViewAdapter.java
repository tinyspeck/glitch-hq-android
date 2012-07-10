package com.tinyspeck.glitchhq;

import java.util.Vector;

import com.tinyspeck.glitchhq.BaseFragment.glitchGiant;
import com.tinyspeck.glitchhq.BaseFragment.glitchItem;
import com.tinyspeck.glitchhq.BaseFragment.glitchLocationStreet;
import com.tinyspeck.glitchhq.BaseFragment.searchResult;
import com.tinyspeck.glitchhq.BaseFragment.skillAvailable;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EncyclopediaSearchListViewAdapter extends BaseAdapter {
	
	private Vector<searchResult> m_searchResultsList;
	private LayoutInflater m_inflater;
	private Activity m_act;	
	private BaseFragment m_bf;
	private EditText m_searchBox;
	private MyApplication m_application;
	
	public EncyclopediaSearchListViewAdapter(BaseFragment bf, EditText searchBox, Vector<searchResult> searchResultsList)
	{
		m_searchResultsList = searchResultsList;
		m_act = bf.getActivity();
		m_bf = bf;
		m_searchBox = searchBox;
		m_inflater = (LayoutInflater)m_act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_application = (MyApplication)m_act.getApplicationContext();
	}
	
	public class ViewHolder
	{
		ImageView icon;
		TextView name;
		View whole;
	}
	
	public int getCount() 
	{
		if (m_searchResultsList == null)
			return 0;
		return m_searchResultsList.size();
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
			convertView = m_inflater.inflate(R.layout.encyclopedia_search_results_list_item, null);
			holder = new ViewHolder();
			
			holder.icon = (ImageView)convertView.findViewById(R.id.encyclopedia_search_result_icon);
			holder.name = (TextView)convertView.findViewById(R.id.encyclopedia_search_result_name);
			holder.name.setTypeface(m_application.m_vagFont);
			holder.whole = (View)convertView.findViewById(R.id.encyclopedia_search_result_item);
			convertView.setTag(holder);
		}
		holder = (ViewHolder)convertView.getTag();
		
		if (position < getCount()) {
			searchResult result = m_searchResultsList.get(position);
			DrawableURL.Show(holder.icon, result.icon, false);
			holder.name.setText(result.name);
			holder.whole.setTag(position);
			holder.whole.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					searchResult result = m_searchResultsList.get((Integer)v.getTag());
					InputMethodManager imm = (InputMethodManager)m_act.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(m_searchBox.getWindowToken(), 0);
					if (result.type.equalsIgnoreCase("Giants")) {
						glitchGiant giant = m_bf.new glitchGiant();
						giant.id = result.id;
						EncyclopediaGiantDetailFragment f = new EncyclopediaGiantDetailFragment(m_bf, giant);
						((HomeScreen)m_act).setCurrentFragment(f, true);
					} else if (result.type.equalsIgnoreCase("Locations")) {
						glitchLocationStreet street = m_bf.new glitchLocationStreet();
						street.tsid = result.id;
						street.name = result.name;
						EncyclopediaStreetDetailFragment f = new EncyclopediaStreetDetailFragment(null, street);
						((HomeScreen)m_act).setCurrentFragment(f, true);
					} else if (result.type.equalsIgnoreCase("Skills")) {
						SkillDetailFragment f = new SkillDetailFragment(m_bf, result.id);
						((HomeScreen)m_act).setCurrentFragment(f, true);
					} else if (result.type.equalsIgnoreCase("Items")) {
						EncyclopediaItemDetailFragment f = new EncyclopediaItemDetailFragment(m_bf, result.id);
						((HomeScreen)m_act).setCurrentFragment(f, true);
					} else if (result.type.equalsIgnoreCase("Achievements")) {
						AchievementDetailFragment f = new AchievementDetailFragment(result.id, "Back");
						((HomeScreen)m_act).setCurrentFragment(f,  true);
					}
				}
			});
		}
		
		return convertView;
	}

}
