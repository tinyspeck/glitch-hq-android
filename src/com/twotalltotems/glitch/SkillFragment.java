package com.twotalltotems.glitch;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.tinyspeck.android.GlitchRequest;
import com.twotalltotems.glitch.BaseFragment.skillAvailable;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class SkillFragment extends BaseFragment{

    private SkillListAdapter m_skillAdapter;
    private LinearListView  m_skillListView;
    
	private Timer m_RemainingTimer;
	private View m_root;
	
	private TextView m_learningSkillName;
	private TextView m_learningSkillTime;
	private View m_learningSkillProgress;
	
  	private Vector<skillAvailable> m_skillList;
  	private Vector<skillAvailable> m_learningList;
	
    public void onActivityCreated(Bundle savedInstanceState) {
    	
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) 
    {
    	View curView = ViewInit( inflater, R.layout.skill_view, container );
    	m_root = curView;
    	init( curView );
		return curView;
    }
    
	private void init( View root )
	{
		boolean bUpdateData = m_skillList == null;
		
   	    m_skillListView = (LinearListView)root.findViewById( R.id.skill_list );
   	 
   	    TextView tv = (TextView)root.findViewById(R.id.available_skills);
   	    tv.setTypeface(m_application.m_vagFont);
   	 
		m_skillAdapter = new SkillListAdapter();
		m_skillListView.setAdapter( m_skillAdapter );

		m_learningSkillName = (TextView)root.findViewById(R.id.tv_skillName);
		m_learningSkillTime = (TextView)root.findViewById(R.id.tv_skillTime);
		m_learningSkillProgress = (View)root.findViewById(R.id.learning_progress);
		
		m_learningSkillName.setTypeface( m_application.m_vagLightFont );
		m_learningSkillTime.setTypeface( m_application.m_vagLightFont );

		FrameLayout learningPanel = (FrameLayout) m_root.findViewById(R.id.skill_view_learning_panel);
		learningPanel.setOnClickListener( new OnClickListener() 
		{			
			public void onClick(View arg0) {
				skillAvailable skill = m_learningList.get(0);
				SkillDetailFragment fm = new SkillDetailFragment(skill);					
				((HomeScreen)getActivity()).setCurrentFragment(fm, true);
			}
			
		});
		
		if( bUpdateData )
		{
			m_learningList = new Vector<skillAvailable>();
			getSkills();
		} else {
			showSkillPage();
		}		
	}
	
	private void showSkillPage()
	{
		setLearningSkill();
        InitUpdateSkillRemainningTimer();
        updateSkillList();
	}
	
	public void getSkills()
	{
        GlitchRequest request1 = m_application.glitch.getRequest("skills.listLearning");
        request1.execute(this);

		GlitchRequest request2 = m_application.glitch.getRequest("skills.listAvailable");
        request2.execute(this);
        
        m_requestCount = 2;
		((HomeScreen)getActivity()).showSpinner(true);
	}
	
	private void updateSkillList()
	{
   		boolean bHas = m_skillList.size() > 0;
   		
   		m_root.findViewById( R.id.list_message ).setVisibility( bHas? View.GONE: View.VISIBLE );
   		m_skillListView.setVisibility( bHas? View.VISIBLE: View.GONE );
   		
   		if( bHas )
   			m_skillAdapter.notifyDataSetChanged();
	}

	private class SkillListAdapter extends BaseAdapter 
	{
	   	 public class ViewHolder {
	   		 TextView  item;
		     TextView  time;
		     ImageView icon;
		     ImageView status;
		     View whole;
	   	 };
		
	     public SkillListAdapter() 
	     {
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
    			LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			
        		convertView = inflater.inflate( R.layout.skill_list_item, null);
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
					SkillDetailFragment fm = new SkillDetailFragment( skill.id );
					((HomeScreen)getActivity()).setCurrentFragment(fm,true);
				}
    	    });
    		
           	return convertView;
    	 }
    }
	
	@Override
	public void onStop()
	{
	    if( m_RemainingTimer != null )
	    {
		   m_RemainingTimer.cancel();
		   m_RemainingTimer = null;
	    }
	    super.onStop();
	}

	@Override
	public void onRequestBack( String method, JSONObject response )
	{
		if( method == "skills.listLearning" )
		{
			if( m_learningList != null )
				m_learningList.clear();
			
			addToLearningList( m_learningList, response );
			setLearningSkill();
			
	        InitUpdateSkillRemainningTimer();
			onRequestComplete();
		}else
		if( method == "skills.listAvailable" )
    	{
       		m_skillList = new Vector<skillAvailable>();

       		JSONObject jItems = response.optJSONObject("skills");
       		
       		if( jItems != null )
       		{
	        		Iterator<String> it = jItems.keys(); 

	        		while( it.hasNext() )
	        		{	
	        			String sKey = it.next();
	        			JSONObject jobj = jItems.optJSONObject( sKey );
	        			skillAvailable skill = new skillAvailable();

	        			skill.remainTime = jobj.optInt("time_remaining");
	        			skill.totalTime = jobj.optInt("total_time");
	        			skill.icon = jobj.optString("icon_44");
	        			skill.item = jobj.optString("name");
	        			skill.description = jobj.optString("description");
	        			skill.id = sKey;
		        		skill.curTime = System.currentTimeMillis()/1000;
	        			m_skillList.add(skill);
	        		}
       		}
       		updateSkillList();
       		if ( m_skillList.size() == 0 ) {
       			// no skills to learn. change text
       			((TextView)m_root.findViewById( R.id.list_message )).setText("");
       		}
       		onRequestComplete();
    	}
	}

	void setLearningSkill()
	{
		skillAvailable skill = null;
		FrameLayout learningPanel = (FrameLayout) m_root.findViewById(R.id.skill_view_learning_panel);
		int wasVisible = learningPanel.getVisibility();
		
		if( m_learningList.size() > 0 )
			skill = m_learningList.get(0);
		
		if( skill != null )
		{			
			learningPanel.setVisibility(View.VISIBLE);			
			Util.showProgress( getActivity(), m_learningSkillProgress, m_learningSkillTime, m_learningList.get(0).remainTime, m_learningList.get(0).totalTime, m_learningList.get(0).curTime ); 
			m_learningSkillName.setText( m_learningList.get(0).item  );
			if (wasVisible == View.GONE) {
				Util.startScaleAnimation(learningPanel, 600);
			}			
		} else {
			learningPanel.setVisibility(View.GONE);
		}
	}

	private void InitUpdateSkillRemainningTimer()
	{
	   if( m_RemainingTimer != null )
		   m_RemainingTimer.cancel();
		   
	   m_RemainingTimer = new Timer();
	   m_RemainingTimer.scheduleAtFixedRate( new TimerTask(){
	       public void run()
		   {
		    	 getActivity().runOnUiThread(new Runnable(){
		    		 public void run(){
	        			 setLearningSkill();
		        		 /*
		        		 else if( m_learningAdapter!= null )
		    				 m_learningAdapter.notifyDataSetChanged(); */
			         }});
   	       }  
		}, 1000, 1000 ); 
	}
	
	protected boolean doesSupportRefresh()
	{
		return true;
	}

	protected void onRefresh()
	{
		getSkills();
	}
	
	protected void scrollToTop()
	{
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.SkillScrollView);
		sv.smoothScrollTo(0, 0);
	}

}
