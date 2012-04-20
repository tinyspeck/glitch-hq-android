package com.tinyspeck.glitchhq;

import java.util.Vector;

import com.tinyspeck.glitchhq.BaseFragment.glitchFriend;


import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsListViewAdapter extends BaseAdapter implements Filterable {

	private Vector<glitchFriend> m_originalFriendsList;
	private Vector<glitchFriend> m_friendsList;
	private LayoutInflater m_inflater;
	private Activity m_act;
	private BaseFragment m_bf;
	private MyApplication m_application;
	private Filter m_filter;
	
	public class ViewHolder 
	{
		ImageView icon;
		TextView name;
		View divider;
		View whole;
	}
	
	public FriendsListViewAdapter(BaseFragment bf, Vector<glitchFriend> friendsList)
	{
		m_friendsList = friendsList;
		m_act = bf.getActivity();
		m_bf = bf;
		m_inflater = (LayoutInflater)m_act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_application = (MyApplication)m_act.getApplicationContext();
	}
	
	public int getCount()
	{
		if (m_friendsList == null)
			return 0;
		
		return m_friendsList.size();
	}
	
	public Object getItem(int position)
	{
		return position;		
	}
	
	public long getItemId(int position)
	{
		return position;
	}
	
	public Filter getFilter() {
		if (m_filter == null) {
			m_filter = new VectorFilter();
		}
		return m_filter;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		
		if (convertView != null)
			holder = (ViewHolder)convertView.getTag();
		
		if (holder == null) {
			convertView = m_inflater.inflate(R.layout.friend_item, null);
			holder = new ViewHolder();
			
			holder.name = (TextView)convertView.findViewById(R.id.friend_name);
			holder.icon = (ImageView)convertView.findViewById(R.id.icon_friend);
			holder.name.setTypeface(m_application.m_vagFont);
			holder.whole = (View)convertView.findViewById(R.id.friendfeed_item);
			holder.divider = (View)convertView.findViewById(R.id.friend_divider);
			convertView.setTag(holder);
		}
		holder = (ViewHolder)convertView.getTag();
		
		if (position < getCount())
		{
			glitchFriend friend = m_friendsList.get(position);
			holder.name.setText(friend.player_name);
			
			if (friend.avatar != null)
				DrawableURL.CropShow(holder.icon, friend.avatar);
			else
				BitmapUtil.CropShow(holder.icon, BitmapFactory.decodeResource(m_act.getResources(), R.drawable.wireframe));
			
			holder.divider.setVisibility(View.VISIBLE);
		}
		holder.whole.setTag(position);
		
		if (m_bf instanceof MailChooseRecipientFragment) {
			holder.whole.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					glitchFriend friend = m_friendsList.get((Integer)arg0.getTag());
		    		MailComposeFragment f = new MailComposeFragment(friend.player_name, friend.id);					
					((HomeScreen)m_act).setCurrentFragment(f, true);
				}
			});
		} else {
			holder.whole.setOnClickListener( new OnClickListener() {				
				public void onClick(View arg0) {
					glitchFriend friend = m_friendsList.get((Integer)arg0.getTag());
					ProfileFragment f = new ProfileFragment(m_bf, friend.id, true);
					((HomeScreen)m_act).setCurrentFragment(f, true);
				}
			});
		}
		
		return convertView;
	}
	
	private class VectorFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();
			
			if (m_originalFriendsList == null) {
				m_originalFriendsList = new Vector<glitchFriend>(m_friendsList);
			}
			
			if (prefix == null || prefix.length() == 0) {
				m_friendsList = m_originalFriendsList;
				
				results.values = m_friendsList;
				results.count = m_friendsList.size();
			} else {
				String prefixString = prefix.toString().toLowerCase();
				
				Vector<glitchFriend> newValues = new Vector<glitchFriend>();
				
				for (int i = 0; i < m_originalFriendsList.size(); i++) {
					glitchFriend value = m_originalFriendsList.get(i);
					String valueText = value.player_name.toLowerCase();
					
					if (valueText.contains(prefixString)) {
						newValues.add(value);
					}
				}
				results.values = newValues;
				results.count = newValues.size();
			}
			
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			m_friendsList = (Vector<glitchFriend>)results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
