package com.tinyspeck.glitchhq;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tinyspeck.glitchhq.BaseFragment.skillAvailable;


public class UnlearningListViewAdapter extends BaseAdapter {

	private Vector<skillAvailable> m_unlearningList;
	private LayoutInflater m_inflater;
	private Activity m_act;
    private MyApplication m_application;
    
    public class ViewHolder {
    	TextView  item;
    	TextView  time;
    	View 	  unlearningProgressText;
    	View 	  unlearningProgress;
	};
	
	public UnlearningListViewAdapter(Activity act, Vector<skillAvailable> unlearningList)
	{
		m_unlearningList = unlearningList;
		m_act = act;
		m_inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_application = (MyApplication)act.getApplicationContext();
	}
	
	public int getCount()
	{
		if (m_unlearningList == null)
			return 0;
		
		return m_unlearningList.size();
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
			convertView = m_inflater.inflate(R.layout.unlearning_list_item, null);
			holder = new ViewHolder();
			
			holder.item = (TextView)convertView.findViewById(R.id.tv_skillName);
			holder.time = (TextView)convertView.findViewById(R.id.tv_skillTime);
			holder.unlearningProgressText = (View)convertView.findViewById(R.id.unlearning_progress_text);
			holder.unlearningProgress = (View)convertView.findViewById(R.id.unlearning_progress);
			
			holder.item.setTypeface( m_application.m_vagLightFont );
            holder.time.setTypeface( m_application.m_vagLightFont );
            
            convertView.setTag(holder);
		}
		holder = (ViewHolder)convertView.getTag();
		
		if( position < getCount() )
		{
			int remainTime = m_unlearningList.get(position).remainTime;
			int totalTime = m_unlearningList.get(position).totalTime;
			long curTime = m_unlearningList.get(position).curTime;
			
			Util.showUnlearnProgress( m_act, holder.unlearningProgress, holder.time, remainTime, totalTime, curTime );   
			holder.item.setText( "Unlearning " + m_unlearningList.get(position).item );
		}		
		
       	return convertView;
	}
}
