package com.tinyspeck.glitchhq;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinyspeck.glitchhq.BaseFragment.glitchActivity;
import com.tinyspeck.glitchhq.Sidebar.sidebarItem;

public class SidebarListViewAdapter extends BaseAdapter {
	private Vector<sidebarItem> m_sbList;
	private LayoutInflater m_inflater;
	private HomeScreen m_act;
	private Sidebar m_sb;
	private MyApplication m_application;

	public class ViewHolder {
		TextView text;
		TextView badge;
		View divider;
		View whole;
	};

	public SidebarListViewAdapter(Sidebar sidebar,
			Vector<sidebarItem> actList) {
		m_sbList = actList;
		m_act = (HomeScreen) sidebar.getActivity();
		m_sb = sidebar;
		m_inflater = (LayoutInflater) m_act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_application = (MyApplication) m_act.getApplicationContext();
	}

	public int getCount() {
		if (m_sbList == null)
			return 0;

		return m_sbList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		final sidebarItem sbItem = m_sbList.get(position);

		if (convertView != null)
			holder = (ViewHolder) convertView.getTag();

		// Inflate views
		if (holder == null && sbItem != null) {
			
			if (sbItem.isHeader)
			{
				convertView = m_inflater.inflate(R.layout.sidebar_header, null);
				if (sbItem.isTop)
				{
					LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.actfeed_item);
					layout.setBackgroundResource(R.drawable.sidebar_header_gradient_top);
				}
					
			}
			else
				convertView = m_inflater.inflate(R.layout.sidebar_list_item, null);
			
			holder = new ViewHolder();

			holder.text = (TextView) convertView.findViewById(R.id.sidebar_item_text);
			holder.text.setTypeface(m_application.m_vagFont);
			holder.divider = (View) convertView.findViewById(R.id.list_diveider);								
			
			if (!sbItem.isHeader) {
				holder.badge = (TextView) convertView.findViewById(R.id.sidebar_item_badge);
				holder.badge.setTypeface(m_application.m_vagFont);
			}

			holder.whole = (View) convertView.findViewById(R.id.actfeed_item);
			// holder.description.setTypeface( m_application.m_vagFont );
			convertView.setTag(holder);
		}
		
		holder = (ViewHolder) convertView.getTag();

		// Update properties
		if (sbItem != null) {
			
			if (!sbItem.isHeader) {
				holder.badge.setVisibility(sbItem.badge > 0 ? View.VISIBLE : View.GONE);
				if (sbItem.badge > 0) {
					holder.badge.setText(String.valueOf(sbItem.badge));				
				} else if (sbItem.badge > 99) {
					holder.badge.setText("!!");
				} else {
					holder.badge.setText("0");
				}
			}
			
			holder.text.setText(sbItem.text);
			
			if (position == getCount() - 1)
				holder.divider.setVisibility(View.GONE);
			else if (!sbItem.isHeader)
				holder.divider.setVisibility(View.VISIBLE);
		}
		
		holder.whole.setTag(position);
		holder.whole.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				m_sb.finish();
				m_act.setSelectedPage(sbItem.page);
			}
		});
		return convertView;
	}
}
