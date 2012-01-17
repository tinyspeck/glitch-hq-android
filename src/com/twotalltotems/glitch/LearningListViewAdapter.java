package com.twotalltotems.glitch;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.twotalltotems.glitch.BaseFragment.skillLearning;

public class LearningListViewAdapter extends BaseAdapter 
{
	 private Vector<skillLearning> m_learningList;
 	 private LayoutInflater m_inflater;
 	 private Activity m_act;
     private MyApplication m_application;    
 	 
   	 public class ViewHolder {
   		 TextView  item;
	     TextView  time;
	     View 	   learningProgressText;
	     View 	   learningProgress;
   	 };
	
     public LearningListViewAdapter( Activity act, Vector<skillLearning> learningList  ) 
     {
    	 m_learningList = learningList;
    	 m_act = act;
    	 m_inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	     m_application = (MyApplication)act.getApplicationContext();
     }
     public int getCount()
     {
    	 if( m_learningList == null )
    		 return 0;
    	 
    	 return m_learningList.size();
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
    		convertView = m_inflater.inflate( R.layout.learning_list_item, null);
    		holder = new ViewHolder();

    		holder.item = (TextView)convertView.findViewById(R.id.tv_skillName);
            holder.time = (TextView)convertView.findViewById(R.id.tv_skillTime);
            holder.learningProgressText = (View)convertView.findViewById(R.id.learning_progress_text);
            holder.learningProgress = (View)convertView.findViewById(R.id.learning_progress);

            holder.item.setTypeface( m_application.m_vagLightFont );
            holder.time.setTypeface( m_application.m_vagLightFont );
            
            convertView.setTag(holder);
		}
		holder = (ViewHolder)convertView.getTag();

		if( position < getCount() )
		{
			int remainTime = m_learningList.get(position).remainTime;
			int totalTime = m_learningList.get(position).totalTime;
			long curTime = m_learningList.get(position).curTime;
			
			Util.showProgress( m_act, holder.learningProgress, holder.time, remainTime, totalTime, curTime );   
			holder.item.setText( m_learningList.get(position).item );
		}
       	return convertView;
	 }
}
