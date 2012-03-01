package com.tinyspeck.glitchhq;

import java.util.Vector;

import com.tinyspeck.glitchhq.BaseFragment.skillAvailable;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UnlearnableListAdapter extends BaseAdapter {

	private Vector<skillAvailable> m_unlearnableList;
	private HomeScreen m_act;
	private LayoutInflater m_inflater;
	private MyApplication m_application;
	
	public class ViewHolder {
		TextView  item;
		TextView  time;
		ImageView icon;
		ImageView status;
		View whole;
	};
	
	UnlearnableListAdapter(Activity act, Vector<skillAvailable> unlearnableList) 
    {
		m_act = (HomeScreen)act;
		m_unlearnableList = unlearnableList;
		m_inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_application = (MyApplication)act.getApplicationContext();
    }
	
	public int getCount()
    {
   	 if( m_unlearnableList == null )
   		 return 0;
   	 
   	 return m_unlearnableList.size();
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
		ViewHolder holder=null; 
		
		if( convertView != null)
			holder = (ViewHolder)convertView.getTag();

		if( holder == null )
		{			
			convertView = m_inflater.inflate( R.layout.skill_list_item, null);
			holder = new ViewHolder();
   		
			holder.whole = convertView.findViewById(R.id.skill_item);
			holder.item = (TextView)convertView.findViewById(R.id.skill_name);
			holder.time = (TextView)convertView.findViewById(R.id.skill_time);
			holder.icon = (ImageView)convertView.findViewById(R.id.skill_icon);
			holder.status = (ImageView)convertView.findViewById(R.id.skill_status);

			holder.item.setTypeface( m_application.m_vagFont );
			holder.time.setTypeface( m_application.m_vagLightFont );
           
           convertView.setTag(holder);
		}
		holder = (ViewHolder)convertView.getTag();

		if( position < getCount() )
		{
			skillAvailable skill = m_unlearnableList.get(position);
				
			//DrawableURL.Show( holder.icon, skill.icon, false);
			m_application.Download( skill.icon, holder.icon, MyApplication.DOWNLOAD_TYPE_NORMAL );
			
			holder.item.setText( skill.item );
			
			holder.time.setText( Util.TimeToString( skill.totalTime, false ) );
			boolean bShowStop = ( skill.remainTime > 0 );
			holder.status.setVisibility( bShowStop? View.VISIBLE: View.INVISIBLE );
		}
		holder.whole.setTag(position);
		holder.whole.setOnClickListener( new OnClickListener()
	    {
			public void onClick(View arg0) {
				
				int nItem = (Integer)arg0.getTag();

				skillAvailable skill = m_unlearnableList.get(nItem);
				SkillDetailFragment fm = new SkillDetailFragment(skill, true);
				m_act.setCurrentFragment(fm,true);
			}
	    });
		
      	return convertView;
	 }
}
