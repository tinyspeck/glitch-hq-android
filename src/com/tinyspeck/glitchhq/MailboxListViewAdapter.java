package com.tinyspeck.glitchhq;

import java.util.Vector;

import com.tinyspeck.glitchhq.BaseFragment.glitchMail;

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

public class MailboxListViewAdapter extends BaseAdapter {

	private Vector<glitchMail> m_mailList;
	private LayoutInflater m_inflater;
	private Activity m_act;
	private MyApplication m_application;
	
	public class ViewHolder
	{
		ImageView icon;
		TextView name;
		TextView text;
		TextView time;
		View divider;
		View whole;
	}
	
	public MailboxListViewAdapter(Activity act, Vector<glitchMail> mailList)
	{
		m_mailList = mailList;
		m_act = (HomeScreen) act;
		m_inflater = (LayoutInflater)m_act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_application = (MyApplication)m_act.getApplicationContext();
	}
	
	public int getCount() 
	{
		if (m_mailList == null)
			return 0;
		return m_mailList.size();
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
			convertView = m_inflater.inflate(R.layout.mailbox_list_item, null);
			holder = new ViewHolder();
			
			holder.name = (TextView)convertView.findViewById(R.id.inbox_message_name);
			holder.name.setTypeface(m_application.m_vagFont);
			// TODO: ellipsis 
			holder.text = (TextView)convertView.findViewById(R.id.inbox_message_text);
			holder.text.setTypeface(m_application.m_vagLightFont);
			
			holder.time = (TextView)convertView.findViewById(R.id.inbox_message_when);
			holder.time.setTypeface(m_application.m_vagLightFont);
			holder.icon = (ImageView)convertView.findViewById(R.id.icon_message_sender);
			holder.divider = (View)convertView.findViewById(R.id.inbox_message_divider);
			holder.whole = (View)convertView.findViewById(R.id.inbox_item);
			convertView.setTag(holder);
		}
		holder = (ViewHolder)convertView.getTag();
		
		if (position < getCount()) {
			glitchMail message = m_mailList.get(position);
			holder.name.setText(message.sender_label);
			if (message.sender_avatar != null && !message.sender_avatar.equals("")) {
				DrawableURL.CropShow(holder.icon, message.sender_avatar);
			} else {
				BitmapUtil.CropShow(holder.icon, BitmapFactory.decodeResource(m_act.getResources(), R.drawable.wireframe));
			}
			holder.text.setText(message.text);
			holder.time.setText(Util.TimeToString((int)(System.currentTimeMillis()/1000 - message.received)));
			if( position == getCount() - 1)
				holder.divider.setVisibility(View.GONE);
			else
				holder.divider.setVisibility(View.VISIBLE);			
		}
		holder.whole.setTag(position);
		holder.whole.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View arg0) {
				glitchMail currentMessage = m_mailList.get((Integer)arg0.getTag());
				MailboxDetailFragment fm = new MailboxDetailFragment(currentMessage.id);
				((HomeScreen)m_act).setCurrentFragment(fm, true);
			}
		});
		return convertView;
	}

}
