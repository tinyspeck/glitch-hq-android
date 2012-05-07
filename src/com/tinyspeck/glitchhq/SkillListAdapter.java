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

public class SkillListAdapter extends BaseAdapter {

	private Vector<skillAvailable> m_skillList;
	private HomeScreen m_act;
	private BaseFragment m_bf;
	private LayoutInflater m_inflater;
	private MyApplication m_application;
	
	public class ViewHolder {
		TextView  item;
		TextView  time;
		ImageView icon;
		ImageView status;
		View whole;
	};
   	
	public SkillListAdapter(BaseFragment bf, Vector<skillAvailable> skillList) 
    {
		m_bf = bf;
		m_act = (HomeScreen)bf.getActivity();
		m_skillList = skillList;
		m_inflater = (LayoutInflater)m_act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_application = (MyApplication)m_act.getApplicationContext();
    }
 
    public int getCount()
    {
   	 if( m_skillList == null )
   		 return 0;
   	 
   	 return m_skillList.size();
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
			skillAvailable skill = m_skillList.get(position);
				
			//DrawableURL.Show( holder.icon, skill.icon, false);
			m_application.Download( skill.icon, holder.icon, MyApplication.DOWNLOAD_TYPE_NORMAL );
			
			holder.item.setText( skill.item );
			holder.time.setText( Util.TimeToString( skill.remainTime, false ) );
			boolean bShowStop = ( skill.totalTime > skill.remainTime );
			holder.status.setVisibility( bShowStop? View.VISIBLE: View.INVISIBLE );
		}
		holder.whole.setTag(position);
		holder.whole.setOnClickListener( new OnClickListener()
	    {
			public void onClick(View arg0) {
				
				int nItem = (Integer)arg0.getTag();

				skillAvailable skill = m_skillList.get(nItem);
				SkillDetailFragment fm = new SkillDetailFragment(m_bf, skill);
				m_act.setCurrentFragment(fm,true);
				
			}
	    });
		
      	return convertView;
	 }
}
