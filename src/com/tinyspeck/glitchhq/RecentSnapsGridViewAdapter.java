package com.tinyspeck.glitchhq;

import java.util.Vector;

import com.tinyspeck.glitchhq.BaseFragment.glitchSnap;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class RecentSnapsGridViewAdapter extends BaseAdapter {

	private Vector<glitchSnap> m_snapsList;
	private LayoutInflater m_inflater;
	private BaseFragment m_bf;
	private Activity m_act;
	
	public class ViewHolder
	{
		ImageView image;
		View whole;
	}
	
	public RecentSnapsGridViewAdapter(BaseFragment bf, Vector<glitchSnap> snapsList)
	{
		m_snapsList = snapsList;
		m_act = bf.getActivity();
		m_bf = bf;	
		m_inflater = (LayoutInflater) m_act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() 
	{
		if (m_snapsList == null)
			return 0;
		return m_snapsList.size();
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
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (holder == null) {
			convertView = m_inflater.inflate(R.layout.recent_snap_grid_item, null);
			holder = new ViewHolder();
			
			holder.image = (ImageView) convertView.findViewById(R.id.recent_snap_image);
			holder.whole = (View) convertView.findViewById(R.id.recent_snap_item);
			convertView.setTag(holder);
			
			if (position < getCount()) {
				glitchSnap photo = m_snapsList.get(position);
				DrawableURL.Show(holder.image, photo.image, false);
				holder.whole.setTag(position);
				holder.whole.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						glitchSnap photo = m_snapsList.get((Integer)v.getTag());
						SnapDetailFragment f = new SnapDetailFragment(photo.who, photo.playerID, String.valueOf(photo.id), photo.secret, "Snaps");
						((HomeScreen)m_act).setCurrentFragment(f, true);
					}
				});
			}
		}			
		holder = (ViewHolder)convertView.getTag();
		
		return convertView;
	}

}
