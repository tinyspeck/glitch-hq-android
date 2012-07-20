package com.tinyspeck.glitchhq;

import java.util.Vector;

import com.tinyspeck.glitchhq.BaseFragment.glitchSnapComment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SnapCommentsListAdapter extends BaseAdapter {
	
	private Vector<glitchSnapComment> m_commentsList;
	private LayoutInflater m_inflater;
	private Activity m_act;
	private MyApplication m_application;
	
	public class ViewHolder {
		TextView name;
		TextView comment;
		TextView time;
		View whole;
	}
	
	public SnapCommentsListAdapter(Activity act, Vector<glitchSnapComment> comments)
	{
		m_commentsList = comments;
		m_act = act;
		m_inflater = (LayoutInflater)m_act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_application = (MyApplication)m_act.getApplicationContext();
	}
	
	public int getCount() 
	{
		if (m_commentsList == null)
			return 0;
		return m_commentsList.size();
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
		
		if (convertView != null)
			holder = (ViewHolder)convertView.getTag();
	
		if (holder == null) {
			convertView = m_inflater.inflate(R.layout.snap_comments_list_item, null);
			holder = new ViewHolder();
			
			holder.name = (TextView)convertView.findViewById(R.id.snap_comment_name);
			holder.name.setTypeface(m_application.m_vagFont);  
			holder.comment = (TextView)convertView.findViewById(R.id.snap_comment_text);
			holder.time = (TextView)convertView.findViewById(R.id.snap_comment_time);
			holder.time.setTypeface(m_application.m_vagLightFont);
			convertView.setTag(holder);
		}
		holder = (ViewHolder)convertView.getTag();
		
		if (position < getCount()) {
			glitchSnapComment comment = m_commentsList.get(position);
			holder.name.setText(comment.who);
			holder.comment.setText(comment.what);
			if (!comment.when.equalsIgnoreCase("just now"))
				holder.time.setText(comment.when + " ago");
			else
				holder.time.setText(comment.when);			
		}
		
		return convertView;
	}

}
