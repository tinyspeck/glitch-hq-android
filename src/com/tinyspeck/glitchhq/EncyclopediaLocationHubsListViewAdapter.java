package com.tinyspeck.glitchhq;

import java.util.Vector;

import com.tinyspeck.glitchhq.BaseFragment.glitchLocationHub;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EncyclopediaLocationHubsListViewAdapter extends BaseAdapter {

	private Vector<glitchLocationHub> m_hubsList;
	private LayoutInflater m_inflater;
	private Activity m_act;
	private BaseFragment m_bf;
	private MyApplication m_application;
	
	public EncyclopediaLocationHubsListViewAdapter(BaseFragment bf, Vector<glitchLocationHub> hubsList)
	{
		m_hubsList = hubsList;
		m_act = bf.getActivity();
		m_bf = bf;
		m_inflater = (LayoutInflater) m_act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_application = (MyApplication) m_act.getApplicationContext();
	}
	
	public class ViewHolder
	{
		TextView name;
		View whole;
	}
	
	public int getCount() 
	{
		if (m_hubsList == null)
			return 0;
		return m_hubsList.size();
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
			convertView = m_inflater.inflate(R.layout.encyclopedia_location_hubs_list_item, null);
			holder = new ViewHolder();
			
			holder.name = (TextView)convertView.findViewById(R.id.encyclopedia_location_hub_name);
			holder.name.setTypeface(m_application.m_vagFont);
			holder.whole = (View)convertView.findViewById(R.id.encyclopedia_location_hub_item);
			convertView.setTag(holder);
		}
		holder = (ViewHolder)convertView.getTag();
		
		if (position < getCount()) {
			glitchLocationHub hub = m_hubsList.get(position);
			holder.name.setText(hub.name);
			holder.whole.setTag(position);
			holder.whole.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					glitchLocationHub hub = m_hubsList.get((Integer)v.getTag());
					EncyclopediaLocationStreetFragment f = new EncyclopediaLocationStreetFragment(hub);
					((HomeScreen)m_act).setCurrentFragment(f, true);
				}
			});
		}
		
		return convertView;
	}

}
